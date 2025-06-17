package com.gameApi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gameApi.entity.ResourceChannelEntity;
import com.gameApi.entity.ResourceDeviceWhiteEntity;
import com.gameApi.entity.ResourceVersionEntity;
import com.gameApi.entity.ResourceVersionInfoEntity;
import com.gameApi.exception.MyException;
import com.gameApi.mapper.ResourceChannelMapper;
import com.gameApi.mapper.ResourceDeviceWhiteMapper;
import com.gameApi.mapper.ResourceVersionInfoMapper;
import com.gameApi.mapper.ResourceVersionMapper;
import com.gameApi.request.VersionRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Service
public class VersionService {
    @Autowired
    private ResourceVersionInfoMapper resourceVersionInfoMapper;
    @Autowired
    private ResourceVersionMapper resourceVersionMapper;

    @Autowired
    private ResourceChannelMapper resourceChannelMapper;

    @Autowired
    private ResourceDeviceWhiteMapper resourceDeviceWhiteMapper;

    private static final String RESOURCE_VERSION_CACHE = "gm:version";
    private static final String RESOURCE_VERSION_LAST_CACHE = "gm:version:last";
    private static final String RESOURCE_CHANNEL_CACHE = "gm:version:channel";
    private static final String RESOURCE_DEVICE_WHITE_CACHE = "gm:version:device_white";

    private static final Integer NO_FOUND = 500;
    private static final Integer JSON_ERROR = 501;
    private static final Integer CHANNEL_ERROR = 502;
    private static final Integer NEED_UPDATE = 503;

    private Set<String> channelList = new CopyOnWriteArraySet<>();
    private Set<String> deviceList = new CopyOnWriteArraySet<>();
    private ConcurrentHashMap<String, ResourceVersionInfoEntity> versionMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> versionLastMap = new ConcurrentHashMap<>();

    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    @PostConstruct
    private void loadData () {
        reloadCache();
    }

    public void reloadCache () {
        channelList = redisTemplate.opsForSet().members(RESOURCE_CHANNEL_CACHE);
        deviceList = redisTemplate.opsForSet().members(RESOURCE_DEVICE_WHITE_CACHE);

        Map<Object, Object> list = redisTemplate.opsForHash().entries(RESOURCE_VERSION_CACHE);
        try {
            for (Map.Entry<Object, Object> entry : list.entrySet()) {
                String key = entry.getKey().toString();
                ResourceVersionInfoEntity versionInfo = objectMapper.readValue(entry.getValue().toString(), ResourceVersionInfoEntity.class);
                versionMap.put(key, versionInfo);
            }
        }catch (Exception e) {
            log.error("加载缓存数据失败");
        }

        Map<Object, Object> versionLastLists = redisTemplate.opsForHash().entries(RESOURCE_VERSION_LAST_CACHE);
        try {
            for (Map.Entry<Object, Object> entry : versionLastLists.entrySet()) {
                String key = entry.getKey().toString();
                versionLastMap.put(key, entry.getValue().toString());
            }
        }catch (Exception e) {
            log.error("加载缓存数据失败");
        }
    }

    public ResourceVersionInfoEntity version(VersionRequest body)
    {
        try {
            checkChannel(body.getChannel());
            ResourceVersionInfoEntity ret = checkClientVersion(body);
            if (ret != null) {
                log.info("responseId {}", ret.getId());
                return ret;
            }

            boolean isWhite = checkDeviceWhite(body.getDeviceNo());
            int status = isWhite ? 1 : 2;
            ResourceVersionInfoEntity resourceVersionInfoEntity = getDbEntity(body, status);
            if (resourceVersionInfoEntity == null) {
                throw new MyException("版本信息不存在", NO_FOUND);
            }

            log.info("responseId {}", resourceVersionInfoEntity.getId());
            return resourceVersionInfoEntity;
        } catch (MyException e) {
            ResourceVersionInfoEntity ret = new ResourceVersionInfoEntity();
            ret.setCode(e.getCode());
            return ret;
        }
    }

    private ResourceVersionInfoEntity getDbEntity (VersionRequest body, int status) {
        if (!versionMap.isEmpty()) {
            String key = body.getChannel() + "_" + body.getClientVersion() + "_prod";
            if (status == 1) {
                key = body.getChannel() + "_" + body.getClientVersion() + "_pre";
            }
            return versionMap.get(key);
        }

        if (redisTemplate.hasKey(RESOURCE_VERSION_CACHE)) {
            String key = body.getChannel() + "_" + body.getClientVersion() + "_prod";
            if (status == 1) {
                key = body.getChannel() + "_" + body.getClientVersion() + "_pre";
            }
            return getResourceFromCache(key);
        }

        ResourceVersionEntity resourceVersionEntity = getVersionEntity(body);
        LambdaQueryWrapper<ResourceVersionInfoEntity> lambdaQueryWrapper = new LambdaQueryWrapper<ResourceVersionInfoEntity>()
                .eq(ResourceVersionInfoEntity::getVersion_id, resourceVersionEntity.getId())
                .eq(ResourceVersionInfoEntity::getIs_del, 0)
                .eq(ResourceVersionInfoEntity::getStatus, status)
                .orderByDesc(ResourceVersionInfoEntity::getId)
                .last("LIMIT 1");
        return resourceVersionInfoMapper.selectOne(lambdaQueryWrapper);
    }

    private ResourceVersionEntity getVersionEntity (VersionRequest body) {
        LambdaQueryWrapper<ResourceVersionEntity> versionWrapper = new LambdaQueryWrapper<ResourceVersionEntity>();
        versionWrapper.eq(ResourceVersionEntity::getChannel, body.getChannel());
        versionWrapper.eq(ResourceVersionEntity::getClient_version, body.getClientVersion());
        versionWrapper.orderByDesc(ResourceVersionEntity::getId);
        versionWrapper.last("LIMIT 1");
        ResourceVersionEntity dbVersionEntity = resourceVersionMapper.selectOne(versionWrapper);

        return dbVersionEntity;
    }

    @Autowired
    @Resource(name = "primaryStringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    public ResourceVersionInfoEntity getResourceFromCache(String field) {
        Object json = redisTemplate.opsForHash().get(RESOURCE_VERSION_CACHE, field);
        if (json != null) {
            try {
                ResourceVersionInfoEntity versionInfo = objectMapper.readValue(json.toString(), ResourceVersionInfoEntity.class);
                return versionInfo;
            } catch (Exception e) {
                e.printStackTrace();
                throw new MyException("json解析失败", JSON_ERROR);
            }
        }

        return null;
    }

    public void checkChannel (String channel) {
        if (!channelList.isEmpty()) {
            if (!channelList.contains(channel)) {
                throw new MyException("渠道不存在", CHANNEL_ERROR);
            }

            return;
        }
        // 存在cache，从cache获取
        if (redisTemplate.hasKey(RESOURCE_CHANNEL_CACHE)) {
            if (Boolean.FALSE.equals(redisTemplate.opsForSet().isMember(RESOURCE_CHANNEL_CACHE, channel))) {
                throw new MyException("渠道不存在", CHANNEL_ERROR);
            }

            return;
        }

        LambdaQueryWrapper<ResourceChannelEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ResourceChannelEntity::getStatus, 1);
        lambdaQueryWrapper.eq(ResourceChannelEntity::getChannel_code, channel);
        ResourceChannelEntity dbEntity = resourceChannelMapper.selectOne(lambdaQueryWrapper);
        if (dbEntity == null) {
            throw new MyException("渠道不存在", CHANNEL_ERROR);
        }
    }

    public ResourceVersionInfoEntity checkClientVersion (VersionRequest body) {
        // 参数中的包版本比当前发布的版本低，则提示更新
        // 参数中的包版本比当前发布的版本高，则查询是否有包，没有提示无数据
        String dbVersion = getLastProdVersion(body);
        if (dbVersion == null) {
            throw new MyException("最新发布版本找不到", NO_FOUND);
        }
        int compareDbVersion = Integer.parseInt(dbVersion.replace("v", "").replace("V", "").replace(".", ""));
        int compareVersion = Integer.parseInt(body.getClientVersion().replace("v", "").replace("V", "").replace(".", ""));

        if (compareVersion < compareDbVersion) {
            throw new MyException("请更新版本", NEED_UPDATE);
        }
        if (compareVersion > compareDbVersion)  {
            // 先获取版本
            ResourceVersionEntity dbVersionEntity = getVersionEntity(body);
            if (dbVersionEntity == null) {
                throw new MyException("版本信息不存在", NO_FOUND);
            }
            // 获取当前的版本信息，最后一条
            LambdaQueryWrapper<ResourceVersionInfoEntity> resLambdaQueryWrapper = new LambdaQueryWrapper<>();
            resLambdaQueryWrapper.eq(ResourceVersionInfoEntity::getVersion_id, dbVersionEntity.getId());
            resLambdaQueryWrapper.eq(ResourceVersionInfoEntity::getIs_del, 0);
            resLambdaQueryWrapper.orderByDesc(ResourceVersionInfoEntity::getId);
            resLambdaQueryWrapper.last("LIMIT 1");
            ResourceVersionInfoEntity resDbEntity = resourceVersionInfoMapper.selectOne(resLambdaQueryWrapper);
            if (resDbEntity == null) {
                throw new MyException("无发布的版本", NO_FOUND);
            }
            return resDbEntity;
        }

        return null;
    }
    public Boolean checkDeviceWhite (String deviceNo) {
        if (!deviceList.isEmpty()) {
            return deviceList.contains(deviceNo);
        }

        // 存在cache，从cache获取
        if (redisTemplate.hasKey(RESOURCE_DEVICE_WHITE_CACHE)) {
            return redisTemplate.opsForSet().isMember(RESOURCE_DEVICE_WHITE_CACHE, deviceNo);
        }

        LambdaQueryWrapper<ResourceDeviceWhiteEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ResourceDeviceWhiteEntity::getStatus, 1);
        lambdaQueryWrapper.eq(ResourceDeviceWhiteEntity::getDevice_no, deviceNo);
        ResourceDeviceWhiteEntity dbEntity = resourceDeviceWhiteMapper.selectOne(lambdaQueryWrapper);
        return dbEntity != null;
    }

    private String getLastProdVersion (VersionRequest body) {
        if (!versionLastMap.isEmpty()) {
            return versionLastMap.get(body.getChannel());
        }
        if (redisTemplate.hasKey(RESOURCE_VERSION_LAST_CACHE)) {
            return (String) redisTemplate.opsForHash().get(RESOURCE_VERSION_LAST_CACHE, body.getChannel());
        }
        LambdaQueryWrapper<ResourceVersionEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ResourceVersionEntity::getChannel, body.getChannel());
        lambdaQueryWrapper.orderByDesc(ResourceVersionEntity::getId);
        List<ResourceVersionEntity> dbEntityList = resourceVersionMapper.selectList(lambdaQueryWrapper);
        if (dbEntityList.isEmpty()) {
            return null;
        }

        for (ResourceVersionEntity item : dbEntityList) {
            LambdaQueryWrapper<ResourceVersionInfoEntity> resLambdaQueryWrapper = new LambdaQueryWrapper<>();
            resLambdaQueryWrapper.eq(ResourceVersionInfoEntity::getVersion_id, item.getId());
            resLambdaQueryWrapper.eq(ResourceVersionInfoEntity::getIs_del, 0);
            resLambdaQueryWrapper.eq(ResourceVersionInfoEntity::getStatus, 2);
            resLambdaQueryWrapper.orderByDesc(ResourceVersionInfoEntity::getId);
            resLambdaQueryWrapper.last("LIMIT 1");
            ResourceVersionInfoEntity resDbEntityList = resourceVersionInfoMapper.selectOne(resLambdaQueryWrapper);
            if (resDbEntityList != null) {
                redisTemplate.opsForHash().put(RESOURCE_VERSION_LAST_CACHE, body.getChannel(), item.getClient_version());
            }
        }
        return (String) redisTemplate.opsForHash().get(RESOURCE_VERSION_LAST_CACHE, body.getChannel());
    }
}
