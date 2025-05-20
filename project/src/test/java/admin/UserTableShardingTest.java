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
    public static final String SQL =
            """
                    ALTER TABLE t_link_%d
                        MODIFY full_short_url VARCHAR(255)
                            CHARACTER SET utf8mb4
                                COLLATE utf8mb4_general_ci;
                    """;

//    public static final String SQL =
//            """
//                    create table t_link_%d
//                    (
//                        id              bigint auto_increment comment 'ID'
//                            primary key,
//                        domain          varchar(128)                   null comment '域名',
//                        short_url       varchar(8) collate utf8mb3_bin null,
//                        full_short_url  varchar(128)                   null comment '完整短链接',
//                        origin_url      varchar(1024)                  null comment '原始链接',
//                        click_num       int default 0                  null comment '点击量',
//                        gid             varchar(32)                    null comment '分组标识',
//                        favicon         varchar(256)                   null comment '网站标识',
//                        enable_status   tinyint(1)                     null comment '启用标识 0：启用，1：未启用',
//                        created_type    tinyint(1)                     null comment '创建类型 0：接口创建 1：控制台创建',
//                        valid_date_type tinyint(1)                     null comment '有效期类型 0：永久有效 1：自定义',
//                        valid_date      datetime                       null comment '有效期',
//                        `describe`      varchar(1024)                  null comment '描述',
//                        total_pv        int default 0                  null comment '''历史PV''',
//                        total_uv        int default 0                  null comment '''历史UV''',
//                        total_uip       int default 0                  null comment '''历史UIP''',
//                        create_time     datetime                       null comment '创建时间',
//                        update_time     datetime                       null comment '修改时间',
//                        del_time        bigint(20)                     null comment '删除时间戳',
//                        del_flag        tinyint(1)                     null comment '删除标识 0：未删除 1：已删除',
//                        constraint `idx_unique_full-short-url`
//                            unique (full_short_url,del_time)
//                    );
//                    """;

    public static void main(String[] args) {
        for (int i = 0; i < 16; i++) {
            System.out.printf((SQL) + "%n", i);
        }
    }
}
