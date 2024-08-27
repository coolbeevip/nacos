/*
 * Copyright 1999-2022 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.nacos.plugin.datasource.impl.oracle;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.alibaba.nacos.plugin.datasource.constants.DataSourceConstant;
import com.alibaba.nacos.plugin.datasource.constants.FieldConstant;
import com.alibaba.nacos.plugin.datasource.mapper.ConfigInfoGrayMapper;
import com.alibaba.nacos.plugin.datasource.model.MapperContext;
import com.alibaba.nacos.plugin.datasource.model.MapperResult;

import java.util.Collections;
import java.util.List;

/**
 * The postgresql implementation of ConfigInfoAggrMapper.
 *
 **/
public class ConfigInfoGrayMapperByOracle extends AbstractMapperByOracle implements ConfigInfoGrayMapper {

    @Override
    public MapperResult findAllConfigInfoGrayForDumpAllFetchRows(MapperContext context) {
        int startRow = context.getStartRow();
        int pageSize = context.getPageSize();
        int endRow = startRow + pageSize;

        String sql = "SELECT id, data_id, group_id, tenant_id, gray_name, gray_rule, app_name, content, md5, gmt_modified "
                + "FROM ("
                + "    SELECT id, data_id, group_id, tenant_id, gray_name, gray_rule, app_name, content, md5, gmt_modified, "
                + "           ROWNUM as rn "
                + "    FROM ("
                + "        SELECT id, data_id, group_id, tenant_id, gray_name, gray_rule, app_name, content, md5, gmt_modified "
                + "        FROM config_info_gray "
                + "        ORDER BY id "
                + "    ) "
                + "    WHERE ROWNUM <= " + endRow
                + ") "
                + "WHERE rn > " + startRow;

        return new MapperResult(sql, Collections.emptyList());
    }

    @Override
    public MapperResult findChangeConfig(MapperContext context) {
        String sql = "SELECT * FROM ("
                + "  SELECT id, data_id, group_id, tenant_id, app_name, content, gray_name, gray_rule, "
                + "  gmt_modified, encrypted_data_key "
                + "  FROM config_info_gray "
                + "  WHERE gmt_modified >= ? AND id > ? "
                + "  ORDER BY id"
                + ") WHERE ROWNUM <= ?";

        return new MapperResult(sql, CollectionUtils.list(context.getWhereParameter(FieldConstant.START_TIME),
                context.getWhereParameter(FieldConstant.LAST_MAX_ID),
                context.getWhereParameter(FieldConstant.PAGE_SIZE)));
    }

    @Override
    public String getDataSource() {
        return DataSourceConstant.ORACLE;
    }
}
