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
import com.xkcoding.rbac.shiro.common.DcsResult;
import com.xkcoding.rbac.shiro.common.DcsResultMessage;
import com.xkcoding.rbac.shiro.model.entity.Resource;
import com.xkcoding.rbac.shiro.model.page.CommonPager;
import com.xkcoding.rbac.shiro.model.page.PageParameter;
import com.xkcoding.rbac.shiro.model.query.ResourceQuery;
import com.xkcoding.rbac.shiro.model.vo.PermissionMenuVO;
import com.xkcoding.rbac.shiro.service.ResourceService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Optional;

/**
 * this is resource controller.
 */
@Validated
@RestController
@RequestMapping("/resource")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(final ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    /**
     * query resource.
     *
     * @param title resource title
     * @param currentPage current Page
     * @param pageSize page size
     * @return {@linkplain DcsResult}
     */
    @GetMapping("")
    public DcsResult queryResource(final String title, final Long currentPage, final Long pageSize) {
        CommonPager<Resource> commonPager = resourceService.listByPage(new ResourceQuery(title, new PageParameter(currentPage, pageSize)));
        if (CollectionUtil.isNotEmpty(commonPager.getDataList())) {
            return DcsResult.success(DcsResultMessage.QUERY_SUCCESS, commonPager);
        }
        return DcsResult.error(DcsResultMessage.QUERY_FAILED);
    }

    /**
     * get menu tree.
     *
     * @return {@linkplain DcsResult}
     */
    @GetMapping("/menu")
    public DcsResult getMenuTree() {
        List<PermissionMenuVO.MenuInfo> menuInfoList = resourceService.getMenuTree();
        if (CollectionUtil.isNotEmpty(menuInfoList)) {
            return DcsResult.success(DcsResultMessage.QUERY_SUCCESS, menuInfoList);
        }
        return DcsResult.error(DcsResultMessage.QUERY_FAILED);
    }

    /**
     * get button by parentId.
     *
     * @param id resource id
     * @return {@linkplain DcsResult}
     */
    @GetMapping("/button")
    public DcsResult getButton(final String id) {
        List<Resource> resourceVOList = resourceService.findByParentId(id);
        if (CollectionUtil.isNotEmpty(resourceVOList)) {
            return DcsResult.success(DcsResultMessage.QUERY_SUCCESS, resourceVOList);
        }
        return DcsResult.error(DcsResultMessage.QUERY_FAILED);
    }

    /**
     * detail resource info.
     *
     * @param id role id
     * @return {@linkplain DcsResult}
     */
    @GetMapping("/{id}")
    public DcsResult detailResource(@PathVariable("id") final String id) {
        return Optional.ofNullable(resourceService.findById(id))
                .map(item -> DcsResult.success(DcsResultMessage.DETAIL_SUCCESS, item))
                .orElse(DcsResult.error(DcsResultMessage.DETAIL_FAILED));
    }

    /**
     * create resource.
     *
     * @param resource resource dto
     * @return {@linkplain DcsResult}
     */
    @PostMapping("")
    public DcsResult createResource(@Valid @RequestBody final Resource resource) {
        return DcsResult.success(DcsResultMessage.CREATE_SUCCESS, resourceService.createOrUpdate(resource));
    }

    /**
     * update resource.
     *
     * @param id primary key.
     * @param resource resource info
     * @return {@linkplain DcsResult}
     */
    @PutMapping("/{id}")
    public DcsResult updateResource(@PathVariable("id") final String id, @Valid @RequestBody final Resource resource) {
        resource.setId(id);
        return DcsResult.success(DcsResultMessage.UPDATE_SUCCESS, resourceService.createOrUpdate(resource));
    }

    /**
     * delete resource info.
     *
     * @param ids primary keys.
     * @return {@linkplain DcsResult}
     */
    @DeleteMapping("/batch")
    public DcsResult deleteResource(@RequestBody @NotEmpty final List<@NotBlank String> ids) {
        return DcsResult.success(DcsResultMessage.DELETE_SUCCESS, resourceService.delete(ids));
    }
}
