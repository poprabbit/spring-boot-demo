package com.xkcoding.rbac.shiro.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xkcoding.rbac.shiro.mapper.UserRoleMapper;
import com.xkcoding.rbac.shiro.model.entity.UserRole;
import com.xkcoding.rbac.shiro.service.UserRoleService;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public List<UserRole> findByUserId(String userId){
        return list(new QueryWrapper<UserRole>().eq("user_id",userId));
    }

    @Override
    public Boolean deleteByUserId(String userId){
        return remove(new QueryWrapper<UserRole>().eq("user_id",userId));
    }
}
