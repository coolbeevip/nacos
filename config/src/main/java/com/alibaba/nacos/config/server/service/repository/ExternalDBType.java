package com.alibaba.nacos.config.server.service.repository;

import com.alibaba.nacos.config.server.utils.PropertyUtil;
import com.alibaba.nacos.sys.env.EnvUtil;

public class ExternalDBType {
  public enum DBType {
    DERBY, MYSQL, ORACLE, POSTGRESQL
  }

  public static DBType dbType() {
    if ((EnvUtil.getStandaloneMode() && !PropertyUtil.isUseExternalDB()) || PropertyUtil
        .isEmbeddedStorage()) {
      return DBType.DERBY;
    } else if (PropertyUtil.getUseExternalDBDriverClassName().equals("com.mysql.cj.jdbc.Driver")) {
      return DBType.MYSQL;
    } else if (PropertyUtil.getUseExternalDBDriverClassName().equals("oracle.jdbc.driver.OracleDriver")) {
      return DBType.ORACLE;
    } else if (PropertyUtil.getUseExternalDBDriverClassName().equals("org.postgresql.Driver")) {
      return DBType.POSTGRESQL;
    } else {
      return DBType.MYSQL;
    }
  }
}
