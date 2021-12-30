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


import com.xkcoding.rbac.shiro.model.entity.DcsUser;
import com.xkcoding.rbac.shiro.model.page.PageParameter;
import com.xkcoding.rbac.shiro.util.UUIDUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * this is dashboard user from by web front.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DcsUserQuery implements Serializable {

    private static final long serialVersionUID = -7005615329360835626L;

    /**
     * primary key.
     */
    private String id;

    /**
     * user name.
     */
    @NotNull
    private String userName;

    /**
     * user password.
     */
    private String password;

    /**
     * current role list.
     */
    private List<String> roles;

    /**
     * whether enabled.
     */
    private Boolean enabled;

    /**
     * page parameter.
     */
    private PageParameter pageParameter;


    public static DcsUser convert2DO(final DcsUserQuery dcsUserQuery) {
        return Optional.ofNullable(dcsUserQuery).map(item -> {
            DcsUser dcsUser = DcsUser.builder()
                .userName(item.getUserName())
                .password(item.getPassword())
                .enabled(item.getEnabled()).build();
            dcsUser.setId(dcsUser.getId());
            return dcsUser;
        }).orElse(null);
    }

    public static DcsUser createDO(final DcsUserQuery dcsUserQuery) {
        return Optional.ofNullable(dcsUserQuery).map(item -> {
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            DcsUser dcsUser =  DcsUser.builder()
                .userName(item.getUserName())
                .password(item.getPassword()).build();
            dcsUser.setDateUpdated(currentTime);
            if (StringUtils.isEmpty(item.getId())) {
                dcsUser.setId(UUIDUtils.getInstance().generateShortUuid());
                dcsUser.setEnabled(true);
                dcsUser.setDateCreated(currentTime);
            } else {
                dcsUser.setId(item.getId());
                dcsUser.setEnabled(item.getEnabled());
            }
            return dcsUser;
        }).orElse(null);
    }

}
