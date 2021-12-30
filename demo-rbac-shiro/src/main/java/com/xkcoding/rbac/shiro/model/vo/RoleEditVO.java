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

package com.xkcoding.rbac.shiro.model.vo;

import com.xkcoding.rbac.shiro.model.entity.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * this is role edit for web front.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleEditVO implements Serializable {

    private static final long serialVersionUID = -292613858092450065L;

    /**
     * role have permission list.
     */
    private List<String> rolePermissionList;

    /**
     * role info.
     */
    private RoleVO sysRole;

    /**
     *  all permission info.
     */
    private PermissionInfo allPermissionInfo;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PermissionInfo {

        /**
         * permission tree list.
         */
        private List<ResourceInfo> treeList;

        /**
         * permission ids.
         */
        private List<String> permissionIds;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResourceInfo {

        /**
         * resource id.
         */
        private String id;

        /**
         * resource title.
         */
        private String title;

        /**
         * resource name.
         */
        private String name;

        /**
         * resource children.
         */
        private List<ResourceInfo> children;

        /**
         * resource leaf.
         */
        private Boolean isLeaf;

        /**
         * resource parentId.
         */
        private String parentId;

        /**
         * build resource info.
         *
         * @param resourceVO {@linkplain Resource}
         * @return {@linkplain ResourceInfo}
         */
        public static ResourceInfo buildResourceInfo(final Resource resourceVO) {
            return Optional.ofNullable(resourceVO).map(item -> {
                ResourceInfo resourceInfo = ResourceInfo.builder()
                        .id(item.getId())
                        .title(item.getTitle())
                        .name(item.getName())
                        .parentId(item.getParentId())
                        .isLeaf(item.getIsLeaf())
                        .build();
                if (item.getIsLeaf().equals(Boolean.FALSE)) {
                    resourceInfo.setChildren(new ArrayList<>());
                }
                return resourceInfo;
            }).orElse(null);
        }

        /**
         * builder method.
         *
         * @return builder object.
         */
        public static ResourceInfoBuilder builder() {
            return new ResourceInfoBuilder();
        }

        public static final class ResourceInfoBuilder {

            /**
             * resource id.
             */
            private String id;

            /**
             * resource title.
             */
            private String title;

            /**
             * resource name.
             */
            private String name;

            /**
             * resource children.
             */
            private List<ResourceInfo> children;

            /**
             * resource leaf.
             */
            private Boolean isLeaf;

            /**
             * resource parentId.
             */
            private String parentId;

            public ResourceInfoBuilder() {
            }

            public ResourceInfoBuilder(final String id, final String title, final String name, final List<ResourceInfo> children, final Boolean isLeaf, final String parentId) {
                this.id = id;
                this.title = title;
                this.name = name;
                this.children = children;
                this.isLeaf = isLeaf;
                this.parentId = parentId;
            }

            /**
             * id.
             *
             * @param id the id.
             * @return ResourceInfoBuilder.
             */
            public ResourceInfoBuilder id(final String id) {
                this.id = id;
                return this;
            }

            /**
             * title.
             *
             * @param title the title.
             * @return ResourceInfoBuilder.
             */
            public ResourceInfoBuilder title(final String title) {
                this.title = title;
                return this;
            }

            /**
             * name.
             *
             * @param name the name.
             * @return ResourceInfoBuilder.
             */
            public ResourceInfoBuilder name(final String name) {
                this.name = name;
                return this;
            }

            /**
             * children.
             *
             * @param children the children.
             * @return ResourceInfoBuilder.
             */
            public ResourceInfoBuilder children(final List<ResourceInfo> children) {
                this.children = children;
                return this;
            }

            /**
             * isLeaf.
             *
             * @param isLeaf the isLeaf.
             * @return ResourceInfoBuilder.
             */
            public ResourceInfoBuilder isLeaf(final Boolean isLeaf) {
                this.isLeaf = isLeaf;
                return this;
            }

            /**
             * parentId.
             *
             * @param parentId the parentId.
             * @return ResourceInfoBuilder.
             */
            public ResourceInfoBuilder parentId(final String parentId) {
                this.parentId = parentId;
                return this;
            }

            /**
             * build method.
             *
             * @return build object.
             */
            public ResourceInfo build() {
                ResourceInfo resourceInfo = new ResourceInfo(id, title, name, children, isLeaf, parentId);
                return resourceInfo;
            }
        }
    }

}
