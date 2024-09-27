package com.nageoffer.admin.dto.req;

import lombok.Data;

/**
 * 短链接分组排序参数
 */
@Data
public class ShortLinkGroupSortRequestDTO {
    /**
     * 分组名
     */
    private String gid;

    /**
     * 排序
     */
    private Integer sortOrder;
}
