package com.nageoffer.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortLinkMsg {
    private String fullUrl;
    private String key;
    // 其他需要的字段
}
