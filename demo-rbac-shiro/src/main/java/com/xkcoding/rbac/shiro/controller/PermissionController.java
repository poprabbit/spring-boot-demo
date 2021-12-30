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
import com.xkcoding.rbac.shiro.model.vo.PermissionMenuVO;
import com.xkcoding.rbac.shiro.service.PermissionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * this is permission controller.
 */
@RestController
@RequestMapping("/permission")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(final PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    /**
     * get menu by token.
     *
     * @param token login success ack token
     * @return {@linkplain DcsResult}
     */
    @GetMapping("/getUserPermissionByToken")
    public DcsResult getUserPermissionByToken(@RequestParam(name = "token") final String token) {
        PermissionMenuVO permissionMenuVO = permissionService.getPermissionMenu(token);
        return Optional.ofNullable(permissionMenuVO)
                .map(item -> DcsResult.success(DcsResultMessage.MENU_SUCCESS, item))
                .orElseGet(() -> DcsResult.error(DcsResultMessage.MENU_FAILED));
    }
}
