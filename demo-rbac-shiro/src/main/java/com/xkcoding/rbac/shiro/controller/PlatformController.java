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

package com.xkcoding.rbac.shiro.controller;

import com.xkcoding.rbac.shiro.common.DcsResult;
import com.xkcoding.rbac.shiro.common.DcsResultMessage;
import com.xkcoding.rbac.shiro.model.page.CommonPager;
import com.xkcoding.rbac.shiro.model.page.PageParameter;
import com.xkcoding.rbac.shiro.model.vo.LoginDcsUserVO;
import com.xkcoding.rbac.shiro.service.DcsUserService;
import com.xkcoding.rbac.shiro.service.EnumService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Optional;

/**
 * this is platform controller.
 */
@RestController
@RequestMapping("")
public class PlatformController {

    private final DcsUserService dcsUserService;

    private final EnumService enumService;

    public PlatformController(final DcsUserService dcsUserService, final EnumService enumService) {
        this.dcsUserService = dcsUserService;
        this.enumService = enumService;
    }

    /**
     * login dashboard user.
     *
     * @param userName user name
     * @param password user password
     * @return {@linkplain DcsResult}
     */
    @GetMapping("/platform/login")
    public DcsResult loginDashboardUser(final String userName, final String password) {
        LoginDcsUserVO loginVO = dcsUserService.login(userName, password);
        return Optional.ofNullable(loginVO).map(item -> DcsResult.success(DcsResultMessage.PLATFORM_LOGIN_SUCCESS, loginVO))
                .orElse(DcsResult.error(DcsResultMessage.PLATFORM_LOGIN_ERROR));
    }

    /**
     * query enums.
     *
     * @return {@linkplain DcsResult}
     */
    @GetMapping("/platform/enum")
    public DcsResult queryEnums() {
        return DcsResult.success(enumService.list());
    }

    @GetMapping("/plugin")
    public DcsResult queryPlugins(final String name, final Integer enabled, final Integer currentPage, final Integer pageSize) {
        return DcsResult.success(DcsResultMessage.QUERY_SUCCESS, new CommonPager<>(new PageParameter(), new ArrayList<>()));
    }

    /**
     * Index string.
     *
     * @param model the model
     * @return the string
     */
    @RequestMapping("/index")
    public String index(final Model model) {
        model.addAttribute("domain", "http://localhost:9095");
        return "index";
    }
}
