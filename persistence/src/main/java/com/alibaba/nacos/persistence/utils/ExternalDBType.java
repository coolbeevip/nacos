package com.alibaba.nacos.persistence.utils;

import com.alibaba.nacos.persistence.configuration.DatasourceConfiguration;
import com.alibaba.nacos.sys.env.EnvUtil;

public class ExternalDBType {
  public enum DBType {
    DERBY, MYSQL, ORACLE, POSTGRESQL
  }

  public static DBType dbType() {
    if ((EnvUtil.getStandaloneMode() && !DatasourceConfiguration.isUseExternalDB()) || DatasourceConfiguration
        .isEmbeddedStorage() || DatasourceConfiguration.getUseExternalDBDriverClassName()==null) {
      return DBType.DERBY;
    } else if (DatasourceConfiguration.getUseExternalDBDriverClassName().equals("com.mysql.cj.jdbc.Driver")) {
      return DBType.MYSQL;
    } else if (DatasourceConfiguration.getUseExternalDBDriverClassName().equals("oracle.jdbc.driver.OracleDriver") ||
        DatasourceConfiguration.getUseExternalDBDriverClassName().equals("oracle.jdbc.OracleDriver")) {
      return DBType.ORACLE;
    } else if (DatasourceConfiguration.getUseExternalDBDriverClassName().equals("org.postgresql.Driver")) {
      return DBType.POSTGRESQL;
    } else {
      return DBType.MYSQL;
    }
  }
}