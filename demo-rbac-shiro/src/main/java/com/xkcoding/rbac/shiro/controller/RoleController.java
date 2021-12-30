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
import com.xkcoding.rbac.shiro.model.entity.Role;
import com.xkcoding.rbac.shiro.model.page.CommonPager;
import com.xkcoding.rbac.shiro.model.page.PageParameter;
import com.xkcoding.rbac.shiro.model.query.RoleQuery;
import com.xkcoding.rbac.shiro.model.vo.RoleEditVO;
import com.xkcoding.rbac.shiro.service.RoleService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Optional;

/**
 * this is role controller.
 */
@Validated
@RestController
@RequestMapping("/role")
public class RoleController {

    private static final String SUPER = "super";

    private final RoleService roleService;

    public RoleController(final RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * get all roles.
     *
     * @return {@linkplain DcsResult}
     */
    @GetMapping("/getAllRoles")
    public DcsResult selectAll() {
        return DcsResult.success(DcsResultMessage.QUERY_SUCCESS, roleService.selectAll());
    }

    /**
     * query role.
     *
     * @param roleName    role name
     * @param currentPage current page
     * @param pageSize    page size
     * @return {@linkplain DcsResult}
     */
    @GetMapping("")
    public DcsResult queryRole(final String roleName, final Long currentPage, final Long pageSize) {
        RoleQuery roleQuery = new RoleQuery();
        roleQuery.setRoleName(roleName);
        roleQuery.setPageParameter(new PageParameter(currentPage, pageSize));
        CommonPager<Role> commonPager = roleService.listByPage(roleQuery);
        return DcsResult.success(DcsResultMessage.QUERY_SUCCESS, commonPager);
    }

    /**
     * detail role and permission info.
     *
     * @param id role id
     * @return {@linkplain DcsResult}
     */
    @GetMapping("/{id}")
    public DcsResult detailRole(@PathVariable("id") final String id) {
        RoleEditVO roleEditVO = roleService.findById(id);
        return Optional.ofNullable(roleEditVO)
                .map(item -> DcsResult.success(DcsResultMessage.DETAIL_SUCCESS, item))
                .orElse(DcsResult.error(DcsResultMessage.DETAIL_FAILED));
    }

    /**
     * create role.
     *
     * @param roleQuery role
     * @return {@linkplain DcsResult}
     */
    @PostMapping("")
    public DcsResult createRole(@Valid @RequestBody final RoleQuery roleQuery) {
        if (SUPER.equals(roleQuery.getRoleName())) {
            return DcsResult.error(DcsResultMessage.ROLE_CREATE_ERROR);
        }
        return DcsResult.success(DcsResultMessage.CREATE_SUCCESS, roleService.createOrUpdate(roleQuery));
    }

    /**
     * update role and permission info.
     *
     * @param id      primary key.
     * @param roleDTO role and permission info
     * @return {@linkplain DcsResult}
     */
    @PutMapping("/{id}")
    public DcsResult updateRole(@PathVariable("id") final String id, @Valid @RequestBody final RoleQuery roleDTO) {
        roleDTO.setId(id);
        return DcsResult.success(DcsResultMessage.UPDATE_SUCCESS, roleService.createOrUpdate(roleDTO));
    }

    /**
     * delete role info.
     *
     * @param ids primary keys.
     * @return {@linkplain DcsResult}
     */
    @DeleteMapping("/batch")
    public DcsResult deleteRole(@RequestBody @NotEmpty final List<@NotBlank String> ids) {
        return DcsResult.success(DcsResultMessage.DELETE_SUCCESS, roleService.delete(ids));
    }
}
