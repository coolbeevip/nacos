-- Drop table config_info_aggr
BEGIN
EXECUTE IMMEDIATE 'DROP TABLE config_info_aggr';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
END IF;
END;
/

-- Add new table config_info_gray
CREATE TABLE config_info_gray (
    id NUMBER(38) not null primary key,
    data_id VARCHAR2(255) NOT NULL,
    group_id VARCHAR2(128) NOT NULL,
    tenant_id VARCHAR2(128) DEFAULT '',
    gray_name VARCHAR2(128) NOT NULL,
    gray_rule CLOB,
    app_name VARCHAR2(128),
    src_ip VARCHAR2(128),
    src_user VARCHAR2(128) DEFAULT '',
    content CLOB,
    md5 VARCHAR2(32) DEFAULT NULL,
    encrypted_data_key VARCHAR2(32) DEFAULT NULL,
    gmt_create   TIMESTAMP(6) default sysdate not null,
    gmt_modified TIMESTAMP(6) default sysdate not null,
    UNIQUE (data_id, group_id, tenant_id, gray_name)
);
CREATE SEQUENCE config_info_gray_id_seq
    INCREMENT BY 1
    START WITH 1
    NOMAXVALUE
       NOCYCLE;
CREATE TRIGGER config_info_gray_id_tr BEFORE
    insert ON config_info_gray FOR EACH ROW
begin
    select config_info_gray_id_seq.nextval into:New.id from dual;
end;
/

-- Support for gray release
ALTER TABLE his_config_info ADD publish_type VARCHAR2(50) DEFAULT 'formal';
ALTER TABLE his_config_info ADD ext_info CLOB DEFAULT NULL;
ALTER TABLE his_config_info ADD gray_name VARCHAR2(128) DEFAULT NULL;