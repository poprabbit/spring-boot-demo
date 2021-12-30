package com.xkcoding.rbac.shiro.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xkcoding.rbac.shiro.common.AdminConstants;
import com.xkcoding.rbac.shiro.enums.AdminResourceEnum;
import com.xkcoding.rbac.shiro.mapper.PermissionMapper;
import com.xkcoding.rbac.shiro.mapper.ResourceMapper;
import com.xkcoding.rbac.shiro.model.entity.Permission;
import com.xkcoding.rbac.shiro.model.entity.Resource;
import com.xkcoding.rbac.shiro.model.page.CommonPager;
import com.xkcoding.rbac.shiro.model.page.PageParameter;
import com.xkcoding.rbac.shiro.model.page.PageResultUtils;
import com.xkcoding.rbac.shiro.model.query.ResourceQuery;
import com.xkcoding.rbac.shiro.model.vo.PermissionMenuVO.MenuInfo;
import com.xkcoding.rbac.shiro.service.ResourceService;
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
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements ResourceService {

    @Autowired
    private PermissionMapper permissionMapper;

    public void createResource(final Resource resource) {
        insertResource(resource);
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean createOrUpdate(final Resource resource) {
        if (StringUtils.isEmpty(resource.getId())) {
            return insertResource(resource);
        } else {
            return updateById(resource);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean delete(final List<String> ids) {
        Set<String> deleteResourceIds = new HashSet<>(ids);
        List<Resource> allResources = list();
        getDeleteResourceIds(deleteResourceIds, ids, allResources);
        permissionMapper.delete(new QueryWrapper<Permission>().in("resource_id",deleteResourceIds));
        return removeByIds(deleteResourceIds);
    }


    public Resource findById(final String id) {
        return getById(id);
    }


    public Resource findByTitle(final String title) {
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("title", title);
        return listByMap(columnMap).iterator().next();
    }

    public CommonPager<Resource> listByPage(final ResourceQuery resourceQuery) {
        PageParameter pagePara = resourceQuery.getPageParameter();
        IPage<Resource> page = new Page(pagePara.getCurrentPage(),pagePara.getPageSize());
        String title = resourceQuery.getTitle();
        page = page(page, new QueryWrapper<Resource>().eq("status",1)
            .eq(!StringUtils.isEmpty(title),"title",title));
        return PageResultUtils.result(page);
    }


    public List<MenuInfo> getMenuTree() {
        List<Resource> resourceVOList = list();
        if (CollectionUtil.isNotEmpty(resourceVOList)) {
            List<MenuInfo> menuInfoList = new ArrayList<>();
            getMenuInfo(menuInfoList, resourceVOList, null);
            return menuInfoList;
        }
        return null;
    }


    public List<Resource> findByParentId(final String id) {
        return list(new QueryWrapper<Resource>(Resource.builder().parentId(id).build())).stream()
            .filter(item -> item.getResourceType().equals(AdminResourceEnum.THREE_MENU.getCode()))
            .collect(Collectors.toList());
    }


    @Override
    public void getMenuInfo(final List<MenuInfo> menuInfoList, final List<Resource> metaList, final MenuInfo menuInfo) {
        for (Resource resourceVO : metaList) {
            String parentId = resourceVO.getParentId();
            MenuInfo tempMenuInfo = MenuInfo.buildMenuInfo(resourceVO);
            if (ObjectUtils.isEmpty(tempMenuInfo)) {
                continue;
            }
            if (ObjectUtils.isEmpty(menuInfo) && StringUtils.isEmpty(parentId)) {
                menuInfoList.add(tempMenuInfo);
                if (Objects.equals(resourceVO.getIsLeaf(), Boolean.FALSE)) {
                    getMenuInfo(menuInfoList, metaList, tempMenuInfo);
                }
            } else if (!ObjectUtils.isEmpty(menuInfo) && !StringUtils.isEmpty(parentId) && parentId.equals(menuInfo.getId())) {
                menuInfo.getChildren().add(tempMenuInfo);
                if (Objects.equals(resourceVO.getIsLeaf(), Boolean.FALSE)) {
                    getMenuInfo(menuInfoList, metaList, tempMenuInfo);
                }
            }
        }
    }


    private void getDeleteResourceIds(final Set<String> deleteResourceIds, final List<String> resourceIds,
                                      final List<Resource> allResources) {
        List<String> matchResourceIds = new ArrayList<>();
        resourceIds.forEach(item -> {
            matchResourceIds.clear();
            allResources.forEach(resource -> {
                if (resource.getParentId().equals(item)) {
                    matchResourceIds.add(resource.getId());
                    deleteResourceIds.add(resource.getId());
                }
            });
            if (CollectionUtil.isNotEmpty(matchResourceIds)) {
                getDeleteResourceIds(deleteResourceIds, matchResourceIds, allResources);
            }
        });
    }


    private boolean insertResource(final Resource resource) {
        Permission permission = new Permission(AdminConstants.ROLE_SUPER_ID,resource.getId());
        permission.setId(UUIDUtils.getInstance().generateShortUuid());
        permissionMapper.insert(permission);
        return save(resource);
    }
}
