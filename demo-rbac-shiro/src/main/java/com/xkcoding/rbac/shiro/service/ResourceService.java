package com.xkcoding.rbac.shiro.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xkcoding.rbac.shiro.model.entity.Resource;
import com.xkcoding.rbac.shiro.model.page.CommonPager;
import com.xkcoding.rbac.shiro.model.query.ResourceQuery;
import com.xkcoding.rbac.shiro.model.vo.PermissionMenuVO;

import java.util.List;

/**
 * <p>
 * User Service
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-08 18:10
 */
public interface ResourceService extends IService<Resource> {

    void getMenuInfo(final List<PermissionMenuVO.MenuInfo> menuInfoList, final List<Resource> metaList, final PermissionMenuVO.MenuInfo menuInfo);

    List<PermissionMenuVO.MenuInfo> getMenuTree();

    List<Resource> findByParentId(String id);

    Resource findById(String id);

    Boolean createOrUpdate(final Resource resource);

    CommonPager<Resource> listByPage(final ResourceQuery resource);

    Boolean delete(List<String> ids);
}
