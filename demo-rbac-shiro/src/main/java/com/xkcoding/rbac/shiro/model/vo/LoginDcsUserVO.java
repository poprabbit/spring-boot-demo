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

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.Optional;

/**
 * login dashboard return user info's vo.
 */
@Data
@NoArgsConstructor
public class LoginDcsUserVO extends DcsUserVO {

    private static final long serialVersionUID = -411996250594776944L;

    /**
     * token.
     */
    private String token;


    /**
     * build LoginDcsUserVO.
     *
     * @param dcsUserVO {@linkplain DcsUserVO}
     * @return {@linkplain LoginDcsUserVO}
     */
    public static LoginDcsUserVO buildLoginDcsUserVO(final DcsUserVO dcsUserVO) {
        return Optional.ofNullable(dcsUserVO)
                .map(item -> {
                    LoginDcsUserVO vo = new LoginDcsUserVO();
                    BeanUtils.copyProperties(item, vo);
                    return vo;
                }).orElse(null);
    }
}
