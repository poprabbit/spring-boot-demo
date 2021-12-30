package com.xkcoding.rbac.shiro.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xkcoding.rbac.shiro.model.entity.DcsUser;
import com.xkcoding.rbac.shiro.model.vo.DcsUserVO;

/**
 * <p>
 * User Service
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-08 18:10
 */
public interface DcsUserService extends IService<DcsUser> {
    DcsUserVO findByUserName(final String userName);
}
