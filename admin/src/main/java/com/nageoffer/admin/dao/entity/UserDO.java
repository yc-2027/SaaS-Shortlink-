package com.nageoffer.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nageoffer.admin.common.database.BaseDo;
import lombok.Data;

@Data
@TableName("t_user")
public class UserDO extends BaseDo {
    /**
     * @description t_user
     * @author zhengkai.blog.csdn.net
     * @date 2024-08-10
     */



        /**
         * id
         */
        private Long id;

        /**
         * 用户名
         */
        private String username;

        /**
         * 密码
         */
        private String password;

        /**
         * 真实姓名
         */
        private String realName;

        /**
         * 手机号
         */
        private String phone;

        /**
         * 邮箱
         */
        private String mail;

        /**
         * 注销时间戳
         */
        private Long deletionTime;




}
