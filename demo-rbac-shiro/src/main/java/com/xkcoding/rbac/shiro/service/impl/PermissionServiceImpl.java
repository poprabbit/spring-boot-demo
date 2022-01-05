package com.xkcoding.rbac.shiro.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xkcoding.rbac.shiro.common.ResourceTypeConstants;
import com.xkcoding.rbac.shiro.config.prop.JwtUtils;
import com.xkcoding.rbac.shiro.mapper.PermissionMapper;
import com.xkcoding.rbac.shiro.model.custom.UserInfo;
import com.xkcoding.rbac.shiro.model.entity.DcsUser;
import com.xkcoding.rbac.shiro.model.entity.Permission;
import com.xkcoding.rbac.shiro.model.entity.Resource;
import com.xkcoding.rbac.shiro.model.entity.UserRole;
import com.xkcoding.rbac.shiro.model.vo.PermissionMenuVO;
import com.xkcoding.rbac.shiro.model.vo.PermissionMenuVO.AuthPerm;
import com.xkcoding.rbac.shiro.model.vo.PermissionMenuVO.MenuInfo;
import com.xkcoding.rbac.shiro.service.DcsUserService;
import com.xkcoding.rbac.shiro.service.PermissionService;
import com.xkcoding.rbac.shiro.service.ResourceService;
import com.xkcoding.rbac.shiro.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

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
    private DcsUserService dcsUserService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private ResourceService resourceService;

    @Override
    public PermissionMenuVO getPermissionMenu(final String token) {
        UserInfo userInfo = JwtUtils.getUserInfo();
        if (Objects.nonNull(userInfo)) {
            List<Resource> resourceVOList = getResourceListByUserName(userInfo.getUserName());
            if (!isEmpty(resourceVOList)) {
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
        if (!isEmpty(resourceVOList)) {
            return getAuthPerm(resourceVOList).stream().map(AuthPerm::getPerms).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    @Override
    public List<Permission> findByObjectId(String objectId){
        return list(new QueryWrapper<Permission>().eq("object_id",objectId));
    }

    @Override
    public void deleteByResourceIds(Collection<String> resourceIds){
        remove(new QueryWrapper<Permission>().in("resource_id",resourceIds));
    }

    @Override
    public void deleteByObjectIds(Collection<String> objectIds){
        remove(new QueryWrapper<Permission>().in("object_id",objectIds));
    }

    @Override
    public void deleteByObjectIdAndResourceId(String objectId, String resourceId){
        remove(new QueryWrapper<Permission>().eq("object_id",objectId).eq("resource_id",resourceId));
    }

    private List<Resource> getResourceListByUserName(final String userName) {
        Map<String, Integer> resourceMap = new HashMap<>();
        DcsUser dcsUser = dcsUserService.findByUserName(userName);
        List<UserRole> userRoleDOList = userRoleService.findByUserId(dcsUser.getId());
        for (UserRole userRoleDO : userRoleDOList) {
            findByObjectId(userRoleDO.getRoleId()).stream()
                .map(Permission::getResourceId).collect(Collectors.toList())
                .forEach(resource -> resourceMap.put(resource, 1));
        }
        if (!isEmpty(resourceMap)) {
            return resourceMap.keySet().stream()
                .map(resourceService::findById)
                .filter(Objects::nonNull).collect(Collectors.toList());
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
