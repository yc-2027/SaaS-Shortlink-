package com.nageoffer.project.mq.consumer;

/**
 * 短链接监控状态保存消息队列消费者
 * 公众号：马丁玩编程，回复：加群，添加马哥微信（备注：link）获取项目资料
 */
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class ShortLinkStatsSaveConsumer implements StreamListener<String, MapRecord<String, String, String>> {
//
//    private final ShortLinkMapper shortLinkMapper;
//    private final ShortLinkGotoMapper shortLinkGotoMapper;
//    private final RedissonClient redissonClient;
//    private final LinkAccessStatsMapper linkAccessStatsMapper;
//    private final LinkLocaleStatsMapper linkLocaleStatsMapper;
//    private final LinkOsStatsMapper linkOsStatsMapper;
//    private final LinkBrowserStatsMapper linkBrowserStatsMapper;
//    private final LinkAccessLogsMapper linkAccessLogsMapper;
//    private final LinkDeviceStatsMapper linkDeviceStatsMapper;
//    private final LinkNetworkStatsMapper linkNetworkStatsMapper;
//    private final LinkStatsTodayMapper linkStatsTodayMapper;
//    private final DelayShortLinkStatsProducer delayShortLinkStatsProducer;
//    private final StringRedisTemplate stringRedisTemplate;
//    private final MessageQueueIdempotentHandler messageQueueIdempotentHandler;
//
//
//
//
//    @Override
//    public void onMessage(MapRecord<String, String, String> message) {//实现了listenner
//        String stream = message.getStream();
//        RecordId id = message.getId();
//        //判断当前的这个消息是否被消费过
//        if (messageQueueIdempotentHandler.isMessageProcessed(id.toString())) {
//            // 判断当前的这个消息流程是否执行完成
//            if (messageQueueIdempotentHandler.isAccomplished(id.toString())) {
//                return;
//            }
//            throw new ServiceException("消息未完成流程，需要消息队列重试");
//        }
//        try {
//            Map<String, String> producerMap = message.getValue();
//            String fullShortUrl = producerMap.get("fullShortUrl");
//            if (StrUtil.isNotBlank(fullShortUrl)) {
//                String gid = producerMap.get("gid");
//                ShortLinkStatsRecordDTO statsRecord = JSON.parseObject(producerMap.get("statsRecord"), ShortLinkStatsRecordDTO.class);
//                actualSaveShortLinkStats(fullShortUrl, gid, statsRecord);
//            }
//            stringRedisTemplate.opsForStream().delete(Objects.requireNonNull(stream), id.getValue());
//        } catch (Throwable ex) {
//            // 某某某情况宕机了
//            messageQueueIdempotentHandler.delMessageProcessed(id.toString());
//            log.error("记录短链接监控消费异常", ex);
//        }
//        messageQueueIdempotentHandler.setAccomplished(id.toString());
//    }
//
//    public void actualSaveShortLinkStats(String fullShortUrl, String gid, ShortLinkStatsRecordDTO statsRecord) {
//        fullShortUrl = Optional.ofNullable(fullShortUrl).orElse(statsRecord.getFullShortUrl());
//        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(String.format(LOCK_GID_UPDATE_KEY, fullShortUrl));
//        RLock rLock = readWriteLock.readLock();
//        if (!rLock.tryLock()) {
//            //获取不了锁直接传入redis延迟队列里 过一段时间再重试
//            delayShortLinkStatsProducer.send(statsRecord);
//            return;
//        }
//        try {
//            if (StrUtil.isBlank(gid)) {
//                LambdaQueryWrapper<ShortLinkGotoDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
//                        .eq(ShortLinkGotoDO::getFullShortUrl, fullShortUrl);
//                ShortLinkGotoDO shortLinkGotoDO = shortLinkGotoMapper.selectOne(queryWrapper);
//                gid = shortLinkGotoDO.getGid();
//            }
//            int hour = DateUtil.hour(new Date(), true);
//            Week week = DateUtil.dayOfWeekEnum(new Date());
//            int weekValue = week.getIso8601Value();
//            LinkAccessStatsDO linkAccessStatsDO = LinkAccessStatsDO.builder()
//                    .pv(1)
//                    .uv(statsRecord.getUvFirstFlag() ? 1 : 0)
//                    .uip(statsRecord.getUipFirstFlag() ? 1 : 0)
//                    .hour(hour)
//                    .weekday(weekValue)
//                    .fullShortUrl(fullShortUrl)
//                    .gid(gid)
//                    .date(new Date())
//                    .build();
//            linkAccessStatsMapper.shortLinkStats(linkAccessStatsDO);
//            String localeResultStr = HttpUtil.get(IP_API_REMOTE_URL);
//            JSONObject localeResultObj = JSON.parseObject(localeResultStr);
//            String infoCode = localeResultObj.getString("infocode");
//            String actualRegionName = "未知";
//            String actualCity = "未知";
//            String actualCountry = "未知";
//            //todo:需要修改
//            if (StrUtil.isNotBlank(infoCode) && StrUtil.equals(infoCode, "10000")) {
//                String regionName = localeResultObj.getString("regionName");
//                boolean unknownFlag = StrUtil.equals(regionName, "[]");
//                LinkLocaleStatsDO linkLocaleStatsDO = LinkLocaleStatsDO.builder()
//                        .regionName(actualRegionName = unknownFlag ? actualRegionName : regionName)
//                        .city(actualCity = unknownFlag ? actualCity : localeResultObj.getString("city"))
//                        .zip(unknownFlag ? "未知" : localeResultObj.getString("zip"))
//                        .cnt(1)
//                        .fullShortUrl(fullShortUrl)
//                        .country(actualCity = unknownFlag ? actualCity : localeResultObj.getString("country"))
//                        .gid(gid)
//                        .date(new Date())
//                        .build();
//                linkLocaleStatsMapper.shortLinkLocaleStats(linkLocaleStatsDO);
//            }
//            LinkOsStatsDO linkOsStatsDO = LinkOsStatsDO.builder()
//                    .os(statsRecord.getOs())
//                    .cnt(1)
//                    .gid(gid)
//                    .fullShortUrl(fullShortUrl)
//                    .date(new Date())
//                    .build();
//            linkOsStatsMapper.shortLinkOsState(linkOsStatsDO);
//            LinkBrowserStatsDO linkBrowserStatsDO = LinkBrowserStatsDO.builder()
//                    .browser(statsRecord.getBrowser())
//                    .cnt(1)
//                    .gid(gid)
//                    .fullShortUrl(fullShortUrl)
//                    .date(new Date())
//                    .build();
//            linkBrowserStatsMapper.shortLinkBrowserState(linkBrowserStatsDO);
//            LinkDeviceStatsDO linkDeviceStatsDO = LinkDeviceStatsDO.builder()
//                    .device(statsRecord.getDevice())
//                    .cnt(1)
//                    .gid(gid)
//                    .fullShortUrl(fullShortUrl)
//                    .date(new Date())
//                    .build();
//            linkDeviceStatsMapper.shortLinkDeviceState(linkDeviceStatsDO);
//            LinkNetworkStatsDO linkNetworkStatsDO = LinkNetworkStatsDO.builder()
//                    .network(statsRecord.getNetwork())
//                    .cnt(1)
//                    .gid(gid)
//                    .fullShortUrl(fullShortUrl)
//                    .date(new Date())
//                    .build();
//            linkNetworkStatsMapper.shortLinkNetworkState(linkNetworkStatsDO);
//            LinkAccessLogsDO linkAccessLogsDO = LinkAccessLogsDO.builder()
//                    .user(statsRecord.getUv())
//                    .ip(statsRecord.getRemoteAddr())
//                    .browser(statsRecord.getBrowser())
//                    .os(statsRecord.getOs())
//                    .network(statsRecord.getNetwork())
//                    .device(statsRecord.getDevice())
//                    .locale(StrUtil.join("-", "中国", actualRegionName, actualCity))
//                    .gid(gid)
//                    .fullShortUrl(fullShortUrl)
//                    .build();
//            linkAccessLogsMapper.insert(linkAccessLogsDO);
//            shortLinkMapper.incrementStats(gid, fullShortUrl, 1, statsRecord.getUvFirstFlag() ? 1 : 0, statsRecord.getUipFirstFlag() ? 1 : 0);
//            LinkStatsTodayDO linkStatsTodayDO = LinkStatsTodayDO.builder()
//                    .todayPv(1)
//                    .todayUv(statsRecord.getUvFirstFlag() ? 1 : 0)
//                    .todayUip(statsRecord.getUipFirstFlag() ? 1 : 0)
//                    .gid(gid)
//                    .fullShortUrl(fullShortUrl)
//                    .date(new Date())
//                    .build();
//            linkStatsTodayMapper.shortLinkTodayState(linkStatsTodayDO);
//        } catch (Throwable ex) {
//            log.error("短链接访问量统计异常", ex);
//        } finally {
//            rLock.unlock();
//        }
//    }
//}

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Week;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.nageoffer.project.common.convention.exception.ServiceException;
import com.nageoffer.project.dao.entity.*;
import com.nageoffer.project.dao.mapper.*;
import com.nageoffer.project.dto.biz.ShortLinkStatsRecordDTO;
import com.nageoffer.project.mq.idempotent.MessageQueueIdempotentHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import static com.nageoffer.project.common.constant.RedisKeyConstant.LOCK_GID_UPDATE_KEY;
import static com.nageoffer.project.common.constant.ShortLinkConstant.IP_API_REMOTE_URL;

/**
 * 短链接监控状态保存消息队列消费者
 * 公众号：马丁玩编程，回复：加群，添加马哥微信（备注：link）获取项目资料
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = "${rocketmq.producer.topic}",
        consumerGroup = "short-link_project-service_stats-save_cg"
)
public class ShortLinkStatsSaveConsumer implements RocketMQListener<Map<String, String>> {

    private final ShortLinkMapper shortLinkMapper;
    private final ShortLinkGotoMapper shortLinkGotoMapper;
    private final RedissonClient redissonClient;
    private final LinkAccessStatsMapper linkAccessStatsMapper;
    private final LinkLocaleStatsMapper linkLocaleStatsMapper;
    private final LinkOsStatsMapper linkOsStatsMapper;
    private final LinkBrowserStatsMapper linkBrowserStatsMapper;
    private final LinkAccessLogsMapper linkAccessLogsMapper;
    private final LinkDeviceStatsMapper linkDeviceStatsMapper;
    private final LinkNetworkStatsMapper linkNetworkStatsMapper;
    private final LinkStatsTodayMapper linkStatsTodayMapper;
    private final MessageQueueIdempotentHandler messageQueueIdempotentHandler;


    @Override
    public void onMessage(Map<String, String> producerMap) {
        String keys = producerMap.get("keys");
        String id = producerMap.get("id");
        if (messageQueueIdempotentHandler.isMessageProcessed(keys)) {
            // 判断当前的这个消息流程是否执行完成
            if (messageQueueIdempotentHandler.isAccomplished(keys)) {
                return;
            }
            throw new ServiceException("消息未完成流程，需要消息队列重试");
        }
        try {
            String fullShortUrl = producerMap.get("fullShortUrl");
            if (StrUtil.isNotBlank(fullShortUrl)) {
                String gid = producerMap.get("gid");
                ShortLinkStatsRecordDTO statsRecord = JSON.parseObject(producerMap.get("statsRecord"), ShortLinkStatsRecordDTO.class);
                actualSaveShortLinkStats(fullShortUrl, gid, statsRecord);
            }
        } catch (Throwable ex) {
            // 删除幂等标识
            messageQueueIdempotentHandler.delMessageProcessed(id.toString());
            log.error("记录短链接监控消费异常", ex);
            throw ex;
        }
        messageQueueIdempotentHandler.setAccomplished(keys);
    }

    public void actualSaveShortLinkStats(String fullShortUrl, String gid, ShortLinkStatsRecordDTO statsRecord) {
        fullShortUrl = Optional.ofNullable(fullShortUrl).orElse(statsRecord.getFullShortUrl());
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(String.format(LOCK_GID_UPDATE_KEY, fullShortUrl));
        RLock rLock = readWriteLock.readLock();
        rLock.lock();
        try {
            if (StrUtil.isBlank(gid)) {
                LambdaQueryWrapper<ShortLinkGotoDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                        .eq(ShortLinkGotoDO::getFullShortUrl, fullShortUrl);
                ShortLinkGotoDO shortLinkGotoDO = shortLinkGotoMapper.selectOne(queryWrapper);
                gid = shortLinkGotoDO.getGid();
            }
            int hour = DateUtil.hour(new Date(), true);
            Week week = DateUtil.dayOfWeekEnum(new Date());
            int weekValue = week.getIso8601Value();
            LinkAccessStatsDO linkAccessStatsDO = LinkAccessStatsDO.builder()
                    .pv(1)
                    .uv(statsRecord.getUvFirstFlag() ? 1 : 0)
                    .uip(statsRecord.getUipFirstFlag() ? 1 : 0)
                    .hour(hour)
                    .weekday(weekValue)
                    .fullShortUrl(fullShortUrl)
                    .gid(gid)
                    .date(new Date())
                    .build();
            linkAccessStatsMapper.shortLinkStats(linkAccessStatsDO);
            String localeResultStr = HttpUtil.get(IP_API_REMOTE_URL);
            JSONObject localeResultObj = JSON.parseObject(localeResultStr);
            String infoCode = localeResultObj.getString("infocode");
            String actualRegionName = "未知";
            String actualCity = "未知";
            String actualCountry = "未知";
            if (StrUtil.isNotBlank(infoCode) && StrUtil.equals(infoCode, "10000")) {
                String regionName = localeResultObj.getString("regionName");
                boolean unknownFlag = StrUtil.equals(regionName, "[]");
                LinkLocaleStatsDO linkLocaleStatsDO = LinkLocaleStatsDO.builder()
                        .regionName(actualRegionName = unknownFlag ? actualRegionName : regionName)
                        .city(actualCity = unknownFlag ? actualCity : localeResultObj.getString("city"))
                        .zip(unknownFlag ? "未知" : localeResultObj.getString("zip"))
                        .cnt(1)
                        .fullShortUrl(fullShortUrl)
                        .country(actualCity = unknownFlag ? actualCity : localeResultObj.getString("country"))
                        .gid(gid)
                        .date(new Date())
                        .build();
                linkLocaleStatsMapper.shortLinkLocaleStats(linkLocaleStatsDO);
            }
            LinkOsStatsDO linkOsStatsDO = LinkOsStatsDO.builder()
                    .os(statsRecord.getOs())
                    .cnt(1)
                    .gid(gid)
                    .fullShortUrl(fullShortUrl)
                    .date(new Date())
                    .build();
            linkOsStatsMapper.shortLinkOsState(linkOsStatsDO);
            LinkBrowserStatsDO linkBrowserStatsDO = LinkBrowserStatsDO.builder()
                    .browser(statsRecord.getBrowser())
                    .cnt(1)
                    .gid(gid)
                    .fullShortUrl(fullShortUrl)
                    .date(new Date())
                    .build();
            linkBrowserStatsMapper.shortLinkBrowserState(linkBrowserStatsDO);
            LinkDeviceStatsDO linkDeviceStatsDO = LinkDeviceStatsDO.builder()
                    .device(statsRecord.getDevice())
                    .cnt(1)
                    .gid(gid)
                    .fullShortUrl(fullShortUrl)
                    .date(new Date())
                    .build();
            linkDeviceStatsMapper.shortLinkDeviceState(linkDeviceStatsDO);
            LinkNetworkStatsDO linkNetworkStatsDO = LinkNetworkStatsDO.builder()
                    .network(statsRecord.getNetwork())
                    .cnt(1)
                    .gid(gid)
                    .fullShortUrl(fullShortUrl)
                    .date(new Date())
                    .build();
            linkNetworkStatsMapper.shortLinkNetworkState(linkNetworkStatsDO);
            LinkAccessLogsDO linkAccessLogsDO = LinkAccessLogsDO.builder()
                    .user(statsRecord.getUv())
                    .ip(statsRecord.getRemoteAddr())
                    .browser(statsRecord.getBrowser())
                    .os(statsRecord.getOs())
                    .network(statsRecord.getNetwork())
                    .device(statsRecord.getDevice())
                    .locale(StrUtil.join("-", "中国", actualRegionName, actualCity))
//                    .gid(gid)
                    .fullShortUrl(fullShortUrl)
                    .build();
            linkAccessLogsMapper.insert(linkAccessLogsDO);
            shortLinkMapper.incrementStats(gid, fullShortUrl, 1, statsRecord.getUvFirstFlag() ? 1 : 0, statsRecord.getUipFirstFlag() ? 1 : 0);
            LinkStatsTodayDO linkStatsTodayDO = LinkStatsTodayDO.builder()
                    .todayPv(1)
                    .todayUv(statsRecord.getUvFirstFlag() ? 1 : 0)
                    .todayUip(statsRecord.getUipFirstFlag() ? 1 : 0)
//                    .gid(gid)
                    .fullShortUrl(fullShortUrl)
                    .date(new Date())
                    .build();
            linkStatsTodayMapper.shortLinkTodayState(linkStatsTodayDO);
        } catch (Throwable ex) {
            log.error("短链接访问量统计异常", ex);
        } finally {
            rLock.unlock();
        }
    }
}