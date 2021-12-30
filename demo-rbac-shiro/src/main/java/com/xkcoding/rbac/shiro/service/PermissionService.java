package com.xkcoding.rbac.shiro.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xkcoding.rbac.shiro.model.entity.Permission;
import com.xkcoding.rbac.shiro.model.vo.PermissionMenuVO;

import java.util.Set;

/**
 * <p>
 * User Service
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-08 18:10
 */
public interface PermissionService extends IService<Permission> {
    Set<String> getAuthPermByUserName(final String userName);

    PermissionMenuVO getPermissionMenu(String token);
}
