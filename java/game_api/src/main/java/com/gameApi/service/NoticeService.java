package com.gameApi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gameApi.common.IntegerToDateUtil;
import com.gameApi.entity.OutNoticeEntity;
import com.gameApi.mapper.OutNoticeMapper;
import com.gameApi.request.NoticeRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NoticeService {
    @PostConstruct
    public void init () {
        reloadRulesFromDB();
    }

    @Autowired
    private OutNoticeMapper outNoticeMapper;

    private static final AtomicReference<Map<String, NavigableMap<Integer, List<OutNoticeEntity>>>> ruleHolder =
            new AtomicReference<>(new HashMap<>());

    public List<OutNoticeEntity> get(NoticeRequest param) {
        String channel = param.getChannel().trim();

        Map<String, NavigableMap<Integer, List<OutNoticeEntity>>> currentMap = ruleHolder.get();
        NavigableMap<Integer, List<OutNoticeEntity>> timeMap = currentMap.getOrDefault(channel, new TreeMap<>());
        if (timeMap.isEmpty()) return Collections.emptyList();

        int currentTime = IntegerToDateUtil.getNowSecond();
        List<OutNoticeEntity> result = timeMap.headMap(currentTime, true)
                .values()
                .stream()
                .flatMap(Collection::stream)
                .filter(rule -> currentTime <= IntegerToDateUtil.localDateTimeToSecond(rule.getEnd()))
                .sorted(Comparator.comparing(OutNoticeEntity::getSort, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());

        return result;
    }

    public void reloadRulesFromDB() {
        // 模拟从数据库加载
        List<OutNoticeEntity> dbRules = loadRules(); // 查询数据库返回 List<Rule>
        // 构建新的 Map
        Map<String, NavigableMap<Integer, List<OutNoticeEntity>>> newRuleMap = new HashMap<>();

        for (OutNoticeEntity rule : dbRules) {
            int startSecond = IntegerToDateUtil.localDateTimeToSecond(rule.getStart());

            // 按逗号分割 server 字符串
            String[] servers = rule.getServer().split(",");

            for (String server : servers) {
                server = server.trim(); // 去除空格，防止 "s1, s2" 这种写法出错
                if (server.isEmpty()) continue;

                newRuleMap
                        .computeIfAbsent(server, c -> new TreeMap<>())
                        .computeIfAbsent(startSecond, s -> new ArrayList<>())
                        .add(rule);
            }
        }

        // 原子替换内存数据（瞬间完成，线程安全）
        ruleHolder.set(newRuleMap);
        log.info("Notice rules reloaded. {}", newRuleMap);
    }

    private List<OutNoticeEntity> loadRules() {
        Date now = new Date();
        LambdaQueryWrapper<OutNoticeEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OutNoticeEntity::getStatus, 2);
        lambdaQueryWrapper.lt(OutNoticeEntity::getStart, now);
        lambdaQueryWrapper.gt(OutNoticeEntity::getEnd, now);
        lambdaQueryWrapper.orderByDesc(OutNoticeEntity::getSort);
        lambdaQueryWrapper.orderByDesc(OutNoticeEntity::getId);

        // 模拟从数据库加载
        return outNoticeMapper.selectList(lambdaQueryWrapper);
    }

}
