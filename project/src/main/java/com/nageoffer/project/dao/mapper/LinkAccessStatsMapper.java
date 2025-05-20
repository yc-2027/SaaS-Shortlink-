package com.nageoffer.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nageoffer.project.dao.entity.LinkAccessStatsDO;
import com.nageoffer.project.dto.req.ShortLinkGroupStatsReqDTO;
import com.nageoffer.project.dto.req.ShortLinkStatsReqDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 短链接基础访问监控持久层
 */
public interface LinkAccessStatsMapper extends BaseMapper<LinkAccessStatsDO> {

    /**
     * 记录基础访问监控数据
     * @param linkAccessStatsDO
     */
    @Insert("INSERT INTO " +
            "t_link_access_stats (full_short_url, gid, date, pv, uv, uip, hour, weekday, create_time, update_time, del_flag) " +
            "VALUES( #{linkAccessStats.fullShortUrl}, #{linkAccessStats.gid}, #{linkAccessStats.date}, #{linkAccessStats.pv}, #{linkAccessStats.uv}, #{linkAccessStats.uip}, #{linkAccessStats.hour}, #{linkAccessStats.weekday}, NOW(), NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE pv = pv +  #{linkAccessStats.pv}, uv = uv + #{linkAccessStats.uv}, uip = uip + #{linkAccessStats.uip};")
    void shortLinkStats(@Param("linkAccessStats") LinkAccessStatsDO linkAccessStatsDO);

    /**
     * 短链接基础访问监控持久层
     @@ -18,4 +22,54 @@ public interface LinkAccessStatsMapper extends BaseMapper<LinkAccessStatsDO> {
     "uv = uv + #{linkAccessStats.uv}, " +
     " uip = uip + #{linkAccessStats.uip};")
     void shortLinkStats(@Param("linkAccessStats") LinkAccessStatsDO linkAccessStatsDO);

     /**
      * 根据短链接获取指定日期内基础监控数据
     */
    @Select("SELECT " +
            "    date, " +
            "    SUM(pv) AS pv, " +
            "    SUM(uv) AS uv, " +
            "    SUM(uip) AS uip " +
            "FROM " +
            "    t_link_access_stats " +
            "WHERE " +
            "    full_short_url = #{param.fullShortUrl} " +
//            "    AND gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    full_short_url,  date;")
    List<LinkAccessStatsDO> listStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 根据分组获取指定日期内基础监控数据
     */
//    @Select("SELECT " +
//            "    date, " +
//            "    SUM(pv) AS pv, " +
//            "    SUM(uv) AS uv, " +
//            "    SUM(uip) AS uip " +
//            "FROM " +
//            "    t_link_access_stats " +
//            "WHERE " +
//            "    gid = #{param.gid} " +
//            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
//            "GROUP BY " +
//            "    gid, date;")
    @Select(
            """
                    SELECT date,SUM(pv) AS pv,SUM(uv) AS uv,SUM(uip) AS uip
                    FROM t_link_access_stats as s
                    JOIN t_link  as g on g.full_short_url = s.full_short_url
                    WHERE g.gid = #{param.gid}
                    AND s.date BETWEEN #{param.startDate} and #{param.endDate}
                    GROUP BY s.date
                    """
    )
    List<LinkAccessStatsDO> listStatsByGroup(@Param("param") ShortLinkGroupStatsReqDTO requestParam);

    /**
     * 根据短链接获取指定日期内小时基础监控数据
     */
    @Select("SELECT " +
            "    hour, " +
            "    SUM(pv) AS pv " +
            "FROM " +
            "    t_link_access_stats " +
            "WHERE " +
            "    full_short_url = #{param.fullShortUrl} " +
//            "    AND gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    full_short_url,  hour;")
    List<LinkAccessStatsDO> listHourStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 根据分组获取指定日期内小时基础监控数据
     */
//    @Select("SELECT " +
//            "    hour, " +
//            "    SUM(pv) AS pv " +
//            "FROM " +
//            "    t_link_access_stats " +
//            "WHERE " +
////            "    gid = #{param.gid}  AND" +
//            "    date BETWEEN #{param.startDate} and #{param.endDate} " +
//            "GROUP BY " +
//            "     hour;")
    @Select("""
    select hour,SUM(pv) AS pv from t_link_access_stats as s
    join t_link as t
    on s.full_short_url = t.full_short_url
    where t.gid = #{param.gid}
    and s.date BETWEEN #{param.startDate} and #{param.endDate}
    group by hour;
    """)
    List<LinkAccessStatsDO> listHourStatsByGroup(@Param("param") ShortLinkGroupStatsReqDTO requestParam);

    /**
     * 根据短链接获取指定日期内小时基础监控数据
     */
    @Select("SELECT " +
            "    weekday, " +
            "    SUM(pv) AS pv " +
            "FROM " +
            "    t_link_access_stats " +
            "WHERE " +
            "    full_short_url = #{param.fullShortUrl} " +
//            "    AND gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    full_short_url,  weekday;")
    List<LinkAccessStatsDO> listWeekdayStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 根据分组获取指定日期内小时基础监控数据
     */
//    @Select("SELECT " +
//            "    weekday, " +
//            "    SUM(pv) AS pv " +
//            "FROM " +
//            "    t_link_access_stats " +
//            "WHERE " +
//            "    gid = #{param.gid} " +
//            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
//            "GROUP BY " +
//            "    gid, weekday;")
    @Select("""
        SELECT weekday,SUM(pv) AS pv
        FROM t_link_access_stats as s
        JOIN t_link as t 
        ON s.full_short_url = t.full_short_url
        WHERE t.gid = #{param.gid}
        AND s.date BETWEEN #{param.startDate} and #{param.endDate}
        GROUP BY s.weekday;
    """)
    List<LinkAccessStatsDO> listWeekdayStatsByGroup(@Param("param") ShortLinkGroupStatsReqDTO requestParam);
}