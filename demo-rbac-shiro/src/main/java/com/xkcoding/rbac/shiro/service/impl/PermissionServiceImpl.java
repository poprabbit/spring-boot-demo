package com.xkcoding.rbac.shiro.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xkcoding.rbac.shiro.common.ResourceTypeConstants;
import com.xkcoding.rbac.shiro.config.prop.JwtUtils;
import com.xkcoding.rbac.shiro.mapper.DcsUserMapper;
import com.xkcoding.rbac.shiro.mapper.PermissionMapper;
import com.xkcoding.rbac.shiro.mapper.ResourceMapper;
import com.xkcoding.rbac.shiro.mapper.UserRoleMapper;
import com.xkcoding.rbac.shiro.model.custom.UserInfo;
import com.xkcoding.rbac.shiro.model.entity.DcsUser;
import com.xkcoding.rbac.shiro.model.entity.Permission;
import com.xkcoding.rbac.shiro.model.entity.Resource;
import com.xkcoding.rbac.shiro.model.entity.UserRole;
import com.xkcoding.rbac.shiro.model.vo.PermissionMenuVO;
import com.xkcoding.rbac.shiro.model.vo.PermissionMenuVO.AuthPerm;
import com.xkcoding.rbac.shiro.model.vo.PermissionMenuVO.MenuInfo;
import com.xkcoding.rbac.shiro.service.PermissionService;
import com.xkcoding.rbac.shiro.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * User Service
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-08 18:10
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Autowired
    private DcsUserMapper dcsUserMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private ResourceService resourceService;



    public PermissionMenuVO getPermissionMenu(final String token) {
        UserInfo userInfo = JwtUtils.getUserInfo();
        if (!ObjectUtils.isEmpty(userInfo)) {
            List<Resource> resourceVOList = getResourceListByUserName(userInfo.getUserName());
            if (CollectionUtil.isNotEmpty(resourceVOList)) {
                List<MenuInfo> menuInfoList = new ArrayList<>();
                resourceService.getMenuInfo(menuInfoList, resourceVOList, null);
                return new PermissionMenuVO(menuInfoList, getAuthPerm(resourceVOList), getAllAuthPerms());
            }
        }
        return null;
    }


    @Override
    public Set<String> getAuthPermByUserName(final String userName) {
        List<Resource> resourceVOList = getResourceListByUserName(userName);
        if (CollectionUtil.isNotEmpty(resourceVOList)) {
            return getAuthPerm(resourceVOList).stream().map(AuthPerm::getPerms).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }


    private List<Resource> getResourceListByUserName(final String userName) {
        Map<String, Integer> resourceMap = new HashMap<>();
        DcsUser dcsUser = dcsUserMapper.selectOne(new QueryWrapper<DcsUser>().eq("user_name",userName));
        List<UserRole> userRoleDOList = userRoleMapper.selectList(new QueryWrapper<UserRole>().eq("user_id",dcsUser.getId()));
        for (UserRole userRoleDO : userRoleDOList) {
            list(new QueryWrapper<Permission>().eq("object_id",userRoleDO.getRoleId()))
                .stream().map(Permission::getResourceId)
                .collect(Collectors.toList())
                .forEach(resource -> resourceMap.put(resource, 1));
        }
        if (CollectionUtil.isNotEmpty(resourceMap)) {
            return resourceMap.keySet().stream()
                .map(resource -> resourceMapper.selectById(resource))
                .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }


    private List<AuthPerm> getAuthPerm(final List<Resource> resourceVOList) {
        return resourceVOList.stream()
            .filter(item -> item.getResourceType().equals(ResourceTypeConstants.MENU_TYPE_2))
            .map(AuthPerm::buildAuthPerm)
            .collect(Collectors.toList());
    }

    /**
     * get All AuthPerm.
     *
     * @return {@linkplain List}
     */
    private List<AuthPerm> getAllAuthPerms() {
        return resourceService.list().stream()
            .filter(item -> item.getResourceType().equals(ResourceTypeConstants.MENU_TYPE_2))
            .map(AuthPerm::buildAuthPerm).collect(Collectors.toList());
    }
}
