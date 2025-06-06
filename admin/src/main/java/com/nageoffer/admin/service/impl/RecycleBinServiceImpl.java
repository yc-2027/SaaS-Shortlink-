package com.nageoffer.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nageoffer.admin.common.biz.user.UserContext;
import com.nageoffer.admin.common.convention.exception.ServiceException;
import com.nageoffer.admin.common.convention.result.Result;
import com.nageoffer.admin.dao.entity.GroupDO;
import com.nageoffer.admin.dao.mapper.GroupMapper;
import com.nageoffer.admin.remote.ShortLinkActualRemoteService;
import com.nageoffer.admin.remote.dto.req.ShortLinkRecycleBinPageReqDTO;
import com.nageoffer.admin.remote.dto.resp.ShortLinkPageRespDTO;
import com.nageoffer.admin.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * URL 接口实现层
 */
@Service
@RequiredArgsConstructor
public class RecycleBinServiceImpl implements RecycleBinService {

    private final ShortLinkActualRemoteService shortLinkActualRemoteService;
    private final GroupMapper groupMapper;




    @Override
    public Result<Page<ShortLinkPageRespDTO>> pageRecycleBinShortLink(ShortLinkRecycleBinPageReqDTO requestParam){
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0);
        List<GroupDO> groupDOList = groupMapper.selectList(queryWrapper);
        if(CollUtil.isEmpty(groupDOList)){
            throw new ServiceException("用户无分组信息");
        }
        requestParam.setGidList(groupDOList.stream().map(GroupDO::getGid).toList());//把搜到的group信息以list的形式加入requestParam
        return shortLinkActualRemoteService.pageRecycleBinShortLink(requestParam);
    }

}
