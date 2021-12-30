package com.xkcoding.rbac.shiro.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xkcoding.rbac.shiro.model.entity.DcsUser;
import com.xkcoding.rbac.shiro.model.page.CommonPager;
import com.xkcoding.rbac.shiro.model.query.DcsUserQuery;
import com.xkcoding.rbac.shiro.model.vo.DcsUserVO;
import com.xkcoding.rbac.shiro.model.vo.LoginDcsUserVO;

import java.util.List;

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

    CommonPager<DcsUser> listByPage(DcsUserQuery build);

    DcsUserVO findById(String id);

    Boolean createOrUpdate(DcsUserQuery item);

    Integer delete(List<String> ids);

    DcsUserVO findByQuery(final String userName, final String password);

    LoginDcsUserVO login(String userName, String password);
}
