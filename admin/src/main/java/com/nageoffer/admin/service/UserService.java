package com.nageoffer.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nageoffer.admin.dao.entity.UserDO;
import com.nageoffer.admin.dto.req.UserLoginReqDTO;
import com.nageoffer.admin.dto.req.UserRegisterReqDTO;
import com.nageoffer.admin.dto.req.UserUpdateReqDTO;
import com.nageoffer.admin.dto.resp.UserLoginRespDTO;
import com.nageoffer.admin.dto.resp.UserRespDTO;

public interface UserService extends IService<UserDO> {
    /**
     * 根据用户名查询用户星系
     * @param username
     * @return
     */
    UserRespDTO getUserByUsername(String username);

    Boolean hasUsername(String username);

    /**
     * 注册用户
     * @param requestParam 注册用户请求参数
     */
    void register(UserRegisterReqDTO requestParam);

    /**
     * 更新用户
     * @param requestParam
     */
    void update(UserUpdateReqDTO requestParam);

    /**
     * 用户登录
     * @param requestParam 用户登录请求参数
     * @return 用户登录返回参数
     */
    UserLoginRespDTO login(UserLoginReqDTO requestParam);

    /**
     * 检查用户是否登录
     * @param username
     * @param token 用户登录token
     * @return
     */
    Boolean checkLogin(String username,String token);

    /**
     * 退出登录
     * @param username
     * @param token
     */
    void logout(String username, String token);
}
