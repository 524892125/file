package com.gameApi.service;

import com.gameApi.entity.GmToolsReportEntity;
import com.gameApi.exception.MyException;
import com.gameApi.mapper.GmToolsReportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class IndexService {
    @Autowired
    private GmToolsReportMapper gmToolsReportMapper;

    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    @Resource(name = "primaryStringRedisTemplate")
    private StringRedisTemplate primaryStringRedisTemplate;

    public Object report (Map<String, Object> body) {
        if (body.isEmpty()) {
            throw new MyException("参数错误", 500);
        }
        if (body.get("uid") == null || body.get("uid").toString().isEmpty()) {
            throw new MyException("缺少uid", 501);
        }
        if (body.get("report_uid") == null || body.get("report_uid").toString().isEmpty()) {
            throw new MyException("缺少report_uid", 502);
        }
        if (body.get("type") == null || body.get("type").toString().isEmpty()) {
            throw new MyException("缺少type", 503);
        }
        if (body.get("content") == null || body.get("content").toString().isEmpty()) {
            throw new MyException("缺少content", 504);
        }

        setStringRedisTemplate();
//      Redis Key 示例：user:data:{uid}
        String key = "gm:user:report:" + body.get("uid") + ":" + body.get("report_uid");
        if (stringRedisTemplate.hasKey(key)) {
            // 获取剩余过期时间（单位：秒）
            Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);

            // 当前时间增加秒
            LocalDateTime localDateTime = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            localDateTime = localDateTime.plusSeconds(expire);
            String dateStr = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            throw new MyException("请在 " + dateStr + " 后再尝试提交", 501);
        }

        // 设置 value 和 1 小时过期时间（60 分钟）
        stringRedisTemplate.opsForValue().set(key, "1", Duration.ofHours(1));

        GmToolsReportEntity reportEntity = new GmToolsReportEntity();
        reportEntity.setUid(Long.parseLong(body.get("uid").toString()));
        reportEntity.setReport_uid(Long.parseLong(body.get("report_uid").toString()));
        reportEntity.setType(body.get("type").toString());
        reportEntity.setContent(body.get("content").toString());
        reportEntity.setCreated_time(new Date());
        if (body.get("remark") != null) {
            reportEntity.setRemark(body.get("remark").toString());
        }
        reportEntity.setStatus(0);


        gmToolsReportMapper.insert(reportEntity);
        throw new MyException("举报成功", 200);
    }

    public void setStringRedisTemplate () {
        stringRedisTemplate = primaryStringRedisTemplate;
    }
}
