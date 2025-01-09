package com.nageoffer.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nageoffer.project.dao.entity.LinkLocaleStatsDO;
import com.nageoffer.project.dto.req.ShortLinkStatsReqDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 地区统计访问持久层
 */
public interface LinkLocaleStatsMapper extends BaseMapper<LinkLocaleStatsDO> {
    /**
     * 记录基础访问监控数据
     * @param
     */
    @Insert("""
        INSERT INTO t_link_locale_stats (
            id,
            full_short_url,
            gid,
            date,
            cnt,
            region_name,
            city,
            zip,
            country,
            query,
            create_time,
            update_time,
            del_flag
        ) VALUES (
            #{linkLocaleStats.id},
            #{linkLocaleStats.fullShortUrl},
            #{linkLocaleStats.gid},
            #{linkLocaleStats.date},
            #{linkLocaleStats.cnt},
            #{linkLocaleStats.regionName},
            #{linkLocaleStats.city},
            #{linkLocaleStats.zip},
            #{linkLocaleStats.country},
            #{linkLocaleStats.query},
            NOW(),
            NOW(),
            0
        )   ON DUPLICATE KEY UPDATE cnt = cnt +  #{linkLocaleStats.cnt};
    """)
    void shortLinkLocaleStats(@Param("linkLocaleStats") LinkLocaleStatsDO linkLocaleStatsDO);

    /**
     * 地区统计访问持久层
     @@ -17,4 +21,20 @@ public interface LinkLocaleStatsMapper extends BaseMapper<LinkLocaleStatsDO> {
     "VALUES( #{linkLocaleStats.fullShortUrl}, #{linkLocaleStats.gid}, #{linkLocaleStats.date}, #{linkLocaleStats.cnt}, #{linkLocaleStats.country}, #{linkLocaleStats.province}, #{linkLocaleStats.city}, #{linkLocaleStats.adcode}, NOW(), NOW(), 0) " +
     "ON DUPLICATE KEY UPDATE cnt = cnt +  #{linkLocaleStats.cnt};")
     void shortLinkLocaleState(@Param("linkLocaleStats") LinkLocaleStatsDO linkLocaleStatsDO);

     /**
      * 根据短链接获取指定地区内基础监控数据
     */
    @Select("SELECT " +
            "    country, " +
            "    SUM(cnt) AS cnt " +
            "FROM " +
            "    t_link_locale_stats " +
            "WHERE " +
            "    full_short_url = #{param.fullShortUrl} " +
            "    AND gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    full_short_url, gid, country;")
    List<LinkLocaleStatsDO> listLocaleByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);
}
