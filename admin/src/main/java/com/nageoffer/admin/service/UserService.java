package com.nageoffer.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nageoffer.admin.dao.entity.UserDO;
import com.nageoffer.admin.dto.resp.UserRespDTO;

public interface UserService extends IService<UserDO> {
    /**
     * 根据用户名查询用户星系
     * @param username
     * @return
     */
    UserRespDTO getUserByUsername(String username);
}
