package com.nageoffer.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nageoffer.admin.common.convention.result.Result;
import com.nageoffer.admin.common.convention.result.Results;
import com.nageoffer.admin.remote.dto.ShortLinkRemoteService;
import com.nageoffer.admin.remote.dto.req.RecycleBinRecoverReqDTO;
import com.nageoffer.admin.remote.dto.req.RecycleBinSaveReqDTO;
import com.nageoffer.admin.remote.dto.req.ShortLinkRecycleBinPageReqDTO;
import com.nageoffer.admin.remote.dto.resp.ShortLinkPageRespDTO;
import com.nageoffer.admin.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 回收站管理控制层
 */
@RestController
@RequiredArgsConstructor
public class RecycleBinController {

    private final RecycleBinService recycleBinService;

    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
    };

    /**
     * 保存回收站
     * @param requestParam
     * @return
     */
    @PostMapping("/api/short-link/admin/v1/recycle-bin/save")
    public Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam){
        shortLinkRemoteService.saveRecycleBin(requestParam);
        return Results.success();
    }

    /**
     * 分页查询回收站短链接
     */
    @GetMapping("/api/short-link/admin/v1/recycle-bin/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkRecycleBinPageReqDTO requestParam){
        return recycleBinService.pageRecycleBinShortLink(requestParam);
    }

    /**
     * 恢复被放入回收站里的短链接
     * @param requestParam
     * @return
     */
    @PostMapping("/api/short-link/admin/v1/recycle-bin/recover")
    public Result<Void> recoverRecycleBin(@RequestBody RecycleBinRecoverReqDTO requestParam){
        shortLinkRemoteService.recoverRecycleBin(requestParam);
        return Results.success();
    }
}
