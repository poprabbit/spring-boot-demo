package com.xkcoding.rbac.shiro.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xkcoding.rbac.shiro.mapper.PermissionMapper;
import com.xkcoding.rbac.shiro.mapper.RoleMapper;
import com.xkcoding.rbac.shiro.model.entity.Permission;
import com.xkcoding.rbac.shiro.model.entity.Resource;
import com.xkcoding.rbac.shiro.model.entity.Role;
import com.xkcoding.rbac.shiro.model.page.CommonPager;
import com.xkcoding.rbac.shiro.model.page.PageParameter;
import com.xkcoding.rbac.shiro.model.page.PageResultUtils;
import com.xkcoding.rbac.shiro.model.query.RoleQuery;
import com.xkcoding.rbac.shiro.model.vo.RoleEditVO;
import com.xkcoding.rbac.shiro.model.vo.RoleEditVO.PermissionInfo;
import com.xkcoding.rbac.shiro.model.vo.RoleEditVO.ResourceInfo;
import com.xkcoding.rbac.shiro.model.vo.RoleVO;
import com.xkcoding.rbac.shiro.service.ResourceService;
import com.xkcoding.rbac.shiro.service.RoleService;
import com.xkcoding.rbac.shiro.util.UUIDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

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
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private ResourceService resourceService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean createOrUpdate(final RoleQuery roleDTO) {
        Role roleDO = RoleQuery.createDO(roleDTO);
        if (StringUtils.isEmpty(roleDTO.getId())) {
            return save(roleDO);
        } else {
            manageRolePermission(roleDTO.getId(), roleDTO.getCurrentPermissionIds());
            return updateById(roleDO);
        }
    }

    @Override
    public Boolean delete(final List<String> ids) {
        permissionMapper.delete(new QueryWrapper<Permission>().in("object_id",ids));
        return removeByIds(ids);
    }

    @Override
    public RoleEditVO findById(final String id) {
        RoleVO sysRole = RoleVO.buildRoleVO(getById(id));
        return Optional.ofNullable(sysRole).map(item -> new RoleEditVO(getPermissionIdsByRoleId(item.getId()), item,
            getAllPermissions())).orElse(null);
    }


    public RoleVO findByQuery(final String roleName) {
        return RoleVO.buildRoleVO(getOne(new QueryWrapper<Role>().eq("role_name",roleName)));
    }

    @Override
    public CommonPager<Role> listByPage(final RoleQuery roleQuery) {
        PageParameter pagePara = roleQuery.getPageParameter();
        IPage<Role> page = new Page(pagePara.getCurrentPage(),pagePara.getPageSize());
        String roleName = roleQuery.getRoleName();
        page = page(page, new QueryWrapper<Role>().ne("role_name","super")
            .eq(!StringUtils.isEmpty(roleName),"role_name",roleName));
        return PageResultUtils.result(page);
    }

    @Override
    public List<RoleVO> selectAll() {
        return list().stream().map(RoleVO::buildRoleVO).collect(Collectors.toList());
    }

    /**
     * get all permissions.
     *
     * @return {@linkplain PermissionInfo}
     */
    private PermissionInfo getAllPermissions() {
        List<Resource> resourceVOList = resourceService.list();
        List<String> permissionIds = resourceVOList.stream().map(Resource::getId).collect(Collectors.toList());
        List<ResourceInfo> treeList = new ArrayList<>();
        getTreeModelList(treeList, resourceVOList, null);
        return new PermissionInfo(treeList, permissionIds);
    }

    /**
     * get permission ids by role id.
     *
     * @param roleId role id
     * @return {@linkplain List}
     */
    private List<String> getPermissionIdsByRoleId(final String roleId) {
        return permissionMapper.selectList(new QueryWrapper<Permission>().eq("object_id",roleId))
            .stream().map(Permission::getResourceId).collect(Collectors.toList());
    }

    /**
     * get menu list.
     *
     * @param treeList {@linkplain ResourceInfo}
     * @param metaList {@linkplain Resource}
     * @param resourceInfo {@linkplain ResourceInfo}
     */
    private void getTreeModelList(final List<ResourceInfo> treeList, final List<Resource> metaList, final ResourceInfo resourceInfo) {
        for (Resource resourceVO : metaList) {
            String parentId = resourceVO.getParentId();
            ResourceInfo resourceInfoItem = ResourceInfo.buildResourceInfo(resourceVO);
            if (ObjectUtils.isEmpty(resourceInfo) && StringUtils.isEmpty(parentId)) {
                treeList.add(resourceInfoItem);
                if (resourceInfoItem.getIsLeaf().equals(Boolean.FALSE)) {
                    getTreeModelList(treeList, metaList, resourceInfoItem);
                }
            } else if (!ObjectUtils.isEmpty(resourceInfo) && !StringUtils.isEmpty(parentId) && parentId.equals(resourceInfo.getId())) {
                resourceInfo.getChildren().add(resourceInfoItem);
                if (resourceInfoItem.getIsLeaf().equals(Boolean.FALSE)) {
                    getTreeModelList(treeList, metaList, resourceInfoItem);
                }
            }

        }
    }

    /**
     * get two list different.
     *
     * @param preList {@linkplain List}
     * @param lastList {@linkplain List}
     * @return {@linkplain List}
     */
    private List<String> getListDiff(final List<String> preList, final List<String> lastList) {
        if (CollectionUtil.isEmpty(lastList)) {
            return null;
        }
        if (CollectionUtil.isEmpty(preList)) {
            return lastList;
        }
        Map<String, Integer> map = preList.stream().distinct()
            .collect(Collectors.toMap(source -> source, source -> 1));
        return lastList.stream().filter(item -> !map.containsKey(item)).collect(Collectors.toList());
    }

    /**
     * batch save permission.
     *
     * @param permissionDOList {@linkplain List}
     */
    private void batchSavePermission(final List<Permission> permissionDOList) {
        permissionDOList.forEach(permissionMapper::insert);
    }

    /**
     * delete by object and resource id.
     *
     * @param permissionQuery permission query
     */
    private void deleteByObjectIdAndResourceId(final Permission permissionQuery) {
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("object_id", permissionQuery.getObjectId());
        paramMap.put("resource_id", permissionQuery.getResourceId());
        permissionMapper.delete(new QueryWrapper<Permission>().allEq(paramMap));
    }

    /**
     * manger role permission.
     *
     * @param roleId role id.
     * @param currentPermissionList {@linkplain List} current role permission ids
     */
    private void manageRolePermission(final String roleId, final List<String> currentPermissionList) {
        List<String> lastPermissionList = permissionMapper.selectList(new QueryWrapper<Permission>().eq("object_id",roleId))
            .stream().map(Permission::getResourceId).collect(Collectors.toList());
        List<String> addPermission = getListDiff(lastPermissionList, currentPermissionList);
        if (CollectionUtil.isNotEmpty(addPermission)) {
            batchSavePermission(addPermission.stream().map(node -> {
                Permission permission = new Permission(roleId, node);
                permission.setId(UUIDUtils.getInstance().generateShortUuid());
                return permission;
            }).collect(Collectors.toList()));
        }
        List<String> deletePermission = getListDiff(currentPermissionList, lastPermissionList);
        if (CollectionUtil.isNotEmpty(deletePermission)) {
            deletePermission.forEach(node -> deleteByObjectIdAndResourceId(new Permission(roleId, node)));
        }
    }
}
