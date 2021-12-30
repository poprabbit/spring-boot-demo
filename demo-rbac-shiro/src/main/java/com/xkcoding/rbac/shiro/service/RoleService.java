package com.xkcoding.rbac.shiro.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xkcoding.rbac.shiro.model.entity.Role;
import com.xkcoding.rbac.shiro.model.page.CommonPager;
import com.xkcoding.rbac.shiro.model.query.RoleQuery;
import com.xkcoding.rbac.shiro.model.vo.RoleEditVO;
import com.xkcoding.rbac.shiro.model.vo.RoleVO;

import java.util.List;

/**
 * <p>
 * User Service
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-08 18:10
 */
public interface RoleService extends IService<Role> {
    Boolean delete(List<String> ids);

    Boolean createOrUpdate(RoleQuery roleDTO);

    RoleEditVO findById(String id);

    List<RoleVO> selectAll();

    CommonPager<Role> listByPage(RoleQuery roleQuery);
}
