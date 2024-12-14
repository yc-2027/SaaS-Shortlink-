package com.nageoffer.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nageoffer.project.dao.entity.LinkLocaleStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

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
}
