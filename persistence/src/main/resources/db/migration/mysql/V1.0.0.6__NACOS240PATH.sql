alter table config_info modify encrypted_data_key varchar(1024) not null default '';
alter table config_info_beta modify encrypted_data_key varchar(1024) not null default '';
alter table his_config_info modify encrypted_data_key varchar(1024) not null default '';