package com.nageoffer.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nageoffer.project.dao.entity.LinkAccessLogsDO;
import com.nageoffer.project.dao.entity.LinkAccessStatsDO;
import com.nageoffer.project.dto.req.ShortLinkGroupStatsReqDTO;
import com.nageoffer.project.dto.req.ShortLinkStatsAccessRecordReqDTO;
import com.nageoffer.project.dto.req.ShortLinkStatsReqDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 短链接访问日志监控持久层
 */
public interface LinkAccessLogsMapper extends BaseMapper<LinkAccessLogsDO> {
    /**
     * 分页查询短链接监控
     */
//    @Select("SELECT *" +
//            "FROM " +
//            "t_link_access_logs" +
//            "WHERE full_short_url = #{param.fullShortUrl}" +
//
//    )
    @Select("""
           SELECT *
           FROM t_link_access_logs
           WHERE full_short_url = #{param.fullShortUrl}
             AND create_time BETWEEN #{param.startDate} AND #{param.endDate}
             AND del_flag = 0
           ORDER BY create_time DESC
     """)
    IPage<LinkAccessLogsDO> ShortLinkStatsByPage(@Param("param") ShortLinkStatsAccessRecordReqDTO requestParam);

    /**
     * 根据短链接获取指定日期内高频访问IP数据
     */
    //注意下面之所以这样写是因为create time会把日期转换为当天0点
    //注意日期后面拼接时间一定要有空格 ！
    @Select("SELECT " +
            "    ip, " +
            "    COUNT(ip) AS count " +
            "FROM " +
            "    t_link_access_logs " +
            "WHERE " +
            "    full_short_url = #{param.fullShortUrl} " +
//            "    AND gid = #{param.gid} " +
            "    AND create_time BETWEEN CONCAT(#{param.startDate},' 00:00:00') and  CONCAT(#{param.endDate},' 23:59:59') " +
            "GROUP BY " +
            "    full_short_url,  ip " +
            "ORDER BY " +
            "    count DESC " +
            "LIMIT 5;")
    List<HashMap<String, Object>> listTopIpByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);
    /**
     * 根据分组获取指定日期内高频访问IP数据
     */
//    @Select("SELECT " +
//            "    a.ip, " +
//            "    COUNT(a.ip) AS count " +
//            "FROM " +
//            "    t_link_access_logs as a" +
//            " JOIN t_link as l" +
//            "WHERE " +
//            "    l.gid = #{param.gid} " +
//            "    AND a.create_time BETWEEN #{param.startDate} and #{param.endDate} " +
//            "GROUP BY " +
//            "    l.gid, a.ip " +
//            "ORDER BY " +
//            "    count DESC " +
//            "LIMIT 5;")
    @Select("""
    SELECT ip,COUNT(ip) as count
    FROM t_link_access_logs as tlal 
    join t_link_0 as l 
    on tlal.full_short_url = l.full_short_url
    where l.gid =  #{param.gid}
    AND tlal.create_time BETWEEN #{param.startDate} and #{param.endDate}
    GROUP BY ip
    ORDER BY count DESC
    LIMIT 5;
    """)
    List<HashMap<String, Object>> listTopIpByGroup(@Param("param") ShortLinkGroupStatsReqDTO requestParam);

    /**
     * 根据短链接获取指定日期内新旧访客数据
     */
    @Select("SELECT " +
            "    SUM(old_user) AS oldUserCnt, " +
            "    SUM(new_user) AS newUserCnt " +
            "FROM ( " +
            "    SELECT " +
            "        CASE WHEN COUNT(DISTINCT DATE(create_time)) > 1 THEN 1 ELSE 0 END AS old_user, " +
            "        CASE WHEN COUNT(DISTINCT DATE(create_time)) = 1 AND MAX(create_time) >= #{param.startDate} AND MAX(create_time) <= #{param.endDate} THEN 1 ELSE 0 END AS new_user " +
            "    FROM " +
            "        t_link_access_logs " +
            "    WHERE " +
            "        full_short_url = #{param.fullShortUrl} " +
//            "        AND gid = #{param.gid} " +
            "    GROUP BY " +
            "        user " +
            ") AS user_counts;")
    HashMap<String, Object> findUvTypeCntByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 获取用户信息是否新老访客
     */
    @Select("""
        <script>
            SELECT\s
                `user`,\s
                CASE\s
                    WHEN MIN(create_time) BETWEEN #{startDate} AND #{endDate} THEN '新访客'\s
                    ELSE '老访客'\s
                END AS uvType
            FROM\s
                t_link_access_logs
            WHERE\s
                full_short_url = #{fullShortUrl}
                <if test="userAccessLogsList != null and userAccessLogsList.size() > 0">
                    AND `user` IN\s
                    <foreach item="item" index="index" collection="userAccessLogsList" open="(" separator="," close=")">
                        #{item}
                    </foreach>
                </if>
            GROUP BY\s
                `user`
        </script>
    """)
    List<Map<String, Object>> selectUvTypeByUsers(
            @Param("fullShortUrl") String fullShortUrl,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("userAccessLogsList") List<String> userAccessLogsList
    );
    /**
     * 获取分组用户信息是否新老访客
     */
    @Select("<script> " +
            "SELECT " +
            "    al.user, " +
            "    CASE " +
            "        WHEN MIN(al.create_time) BETWEEN #{startDate} AND #{endDate} THEN '新访客' " +
            "        ELSE '老访客' " +
            "    END AS uvType " +
            "FROM " +
            "    t_link_access_logs  as al" +
            " t_link as l" +
            "WHERE " +
            "    l.gid = #{gid} " +
            "    AND al.user IN " +
            "    <foreach item='item' index='index' collection='userAccessLogsList' open='(' separator=',' close=')'> " +
            "        #{item} " +
            "    </foreach> " +
            "GROUP BY " +
            "    al.user;" +
            "    </script>"
    )
    List<Map<String, Object>> selectGroupUvTypeByUsers(
            @Param("gid") String gid,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("userAccessLogsList") List<String> userAccessLogsList
    );

    /**
     * 根据分组获取指定日期内PV、UV、UIP数据
     */
    @Select("SELECT " +
            "    COUNT(tlal.user) AS pv, " +
            "    COUNT(DISTINCT tlal.user) AS uv, " +
            "    COUNT(DISTINCT tlal.ip) AS uip " +
            "FROM " +
            "    t_link tl INNER JOIN " +
            "    t_link_access_logs tlal ON tl.full_short_url = tlal.full_short_url " +
            "WHERE " +
            "    tl.gid = #{param.gid} " +
            "    AND tl.del_flag = '0' " +
            "    AND tl.enable_status = '0' " +
            "    AND tlal.create_time BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    tl.gid;")
    LinkAccessStatsDO findPvUvUidStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 根据分组获取指定日期内PV、UV、UIP数据
     */
//    @Select("SELECT " +
//            "    COUNT(user) AS pv, " +
//            "    COUNT(DISTINCT user) AS uv, " +
//            "    COUNT(DISTINCT ip) AS uip " +
//            "FROM " +
//            "    t_link_access_logs " +
//            "WHERE " +
//            "    gid = #{param.gid} " +
//            "    AND create_time BETWEEN #{param.startDate} and #{param.endDate} " +
//            "GROUP BY " +
//            "    gid;")
    @Select("""
        SELECT  COUNT(user) AS pv,COUNT(DISTINCT user) AS uv,COUNT(DISTINCT ip) AS uip
        FROM t_link_access_logs AS s
        JOIN t_link AS t
        ON  t.full_short_url = s.full_short_url
        WHERE t.gid = #{param.gid}
        AND s.create_time BETWEEN #{param.startDate} and #{param.endDate}
        GROUP BY t.gid
    """)
    LinkAccessStatsDO findPvUvUidStatsByGroup(@Param("param") ShortLinkGroupStatsReqDTO requestParam);
}
