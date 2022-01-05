package com.xkcoding.rbac.shiro.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xkcoding.rbac.shiro.common.AuthConstants;
import com.xkcoding.rbac.shiro.config.prop.JwtProperties;
import com.xkcoding.rbac.shiro.config.prop.JwtUtils;
import com.xkcoding.rbac.shiro.config.prop.SecretProperties;
import com.xkcoding.rbac.shiro.mapper.DcsUserMapper;
import com.xkcoding.rbac.shiro.model.entity.DcsUser;
import com.xkcoding.rbac.shiro.model.entity.UserRole;
import com.xkcoding.rbac.shiro.model.page.CommonPager;
import com.xkcoding.rbac.shiro.model.page.PageParameter;
import com.xkcoding.rbac.shiro.model.page.PageResultUtils;
import com.xkcoding.rbac.shiro.model.query.DcsUserQuery;
import com.xkcoding.rbac.shiro.model.vo.DcsUserVO;
import com.xkcoding.rbac.shiro.model.vo.LoginDcsUserVO;
import com.xkcoding.rbac.shiro.model.vo.RoleVO;
import com.xkcoding.rbac.shiro.service.DcsUserService;
import com.xkcoding.rbac.shiro.service.RoleService;
import com.xkcoding.rbac.shiro.service.UserRoleService;
import com.xkcoding.rbac.shiro.util.AesUtils;
import com.xkcoding.rbac.shiro.util.UUIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
public class DcsUserServiceImpl extends ServiceImpl<DcsUserMapper, DcsUser> implements DcsUserService {

    private static final Logger LOG = LoggerFactory.getLogger(DcsUserServiceImpl.class);

    @Autowired
    private SecretProperties secretProperties;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private JwtProperties jwtProperties;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean createOrUpdate(final DcsUserQuery dcsUserQuery) {
        DcsUser dcsUser = DcsUserQuery.createDO(dcsUserQuery);
        if (isEmpty(dcsUserQuery.getId())) {
            bindUserRole(dcsUserQuery.getId(), dcsUserQuery.getRoles());
        } else {
            if (!AuthConstants.ADMIN_NAME.equals(dcsUserQuery.getUserName())) {
                userRoleService.deleteByUserId(dcsUserQuery.getId());
            }
            if (!isEmpty(dcsUserQuery.getRoles())) {
                bindUserRole(dcsUserQuery.getId(), dcsUserQuery.getRoles());
            }
        }
        return save(dcsUser);
    }

    @Override
    public Integer delete(final List<String> ids) {
        int dashboardUserCount = 0;
        for (String id : ids) {
            DcsUser dcsUser = getById(id);
            if (dcsUser != null){
                if(AuthConstants.ADMIN_NAME.equals(dcsUser.getUserName())){
                    continue;
                }
                removeById(id);
                userRoleService.deleteByUserId(id);
                dashboardUserCount++;
            }
        }
        return dashboardUserCount;
    }

    @Override
    public DcsUserVO findById(final String id) {
        DcsUser dcsUser = getById(id);
        if(dcsUser==null){
            return null;
        }
        List<RoleVO> roles = new ArrayList<>();
        List<RoleVO> allRoles = new ArrayList<>();
        userRoleService.findByUserId(id)
            .forEach(userRole -> roles.add(RoleVO.buildRoleVO(roleService.getById(userRole.getRoleId()))));
        roleService.list().forEach(roleDO -> {
            allRoles.add(RoleVO.buildRoleVO(roleDO));
        });
        return DcsUserVO.buildDcsUserVO(dcsUser, roles, allRoles);
    }

    @Override
    public DcsUserVO findByUsernameAndPassword(final String userName, final String password) {
        DcsUser dcsUser = getOne(new QueryWrapper<DcsUser>().eq("user_name",userName).eq("password",password));
        return DcsUserVO.buildDcsUserVO(dcsUser,null,null);
    }


    @Override
    public DcsUser findByUserName(final String userName) {
        return getOne(new QueryWrapper<DcsUser>().eq("user_name",userName));
    }

    @Override
    public CommonPager<DcsUser> listByPage(final DcsUserQuery userDTO) {
        PageParameter pagePara = userDTO.getPageParameter();
        IPage<DcsUser> page = new Page(pagePara.getCurrentPage(),pagePara.getPageSize());
        String userName = userDTO.getUserName();
        page = page(page, new QueryWrapper<DcsUser>().eq(!isEmpty(userName),"user_name",userName));
        return PageResultUtils.result(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginDcsUserVO login(final String userName, final String password) {
        DcsUserVO dcsUserVO = loginByDatabase(userName, password);
        LoginDcsUserVO loginDcsUserVO = LoginDcsUserVO.buildLoginDcsUserVO(dcsUserVO);
        loginDcsUserVO.setToken(JwtUtils.generateToken(dcsUserVO.getUserName(),
            dcsUserVO.getPassword(), jwtProperties.getExpiredSeconds()));
        return loginDcsUserVO;
    }

    /**
     * 数据库登录
     *
     * @param userName userName
     * @param password password
     * @return
     */
    private DcsUserVO loginByDatabase(final String userName, final String password) {
        String key = secretProperties.getKey();
        String iv = secretProperties.getIv();
        DcsUserVO dcsUserVO = findByUsernameAndPassword(userName, AesUtils.aesEncryption(password, key, iv));
        return dcsUserVO;
    }

    /**
     * bind user and role id.
     *
     * @param userId user id
     * @param roleIds role ids.
     */
    private void bindUserRole(final String userId, final List<String> roleIds) {

        roleIds.forEach(item -> {
            UserRole userRole = new UserRole(userId, item);
            userRole.setId(UUIDUtils.getInstance().generateShortUuid());
            userRoleService.save(userRole);
        });
    }
}
