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

import cn.hutool.core.collection.CollectionUtil;
import com.xkcoding.rbac.shiro.common.DcsResultMessage;
import com.xkcoding.rbac.shiro.config.prop.SecretProperties;
import com.xkcoding.rbac.shiro.model.DcsResult;
import com.xkcoding.rbac.shiro.model.entity.DcsUser;
import com.xkcoding.rbac.shiro.model.page.CommonPager;
import com.xkcoding.rbac.shiro.model.page.PageParameter;
import com.xkcoding.rbac.shiro.model.query.DcsUserQuery;
import com.xkcoding.rbac.shiro.model.vo.DcsUserVO;
import com.xkcoding.rbac.shiro.service.DcsUserService;
import com.xkcoding.rbac.shiro.util.AesUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Optional;

/**
 * this is dashboard user controller.
 */
@Validated
@RestController
@RequestMapping("/dashboardUser")
public class DcsUserController {

    private final SecretProperties secretProperties;

    private final DcsUserService dcsUserService;

    public DcsUserController(final SecretProperties secretProperties, final DcsUserService dcsUserService) {
        this.secretProperties = secretProperties;
        this.dcsUserService = dcsUserService;
    }

    /**
     * query dashboard users.
     *
     * @param userName    user name
     * @param currentPage current page
     * @param pageSize    page size
     * @return {@linkplain DcsResult}
     */
    @GetMapping("")
    public DcsResult queryDashboardUsers(final String userName, final Long currentPage, final Long pageSize) {
        String key = secretProperties.getKey();
        String iv = secretProperties.getIv();
        CommonPager<DcsUser> commonPager = dcsUserService.listByPage(DcsUserQuery.builder().userName(userName).pageParameter(new PageParameter(currentPage, pageSize)).build());
        if (CollectionUtil.isNotEmpty(commonPager.getDataList())) {
            commonPager.getDataList()
                    .forEach(item -> item.setPassword(AesUtils.aesDecryption(item.getPassword(), key, iv)));
            return DcsResult.success(DcsResultMessage.QUERY_SUCCESS, commonPager);
        } else {
            return DcsResult.error(DcsResultMessage.DASHBOARD_QUERY_ERROR);
        }
    }

    /**
     * detail dashboard user.
     *
     * @param id dashboard user id.
     * @return {@linkplain DcsResult}
     */
    @GetMapping("/{id}")
    public DcsResult detailDashboardUser(@PathVariable("id") final String id) {
        DcsUserVO dashboardUserEditVO = dcsUserService.findById(id);
        return Optional.ofNullable(dashboardUserEditVO).map(item -> {
            item.setPassword("");
            return DcsResult.success(DcsResultMessage.DETAIL_SUCCESS, item);
        }).orElseGet(() -> DcsResult.error(DcsResultMessage.DASHBOARD_QUERY_ERROR));
    }

    /**
     * create dashboard user.
     *
     * @param dcsUserQuery dashboard user.
     * @return {@linkplain DcsResult}
     */
    @PostMapping("")
    public DcsResult createDashboardUser(@Valid @RequestBody final DcsUserQuery dcsUserQuery) {
        String key = secretProperties.getKey();
        String iv = secretProperties.getIv();
        return Optional.ofNullable(dcsUserQuery).map(item -> {
            item.setPassword(AesUtils.aesEncryption(item.getPassword(), key, iv));
            Boolean flag = dcsUserService.createOrUpdate(item);
            return DcsResult.success(DcsResultMessage.CREATE_SUCCESS, flag);
        }).orElseGet(() -> DcsResult.error(DcsResultMessage.DASHBOARD_CREATE_USER_ERROR));
    }

    /**
     * update dashboard user.
     *
     * @param id               primary key.
     * @param dcsUserQuery dashboard user.
     * @return {@linkplain DcsResult}
     */
    @PutMapping("/{id}")
    public DcsResult updateDashboardUser(@PathVariable("id") final String id, @Valid @RequestBody final DcsUserQuery dcsUserQuery) {
        String key = secretProperties.getKey();
        String iv = secretProperties.getIv();
        dcsUserQuery.setId(id);
        dcsUserQuery.setPassword(AesUtils.aesEncryption(dcsUserQuery.getPassword(), key, iv));
        Boolean flag = dcsUserService.createOrUpdate(dcsUserQuery);
        return DcsResult.success(DcsResultMessage.UPDATE_SUCCESS, flag);
    }

    /**
     * delete dashboard users.
     *
     * @param ids primary key.
     * @return {@linkplain DcsResult}
     */
    @DeleteMapping("/batch")
    public DcsResult deleteDashboardUser(@RequestBody @NotEmpty final List<@NotBlank String> ids) {
        Integer deleteCount = dcsUserService.delete(ids);
        return DcsResult.success(DcsResultMessage.DELETE_SUCCESS, deleteCount);
    }
}
