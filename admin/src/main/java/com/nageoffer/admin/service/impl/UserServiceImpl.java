package com.nageoffer.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.admin.common.convention.exception.ClientException;
import com.nageoffer.admin.common.convention.exception.ServiceException;
import com.nageoffer.admin.common.enums.UserErrorCodeEnum;
import com.nageoffer.admin.dao.entity.UserDO;
import com.nageoffer.admin.dao.mapper.UserMapper;
import com.nageoffer.admin.dto.req.UserLoginReqDTO;
import com.nageoffer.admin.dto.req.UserRegisterReqDTO;
import com.nageoffer.admin.dto.req.UserUpdateReqDTO;
import com.nageoffer.admin.dto.resp.UserLoginRespDTO;
import com.nageoffer.admin.dto.resp.UserRespDTO;
import com.nageoffer.admin.service.GroupService;
import com.nageoffer.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.nageoffer.admin.common.constant.RedisCacheConstant.LOCK_USER_REGISTER_KEY;
import static com.nageoffer.admin.common.enums.UserErrorCodeEnum.*;

/**
 * 用户接口实现层
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;
    private final GroupService groupService;

    @Override
    public UserRespDTO getUserByUsername(String username){
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class).eq(UserDO::getUsername,username);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        UserRespDTO result = new UserRespDTO();
        if(userDO == null)
        {
            throw new ServiceException(UserErrorCodeEnum.USER_NULL);
        }
        org.springframework.beans.BeanUtils.copyProperties(userDO,result);
        return result;
    }

    @Override
    public Boolean hasUsername(String username){
        return userRegisterCachePenetrationBloomFilter.contains(username);
    }

    @Override
    public void register(UserRegisterReqDTO requestParam) {
        if(hasUsername(requestParam.getUsername())){//这里为什么能这么写？
            throw new ClientException(USER_NAME_EXIST);
        }
        RLock lock = redissonClient.getLock(LOCK_USER_REGISTER_KEY + requestParam.getUsername());
        try{
            if(lock.tryLock()) {
                try {
                    int inserted = baseMapper.insert(BeanUtil.toBean(requestParam, UserDO.class));//注意这里
                    if (inserted < 1) {
                        throw new ClientException(USER_INSERT_ERROR);
                    }
                }catch (DuplicateKeyException ex){
                    throw new ClientException(USER_EXIST);
                }
                userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
                groupService.saveGroup(requestParam.getUsername(),"默认分组");
                return;
            }
            throw new ClientException(USER_NAME_EXIST);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void update(UserUpdateReqDTO requestParam) {
        //TODO 验证当前用户名是否为登录用户 保证修改用户信息的人就是该用户
        LambdaUpdateWrapper<UserDO> updateWrapper = Wrappers.lambdaUpdate(UserDO.class).eq(UserDO::getUsername, requestParam.getUsername());
        baseMapper.update(BeanUtil.toBean(requestParam, UserDO.class),updateWrapper);
    }
    @Override
    public UserLoginRespDTO login(UserLoginReqDTO requestParam){
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername,requestParam.getUsername())
                .eq(UserDO::getPassword,requestParam.getPassword())
                .eq(UserDO::getDelFlag,0);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        if(userDO == null){
            throw new ClientException("用户不存在");
        }
        //改为了登录就返回token（方便配合公网短链接试用，让大家都能用同一个账号密码登录）
        Map<Object ,Object> hasLoginMap = stringRedisTemplate.opsForHash().entries("login_" + requestParam.getUsername());
        if (CollUtil.isNotEmpty(hasLoginMap)) {
            String token = hasLoginMap.keySet().stream()
                    .findFirst()
                    .map(Object::toString)
                    .orElseThrow(() -> new ClientException("用户登录错误"));
            return new UserLoginRespDTO(token);
        }
        /**
         * Hash
         * Key: login_username
         * Value:
         *  Key: token标识
         *  value: JSON字符串 用户信息
         */
        String uuid = UUID.randomUUID().toString();
        stringRedisTemplate.opsForHash().put("login_" + requestParam.getUsername(),uuid,JSON.toJSONString(userDO));
        //stringRedisTemplate.expire("login_" + requestParam.getUsername(),30L, TimeUnit.DAYS); //移除过期设置，不必重启后重新登录token
        return new UserLoginRespDTO(uuid);
    }

    @Override
    public Boolean checkLogin(String username,String token) {
        return stringRedisTemplate.opsForHash().get("login_"+username,token) != null;
    }

    @Override
    public void logout(String username, String token) {
        if(checkLogin(username,token)){
            stringRedisTemplate.delete("login_"+username);
            return;
        }
        throw new ClientException("用户Token不存在或用户未登录");
    }


}
