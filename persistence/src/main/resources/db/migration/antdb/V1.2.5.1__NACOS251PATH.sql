-- Drop table config_info_aggr
DROP TABLE config_info_aggr;

-- Add new table config_info_gray
CREATE TABLE config_info_gray (
    id bigint NOT NULL,
    data_id varchar(255) NOT NULL,
    group_id varchar(128) NOT NULL,
    tenant_id varchar(128),
    gray_name varchar(128) NOT NULL,
    gray_rule text,
    app_name varchar(128),
    src_ip varchar(128),
    src_user text,
    content text,
    md5 varchar(32) DEFAULT NULL,
    encrypted_data_key varchar(32) DEFAULT NULL,
    gmt_create timestamp without time zone NOT NULL,
    gmt_modified timestamp without time zone NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (data_id,group_id,tenant_id,gray_name));

CREATE INDEX config_info_gray_dataid_gmt_modified ON config_info_gray(data_id,gmt_modified);
CREATE INDEX config_info_gray_gmt_modified ON config_info_gray(gmt_modified);

-- Support for gray release
ALTER TABLE his_config_info ADD publish_type varchar(50)  DEFAULT 'formal';
ALTER TABLE his_config_info ADD ext_info text   DEFAULT NULL ;
ALTER TABLE his_config_info ADD gray_name varchar(128) DEFAULT NULL;