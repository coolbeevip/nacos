-- Drop table config_info_aggr
DROP TABLE IF EXISTS config_info_aggr;

-- Add new table config_info_gray
CREATE TABLE config_info_gray (
    id BIGSERIAL PRIMARY KEY,
    data_id VARCHAR(255) NOT NULL,
    group_id VARCHAR(128) NOT NULL,
    tenant_id VARCHAR(128) DEFAULT '',
    gray_name VARCHAR(128) NOT NULL,
    gray_rule TEXT,
    app_name VARCHAR(128),
    src_ip VARCHAR(128),
    src_user VARCHAR(128) DEFAULT '',
    content TEXT,
    md5 VARCHAR(32) DEFAULT NULL,
    encrypted_data_key VARCHAR(32) DEFAULT NULL,
    gmt_create TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT '2010-05-05 00:00:00',
    gmt_modified TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT '2010-05-05 00:00:00',
    CONSTRAINT uk_configinfogray_datagrouptenantgrayname UNIQUE (data_id, group_id, tenant_id, gray_name)
);

CREATE INDEX config_info_gray_dataid_gmt_modified ON config_info_gray (data_id, gmt_modified);
CREATE INDEX config_info_gray_gmt_modified ON config_info_gray (gmt_modified);

-- Support for gray release
ALTER TABLE his_config_info ADD COLUMN publish_type VARCHAR(50) DEFAULT 'formal';
ALTER TABLE his_config_info ADD COLUMN ext_info TEXT DEFAULT NULL;
ALTER TABLE his_config_info ADD COLUMN gray_name VARCHAR(128) DEFAULT NULL;