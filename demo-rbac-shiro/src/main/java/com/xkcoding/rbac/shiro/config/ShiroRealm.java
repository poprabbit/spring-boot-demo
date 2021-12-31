/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xkcoding.rbac.shiro.config;


import com.xkcoding.rbac.shiro.config.bean.StatelessToken;
import com.xkcoding.rbac.shiro.config.prop.JwtUtils;
import com.xkcoding.rbac.shiro.model.custom.UserInfo;
import com.xkcoding.rbac.shiro.model.entity.DcsUser;
import com.xkcoding.rbac.shiro.service.DcsUserService;
import com.xkcoding.rbac.shiro.service.PermissionService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Set;

/**
 * shiro custom's realm.
 */
@Service("shiroRealm")
public class ShiroRealm extends AuthorizingRealm {

    private final PermissionService permissionService;

    private final DcsUserService dcsUserService;

    public ShiroRealm(final PermissionService permissionService, final DcsUserService dcsUserService) {
        this.permissionService = permissionService;
        this.dcsUserService = dcsUserService;
    }

    @Override
    public boolean supports(final AuthenticationToken token) {
        return token instanceof StatelessToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(final PrincipalCollection principalCollection) {
        UserInfo userInfo = (UserInfo) principalCollection.getPrimaryPrincipal();
        Set<String> permissions = permissionService.getAuthPermByUserName(userInfo.getUserName());
        if (CollectionUtils.isEmpty(permissions)) {
            return null;
        }
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.setStringPermissions(permissions);

        return simpleAuthorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(final AuthenticationToken authenticationToken) {
        String token = (String) authenticationToken.getCredentials();
        if (StringUtils.isEmpty(token)) {
            return null;
        }

        String userName = JwtUtils.getIssuer(token);
        if (StringUtils.isEmpty(userName)) {
            throw new AuthenticationException("userName is null");
        }

        DcsUser dcsUser = dcsUserService.findByUserName(userName);
        if (dcsUser == null) {
            throw new AuthenticationException(String.format("userName(%s) can not be found.", userName));
        }

        if (!JwtUtils.verifyToken(token, dcsUser.getPassword())) {
            throw new AuthenticationException("token is error.");
        }

        return new SimpleAuthenticationInfo(UserInfo.builder()
                .userName(userName)
                .userId(dcsUser.getId())
                .build(), token, this.getName());
    }
}
