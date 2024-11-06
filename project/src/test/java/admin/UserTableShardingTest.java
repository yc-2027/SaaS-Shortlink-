/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package admin;

public class UserTableShardingTest {

    public static final String SQL = "create table t_link_%d\n" +
            "(\n" +
            "    id              bigint auto_increment comment 'ID'\n" +
            "        primary key,\n" +
            "    domain          varchar(128)                   null comment '域名',\n" +
            "    short_url       varchar(8) collate utf8mb3_bin null,\n" +
            "    full_short_url  varchar(128)                   null comment '完整短链接',\n" +
            "    origin_url      varchar(1024)                  null comment '原始链接',\n" +
            "    click_num       int default 0                  null comment '点击量',\n" +
            "    gid             varchar(32)                    null comment '分组标识',\n" +
            "    enable_status   tinyint(1)                     null comment '启用标识 0：启用，1：未启用',\n" +
            "    created_type    tinyint(1)                     null comment '创建类型 0：接口创建 1：控制台创建',\n" +
            "    valid_date_type tinyint(1)                     null comment '有效期类型 0：永久有效 1：自定义',\n" +
            "    valid_date      datetime                       null comment '有效期',\n" +
            "    `describe`      varchar(1024)                  null comment '描述',\n" +
            "    create_time     datetime                       null comment '创建时间',\n" +
            "    update_time     datetime                       null comment '修改时间',\n" +
            "    del_flag        tinyint(1)                     null comment '删除标识 0：未删除 1：已删除',\n" +
            "    constraint `idx_unique_full_short-url`\n" +
            "        unique (full_short_url)\n" +
            ")ENGINE=InnoDB AUTO_INCREMENT=1715030926162935810 DEFAULT CHARSET=utf8mb4;";


    public static void main(String[] args) {
        for (int i = 0; i < 16; i++) {
            System.out.printf((SQL) + "%n", i);
        }
    }
}
