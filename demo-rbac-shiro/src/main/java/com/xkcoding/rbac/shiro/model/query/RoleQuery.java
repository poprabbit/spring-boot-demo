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

package com.xkcoding.rbac.shiro.model.query;

import com.xkcoding.rbac.shiro.model.entity.Role;
import com.xkcoding.rbac.shiro.model.page.PageParameter;
import com.xkcoding.rbac.shiro.util.UUIDUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * The Role Query.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RoleQuery implements Serializable {

    private static final long serialVersionUID = -2876510433944603583L;

    /**
     * primary key.
     */
    private String id;

    /**
     * Role name.
     */
    private String roleName;

    /**
     * description.
     */
    private String description;

    /**
     * page parameter.
     */
    private PageParameter pageParameter;

    /**
     * pre permission ids.
     */
    private List<String> currentPermissionIds;

    public static Role createDO(final RoleQuery roleDTO) {
        return Optional.ofNullable(roleDTO).map(item -> {
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            Role role = new Role(roleDTO.getRoleName(), roleDTO.getDescription());
            role.setDateUpdated(currentTime);
            if (StringUtils.isEmpty(item.getId())) {
                role.setId(UUIDUtils.getInstance().generateShortUuid());
                role.setDateCreated(currentTime);
            } else {
                role.setId(item.getId());
            }
            return role;
        }).orElse(null);
    }

}
