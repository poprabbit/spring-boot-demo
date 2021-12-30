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

import com.xkcoding.rbac.shiro.model.entity.DcsUser;
import com.xkcoding.rbac.shiro.util.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DcsUserVO implements Serializable {

    private static final long serialVersionUID = -6493573459758664389L;

    /**
     * primary key.
     */
    private String id;

    /**
     * user name.
     */
    private String userName;

    /**
     * user password.
     */
    private String password;

    /**
     * whether enabled.
     */
    private Boolean enabled;

    /**
     * created time.
     */
    private String dateCreated;

    /**
     * updated time.
     */
    private String dateUpdated;

    /**
     * user role list.
     */
    private List<RoleVO> roles;

    /**
     * all role list.
     */
    private List<RoleVO> allRoles;

    public DcsUserVO(String id, String userName, String password, Boolean enabled, String dateCreated, String dateUpdated) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.enabled = enabled;
        this.dateCreated = dateCreated;
        this.dateUpdated = dateUpdated;
    }

    public static DcsUserVO buildDcsUserVO(final DcsUser dcsUser, final List<RoleVO> roles, final List<RoleVO> allRoles) {
        return Optional.ofNullable(dcsUser)
                .map(item -> {
                    DcsUserVO userVO = new DcsUserVO(item.getId(), item.getUserName(),
                            item.getPassword(), item.getEnabled(),
                            DateUtils.localDateTimeToString(item.getDateCreated().toLocalDateTime()),
                            DateUtils.localDateTimeToString(item.getDateUpdated().toLocalDateTime()));
                    userVO.setRoles(roles);
                    userVO.setAllRoles(allRoles);
                    return userVO;
                }).orElse(null);
    }

}
