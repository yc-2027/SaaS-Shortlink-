package com.nageoffer.project.controller;

import com.nageoffer.project.common.convention.result.Result;
import com.nageoffer.project.common.convention.result.Results;
import com.nageoffer.project.service.UrlTitleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * URL 标题控制层
 */
@RestController
@RequiredArgsConstructor
public class UrlTitleController {
    private final UrlTitleService urlTitleService;
    /**
     * 根据 URL 获取对应网站的标题
     * @param url
     * @return
     */
    @GetMapping("/api/short-link/v1/title")
    public Result<String> getTitleByUrl(@RequestParam("url") String url){
        return Results.success(urlTitleService.getTitleByUrl(url));
    }
}
