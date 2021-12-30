package com.xkcoding.rbac.shiro.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xkcoding.rbac.shiro.mapper.DcsUserMapper;
import com.xkcoding.rbac.shiro.mapper.UserRoleMapper;
import com.xkcoding.rbac.shiro.model.entity.DcsUser;
import com.xkcoding.rbac.shiro.model.entity.UserRole;
import com.xkcoding.rbac.shiro.service.DcsUserService;
import com.xkcoding.rbac.shiro.service.UserRoleService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * User Service
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-08 18:10
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

}
