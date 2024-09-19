package com.nageoffer.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.admin.common.biz.user.UserContext;
import com.nageoffer.admin.dao.entity.GroupDO;
import com.nageoffer.admin.dao.mapper.GroupMapper;
import com.nageoffer.admin.dto.resp.ShortLinkGroupRespDTO;
import com.nageoffer.admin.service.GroupService;
import com.nageoffer.admin.toolkit.RandomGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 短链接分组接口实现层
 */
@Slf4j
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {
    @Override
    public void saveGroup(String groupName) {
        String gid;
        while(true){
            gid = RandomGenerator.generateRandom();
            if(!hasGid(gid)){
                break;
            }
        }
        GroupDO groupDO = GroupDO.builder()
                .gid(gid)
                .sortOrder(0)
                .username(UserContext.getUsername())
                .name(groupName)
                .build();
        baseMapper.insert(groupDO);
    }

    @Override
    public List<ShortLinkGroupRespDTO> listGroup() {
        // TODO 从当前上下文获取用户名
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                //.isNull(UserContext.getUsername())
                .eq(GroupDO::getDelFlag,0)
                .eq(GroupDO::getUsername,UserContext.getUsername())
                .orderByDesc(GroupDO::getSortOrder,GroupDO::getUpdateTime);
        List<GroupDO> groupDOList = baseMapper.selectList(queryWrapper);
        return BeanUtil.copyToList(groupDOList, ShortLinkGroupRespDTO.class);
    }

    boolean hasGid(String gid){
        LambdaQueryWrapper<GroupDO> queryWrapper =
                Wrappers.lambdaQuery(GroupDO.class)//检查gid有没有重复
                .eq(GroupDO::getGid,gid)
                // TODO 设置用户名
                .eq(GroupDO::getUsername,UserContext.getUsername());
        GroupDO hasGroupFlag = baseMapper.selectOne(queryWrapper);
        return hasGroupFlag != null;
    }
}
