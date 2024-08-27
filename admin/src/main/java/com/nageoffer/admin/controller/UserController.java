package com.nageoffer.admin.controller;

/**
 * 用户管理控制层
 */

import com.nageoffer.admin.common.convention.result.Result;
import com.nageoffer.admin.common.convention.result.Results;
import com.nageoffer.admin.dto.resp.UserRespDTO;
import com.nageoffer.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    /**
     * 根据用户名查询用户信息
     * @param username
     * @return
     */

    private final UserService userService;
    @GetMapping("/api/shortlink/v1/user/{username}")
    public Result<UserRespDTO> getUserByUserName(@PathVariable("username") String username){
//        UserRespDTO result = userService.getUserByUsername(username);
//        if(result == null){
//            return new Result<UserRespDTO>().setCode(UserErrorCodeEnum.USER_NULL.code()).setMessage(UserErrorCodeEnum.USER_NULL.message());
//        }else {
//            return new Result<UserRespDTO>().setCode("0").setData(result);
//        }
        System.out.println("test2");
        return Results.success(userService.getUserByUsername(username));//优化后的代码 为什么这么写
    }
}
