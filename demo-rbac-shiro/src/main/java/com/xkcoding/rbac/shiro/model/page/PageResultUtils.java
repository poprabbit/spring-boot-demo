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

package com.xkcoding.rbac.shiro.model.page;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.Collections;

/**
 * The type Page result utils.
 */
public class PageResultUtils {


    public static <T> CommonPager<T> result(final IPage<T> page) {
        Long count = page.getTotal();
        if (count != null && count > 0) {
            return new CommonPager<>(convert(page), page.getRecords());
        }
        return new CommonPager<>(convert(page), Collections.emptyList());
    }


    public static PageParameter convert(final IPage<?> page) {
        PageParameter pageParameter = new PageParameter();
        pageParameter.setCurrentPage(page.getCurrent());
        pageParameter.setPageSize(page.getSize());
        pageParameter.setTotalPage(page.getPages());
        pageParameter.setTotalCount(page.getTotal());
        pageParameter.setOffset(page.getSize());
        return pageParameter;
    }
}
