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

import com.alibaba.nacos.common.utils.NamespaceUtil;
import com.alibaba.nacos.common.utils.StringUtils;
import com.alibaba.nacos.plugin.datasource.constants.DataSourceConstant;
import com.alibaba.nacos.plugin.datasource.mapper.AbstractMapper;
import com.alibaba.nacos.plugin.datasource.mapper.ConfigInfoMapper;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * The Oracle implementation of ConfigInfoMapper.
 *
 **/

public class ConfigInfoMapperByOracle extends AbstractMapper implements ConfigInfoMapper {

    @Override
    public String select(List<String> columns, List<String> where) {
        StringBuilder sql = new StringBuilder();
        String method = "SELECT ";
        sql.append(method);
        for (int i = 0; i < columns.size(); i++) {
            sql.append(columns.get(i));
            if (i == columns.size() - 1) {
                sql.append(" ");
            } else {
                sql.append(",");
            }
        }
        sql.append("FROM ");
        sql.append(getTableName());
        sql.append(" ");

        if (where.size() == 0) {
            return sql.toString();
        }

        sql.append("WHERE ");
        for (int i = 0; i < where.size(); i++) {
            sql.append("nvl(" + where.get(i) + ", 'public')").append(" = ").append("nvl(?, 'public')");
            if (i != where.size() - 1) {
                sql.append(" AND ");
            }
        }
        return sql.toString();
    }

    @Override
    public String update(List<String> columns, List<String> where) {
        StringBuilder sql = new StringBuilder();
        String method = "UPDATE ";
        sql.append(method);
        sql.append(getTableName()).append(" ").append("SET ");

        for (int i = 0; i < columns.size(); i++) {
            sql.append(columns.get(i)).append(" = ").append("?");
            if (i != columns.size() - 1) {
                sql.append(",");
            }
        }

        if (where.size() == 0) {
            return sql.toString();
        }

        sql.append(" WHERE ");

        for (int i = 0; i < where.size(); i++) {
            sql.append("nvl(" + where.get(i) + ", 'public')").append(" = ").append("nvl(?, 'public')");
            if (i != where.size() - 1) {
                sql.append(" AND ");
            }
        }
        return sql.toString();
    }

    @Override
    public String delete(List<String> params) {
        StringBuilder sql = new StringBuilder();
        String method = "DELETE ";
        sql.append(method).append("FROM ").append(getTableName()).append(" ").append("WHERE ");
        for (int i = 0; i < params.size(); i++) {
            sql.append("nvl(" + params.get(i) + ", 'public')").append(" ").append("=").append(" nvl(?, 'public') ");
            if (i != params.size() - 1) {
                sql.append("AND ");
            }
        }

        return sql.toString();
    }

    @Override
    public String count(List<String> where) {
        StringBuilder sql = new StringBuilder();
        String method = "SELECT ";
        sql.append(method);
        sql.append("COUNT(*) FROM ");
        sql.append(getTableName());
        sql.append(" ");

        if (null == where || where.size() == 0) {
            return sql.toString();
        }

        sql.append("WHERE ");
        for (int i = 0; i < where.size(); i++) {
            sql.append("nvl(" + where.get(i) + ", 'public')").append(" = ").append("nvl(?, 'public')");
            if (i != where.size() - 1) {
                sql.append(" AND ");
            }
        }
        return sql.toString();
    }

    @Override
    public String findConfigInfoByAppFetchRows(int startRow, int pageSize) {
        return "SELECT ID,data_id,group_id,tenant_id,app_name,content FROM config_info WHERE nvl(tenant_id,'public') = nvl(?,'public') AND "
                + "app_name = ?" + " OFFSET " + startRow + " ROWS FETCH NEXT " + pageSize + " ROWS ONLY";
    }

    @Override
    public String getTenantIdList(int startRow, int pageSize) {
        return "SELECT tenant_id FROM config_info WHERE tenant_id != '" + NamespaceUtil.getNamespaceDefaultId()
                + "' GROUP BY tenant_id OFFSET " + startRow
                + " ROWS FETCH NEXT " + pageSize + " ROWS ONLY";
    }

    @Override
    public String getGroupIdList(int startRow, int pageSize) {
        return "SELECT group_id FROM config_info WHERE tenant_id ='" + NamespaceUtil.getNamespaceDefaultId()
                + "' GROUP BY group_id OFFSET " + startRow
                + " ROWS FETCH NEXT " + pageSize + " ROWS ONLY";
    }

    @Override
    public String findAllConfigKey(int startRow, int pageSize) {
        return " SELECT data_id,group_id,app_name FROM "
                + " ( SELECT id FROM config_info WHERE nvl(tenant_id,'public') = nvl(?,'public') ORDER BY id OFFSET " + startRow
                + " ROWS FETCH NEXT " + pageSize + " ROWS ONLY ) " + "g, config_info t  WHERE g.id = t.id ";
    }

    @Override
    public String findAllConfigInfoBaseFetchRows(int startRow, int pageSize) {
        return "SELECT t.id,data_id,group_id,content,md5 " + " FROM ( SELECT id FROM config_info ORDER BY id OFFSET "
                + startRow + " ROWS FETCH NEXT " + pageSize + " ROWS ONLY )  " + " g, config_info t WHERE g.id = t.id ";
    }

    @Override
    public String findAllConfigInfoFragment(int startRow, int pageSize) {
        return "SELECT id,data_id,group_id,tenant_id,app_name,content,md5,gmt_modified,type FROM config_info WHERE id > ? "
                + "ORDER BY id ASC OFFSET " + startRow + " ROWS FETCH NEXT " + pageSize + " ROWS ONLY";
    }

    @Override
    public String findChangeConfigFetchRows(Map<String, String> params, final Timestamp startTime,
                                            final Timestamp endTime, int startRow, int pageSize, long lastMaxId) {
        final String tenant = params.get(TENANT);
        final String dataId = params.get(DATA_ID);
        final String group = params.get(GROUP);
        final String appName = params.get(APP_NAME);
        final String sqlFetchRows = "SELECT id,data_id,group_id,tenant_id,app_name,content,type,md5,gmt_modified FROM"
                + " config_info WHERE ";
        String where = " 1=1 ";

        if (!StringUtils.isBlank(dataId)) {
            where += " AND data_id LIKE ? ";
        }
        if (!StringUtils.isBlank(group)) {
            where += " AND group_id LIKE ? ";
        }

        if (!StringUtils.isBlank(tenant)) {
            where += " AND tenant_id = ? ";
        }

        if (!StringUtils.isBlank(appName)) {
            where += " AND app_name = ? ";
        }
        if (startTime != null) {
            where += " AND gmt_modified >=? ";
        }
        if (endTime != null) {
            where += " AND gmt_modified <=? ";
        }
        return sqlFetchRows + where + " OFFSET " + startRow + " ROWS FETCH NEXT " + pageSize + " ROWS ONLY";
    }

    @Override
    public String listGroupKeyMd5ByPageFetchRows(int startRow, int pageSize) {
        return " SELECT t.id,data_id,group_id,tenant_id,app_name,type,md5,gmt_modified "
                + "FROM ( SELECT id FROM config_info ORDER BY id OFFSET " + startRow + " ROWS FETCH NEXT " + pageSize
                + " ROWS ONLY ) g, config_info t WHERE g.id = t.id";
    }

    @Override
    public String findConfigInfoBaseLikeFetchRows(Map<String, String> params, int startRow, int pageSize) {
        final String sqlFetchRows = "SELECT id,data_id,group_id,tenant_id,content FROM config_info WHERE ";
        String where = " 1=1 AND tenant_id='"
                + NamespaceUtil.getNamespaceDefaultId()
                + "' ";
        if (!StringUtils.isBlank(params.get(DATA_ID))) {
            where += " AND data_id LIKE ? ";
        }
        if (!StringUtils.isBlank(params.get(GROUP))) {
            where += " AND group_id LIKE ? ";
        }
        if (!StringUtils.isBlank(params.get(CONTENT))) {
            where += " AND content LIKE ? ";
        }
        return sqlFetchRows + where + " OFFSET " + startRow + " ROWS FETCH NEXT " + pageSize + " ROWS ONLY";
    }

    @Override
    public String findConfigInfo4PageFetchRows(Map<String, String> params, int startRow, int pageSize) {
        final String appName = params.get(APP_NAME);
        final String dataId = params.get(DATA_ID);
        final String group = params.get(GROUP);
        final String content = params.get(CONTENT);
        final String sql = "SELECT id,data_id,group_id,tenant_id,app_name,content,type FROM config_info";
        StringBuilder where = new StringBuilder(" WHERE ");
        where.append(" nvl(tenant_id,'public') = nvl(?,'public') ");
        if (StringUtils.isNotBlank(dataId)) {
            where.append(" AND data_id=? ");
        }
        if (StringUtils.isNotBlank(group)) {
            where.append(" AND group_id=? ");
        }
        if (StringUtils.isNotBlank(appName)) {
            where.append(" AND app_name=? ");
        }
        if (!StringUtils.isBlank(content)) {
            where.append(" AND content LIKE ? ");
        }
        return sql + where + " OFFSET " + startRow + " ROWS FETCH NEXT " + pageSize + " ROWS ONLY";
    }

    @Override
    public String findConfigInfoBaseByGroupFetchRows(int startRow, int pageSize) {
        return "SELECT id,data_id,group_id,content FROM config_info WHERE group_id=? " + "AND nvl(tenant_id,'public') = nvl(?,'public') " + " OFFSET "
                + startRow + " ROWS FETCH NEXT " + pageSize + " ROWS ONLY";
    }

    @Override
    public String findConfigInfoLike4PageCountRows(Map<String, String> params) {
        final String appName = params.get("appName");
        final String content = params.get("content");
        final String dataId = params.get("dataId");
        final String group = params.get(GROUP);
        final String sqlCountRows = "SELECT count(*) FROM config_info";
        StringBuilder where = new StringBuilder(" WHERE ");
        where.append(" nvl(tenant_id,'public') = nvl(?,'public') ");
        if (!StringUtils.isBlank(dataId)) {
            where.append(" AND data_id LIKE ? ");
        }
        if (!StringUtils.isBlank(group)) {
            where.append(" AND group_id LIKE ? ");
        }
        if (!StringUtils.isBlank(appName)) {
            where.append(" AND app_name = ? ");
        }
        if (!StringUtils.isBlank(content)) {
            where.append(" AND content LIKE ? ");
        }
        return sqlCountRows + where;
    }

    @Override
    public String findConfigInfoLike4PageFetchRows(Map<String, String> params, int startRow, int pageSize) {
        final String appName = params.get("appName");
        final String content = params.get("content");
        final String dataId = params.get(DATA_ID);
        final String group = params.get(GROUP);
        final String sqlFetchRows = "SELECT id,data_id,group_id,tenant_id,app_name,content,encrypted_data_key FROM config_info";
        StringBuilder where = new StringBuilder(" WHERE ");
        where.append(" nvl(tenant_id,'public') = nvl(?,'public') ");
        if (!StringUtils.isBlank(dataId)) {
            where.append(" AND data_id LIKE ? ");
        }
        if (!StringUtils.isBlank(group)) {
            where.append(" AND group_id LIKE ? ");
        }
        if (!StringUtils.isBlank(appName)) {
            where.append(" AND app_name = ? ");
        }
        if (!StringUtils.isBlank(content)) {
            where.append(" AND content LIKE ? ");
        }
        return sqlFetchRows + where + " OFFSET " + startRow + " ROWS FETCH NEXT " + pageSize + " ROWS ONLY";
    }

    @Override
    public String findAllConfigInfoFetchRows(int startRow, int pageSize) {
        return " SELECT t.id,data_id,group_id,tenant_id,app_name,content,md5 "
                + " FROM ( SELECT id FROM config_info  WHERE nvl(tenant_id,'public') = nvl(?,'public') ORDER BY id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY )"
                + " g, config_info t  WHERE g.id = t.id ";
    }

    @Override
    public String getDataSource() {
        return DataSourceConstant.ORACLE;
    }
}
