alter table config_info alter column encrypted_data_key type varchar(1024) using encrypted_data_key::varchar(1024);
alter table config_info_beta alter column encrypted_data_key type varchar(1024) using encrypted_data_key::varchar(1024);
alter table his_config_info alter column encrypted_data_key type varchar(1024) using encrypted_data_key::varchar(1024);