package com.nageoffer.project.mq.consumer;

import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.nageoffer.project.common.convention.exception.ServiceException;
import com.nageoffer.project.dao.entity.ShortLinkDO;
import com.nageoffer.project.dao.entity.ShortLinkGotoDO;
import com.nageoffer.project.dao.mapper.ShortLinkGotoMapper;
import com.nageoffer.project.dao.mapper.ShortLinkMapper;
import com.nageoffer.project.dto.biz.ShortLinkStatsRecordDTO;
import com.nageoffer.project.dto.req.ShortLinkCreateReqDTO;
import com.nageoffer.project.mq.idempotent.MessageQueueIdempotentHandler;
import com.nageoffer.project.tookit.LinkUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.redisson.api.RBloomFilter;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.nageoffer.project.common.constant.RedisKeyConstant.GOTO_SHORT_LINK_KEY;

@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = "short-link_producer-service-create",
        consumerGroup = "short-link_project-service-create-cg",
        consumeThreadMax = 32
)
public class ShortLinkCreateConsumer implements RocketMQListener<Map<String,String>> {
    private final RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter;
    private final MessageQueueIdempotentHandler messageQueueIdempotentHandler;
    private final ShortLinkMapper shortLinkMapper;
    private final ShortLinkGotoMapper shortLinkGotoMapper;
    private final StringRedisTemplate redis;
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
                String createShortLinkDefaultDomain = producerMap.get("CreateShortLinkDefaultDomain");
                String suffix = producerMap.get("suffix");
                ShortLinkCreateReqDTO requestParam = JSON.parseObject(producerMap.get("requestParam"),ShortLinkCreateReqDTO.class);
                actualCreateShortLink(createShortLinkDefaultDomain, suffix, requestParam);
            }
        } catch (Throwable ex) {
            // 删除幂等标识
            messageQueueIdempotentHandler.delMessageProcessed(id.toString());
            log.error("记录短链接监控消费异常", ex);
            throw ex;
        }
        messageQueueIdempotentHandler.setAccomplished(keys);
    }

    public void actualCreateShortLink(String createShortLinkDefaultDomain,String suffix,ShortLinkCreateReqDTO requestParam){
        String fullShortUrl = StrBuilder.create(createShortLinkDefaultDomain)
                .append("/")
                .append(suffix)
                .toString();
        ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                .domain(createShortLinkDefaultDomain)
                //.domain(requestParam.getDomain())
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .createdType(requestParam.getCreatedType())
                .validDateType(requestParam.getValidDateType())
                .validDate(requestParam.getValidDate())
                .describe(requestParam.getDescribe())
                .shortUrl(suffix)
                .enableStatus(0)
                .totalPv(0)
                .totalUv(0)
                .totalUip(0)
                .delTime(0L)
                .fullShortUrl(fullShortUrl)
                .favicon(requestParam.getOriginUrl())
                .build();
        ShortLinkGotoDO shortLinkGotoDO = ShortLinkGotoDO.builder()
                .fullShortUrl(fullShortUrl)
                .gid(requestParam.getGid())
                .build();
        try {
            //baseMapper.insert(shortLinkDO);
            shortLinkMapper.insert(shortLinkDO);
            shortLinkGotoMapper.insert(shortLinkGotoDO);//同时插入两个数据库，所以得增加事务方法
        } catch (DuplicateKeyException ex) {
            // TODO 已经误判得短链接如何处理
            //generatorSuffix里已经检查是否存在布隆过滤器里
            throw new ServiceException(String.format("短链接：{} 重复入库", fullShortUrl));

        }
        //        //缓存预热
        long timeout = LinkUtil.getLinkCacheValidDate(requestParam.getValidDate());
        System.out.println("设置的 timeout 值为（秒）: " + timeout * 0.001);
        redis.opsForValue()
                .set(String.format(GOTO_SHORT_LINK_KEY, fullShortUrl),//http开头
                        requestParam.getOriginUrl(), LinkUtil.getLinkCacheValidDate(requestParam.getValidDate()), TimeUnit.MILLISECONDS);
        shortUriCreateCachePenetrationBloomFilter.add(fullShortUrl);
    }
}
