-- 中瀚安全云平台业务表结构脚本。
-- 用途：全新环境初始化 AQY 业务表，或在老环境中补齐缺失业务表。
-- 安全性：全文件只使用 CREATE TABLE IF NOT EXISTS，不会删除、清空或覆盖已有表数据。
-- 注意：老环境升级前仍应先备份数据库；已有表不会被 ALTER，本脚本不会修改已有表结构。

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS aqy_project (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) DEFAULT NULL,
  project_type VARCHAR(100) DEFAULT NULL,
  city VARCHAR(100) DEFAULT NULL,
  company_name VARCHAR(255) DEFAULT NULL,
  project_desc TEXT,
  longitude DECIMAL(18, 8) DEFAULT NULL,
  latitude DECIMAL(18, 8) DEFAULT NULL,
  elevation DECIMAL(18, 4) DEFAULT NULL,
  course_angle DECIMAL(18, 4) DEFAULT NULL,
  depression_angle DECIMAL(18, 4) DEFAULT NULL,
  yz_company VARCHAR(255) DEFAULT NULL,
  js_company VARCHAR(255) DEFAULT NULL,
  project_start_date DATETIME DEFAULT NULL,
  create_time DATETIME DEFAULT NULL,
  create_uid BIGINT DEFAULT NULL,
  is_delete TINYINT DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_aqy_project_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目';

CREATE TABLE IF NOT EXISTS aqy_equipment_type (
  id BIGINT NOT NULL AUTO_INCREMENT,
  eqmt_type_name VARCHAR(255) DEFAULT NULL,
  eqmt_type_symbol VARCHAR(100) DEFAULT NULL,
  upload_intv INT DEFAULT NULL,
  plus_intv INT DEFAULT NULL,
  create_time DATETIME DEFAULT NULL,
  create_uid BIGINT DEFAULT NULL,
  is_delete TINYINT DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_aqy_equipment_type_symbol (eqmt_type_symbol)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备类型';

CREATE TABLE IF NOT EXISTS aqy_gatway_equipment (
  id BIGINT NOT NULL AUTO_INCREMENT,
  project_id BIGINT DEFAULT NULL,
  gatway_name VARCHAR(255) DEFAULT NULL,
  gatway_code VARCHAR(100) DEFAULT NULL,
  longitude DECIMAL(18, 8) DEFAULT NULL,
  latitude DECIMAL(18, 8) DEFAULT NULL,
  create_time DATETIME DEFAULT NULL,
  create_uid BIGINT DEFAULT NULL,
  is_delete TINYINT DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_aqy_gatway_project (project_id),
  KEY idx_aqy_gatway_code (gatway_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='网关设备';

CREATE TABLE IF NOT EXISTS aqy_equipment (
  id BIGINT NOT NULL AUTO_INCREMENT,
  project_id BIGINT DEFAULT NULL,
  gatway_id BIGINT DEFAULT NULL,
  eqmt_type_id BIGINT DEFAULT NULL,
  eqmt_name VARCHAR(255) DEFAULT NULL,
  eqmt_code VARCHAR(100) DEFAULT NULL,
  sort_num INT DEFAULT NULL,
  longitude DECIMAL(18, 8) DEFAULT NULL,
  latitude DECIMAL(18, 8) DEFAULT NULL,
  elevation DECIMAL(18, 4) DEFAULT NULL,
  azimuth_angle DECIMAL(18, 4) DEFAULT NULL,
  initial_x DECIMAL(18, 6) DEFAULT NULL,
  initial_y DECIMAL(18, 6) DEFAULT NULL,
  initial_h DECIMAL(18, 6) DEFAULT NULL,
  accumulative_change_value_x DECIMAL(18, 6) DEFAULT NULL,
  accumulative_change_value_y DECIMAL(18, 6) DEFAULT NULL,
  accumulative_change_value_h DECIMAL(18, 6) DEFAULT NULL,
  instant_change_value DECIMAL(18, 6) DEFAULT NULL,
  unit_name VARCHAR(50) DEFAULT NULL,
  alarm_level BIGINT DEFAULT 0,
  online_status TINYINT DEFAULT NULL,
  remark VARCHAR(500) DEFAULT NULL,
  can_catch_image TINYINT DEFAULT NULL,
  visual_eqmt_name VARCHAR(255) DEFAULT NULL,
  visual_eqmt_code VARCHAR(100) DEFAULT NULL,
  x_or_y VARCHAR(10) DEFAULT NULL,
  qrtz_job_id BIGINT DEFAULT NULL,
  create_time DATETIME DEFAULT NULL,
  create_uid BIGINT DEFAULT NULL,
  is_delete TINYINT DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_aqy_equipment_project (project_id),
  KEY idx_aqy_equipment_type (eqmt_type_id),
  KEY idx_aqy_equipment_code (eqmt_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备';

CREATE TABLE IF NOT EXISTS aqy_alarm (
  id BIGINT NOT NULL AUTO_INCREMENT,
  eqmt_type BIGINT DEFAULT NULL,
  alarm_level INT DEFAULT NULL,
  instant_threshold_value DECIMAL(18, 6) DEFAULT NULL,
  accumulative_threshold_value DECIMAL(18, 6) DEFAULT NULL,
  upper_limit DECIMAL(18, 6) DEFAULT NULL,
  lower_limit DECIMAL(18, 6) DEFAULT NULL,
  alarm_color VARCHAR(50) DEFAULT NULL,
  alarm_template VARCHAR(500) DEFAULT NULL,
  alarm_count INT DEFAULT NULL,
  cron_express VARCHAR(100) DEFAULT NULL,
  create_time DATETIME DEFAULT NULL,
  create_uid BIGINT DEFAULT NULL,
  is_delete TINYINT DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_aqy_alarm_type_level (eqmt_type, alarm_level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报警等级';

CREATE TABLE IF NOT EXISTS aqy_alarm_person (
  id BIGINT NOT NULL AUTO_INCREMENT,
  project_id BIGINT DEFAULT NULL,
  alarm_level INT DEFAULT NULL,
  contact_person VARCHAR(100) DEFAULT NULL,
  contact_person_number VARCHAR(50) DEFAULT NULL,
  create_time DATETIME DEFAULT NULL,
  create_uid BIGINT DEFAULT NULL,
  is_delete TINYINT DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_aqy_alarm_person_project_level (project_id, alarm_level),
  KEY idx_aqy_alarm_person_phone (contact_person_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报警联系人';

CREATE TABLE IF NOT EXISTS aqy_alarm_record (
  id BIGINT NOT NULL AUTO_INCREMENT,
  project_id BIGINT DEFAULT NULL,
  qmt_id BIGINT DEFAULT NULL,
  eqmt_name VARCHAR(255) DEFAULT NULL,
  alarm_id BIGINT DEFAULT NULL,
  alarm_level INT DEFAULT NULL,
  instant_value DECIMAL(18, 6) DEFAULT NULL,
  accumulative_value1 DECIMAL(18, 6) DEFAULT NULL,
  accumulative_value2 DECIMAL(18, 6) DEFAULT NULL,
  accumulative_value3 DECIMAL(18, 6) DEFAULT NULL,
  alarm_color VARCHAR(50) DEFAULT NULL,
  alarm_content VARCHAR(500) DEFAULT NULL,
  record_time DATETIME DEFAULT NULL,
  remedial_measures VARCHAR(1000) DEFAULT NULL,
  remedial_time DATETIME DEFAULT NULL,
  remedial_uid BIGINT DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_aqy_alarm_record_project_level (project_id, alarm_level),
  KEY idx_aqy_alarm_record_eqmt_time (qmt_id, record_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报警记录';

CREATE TABLE IF NOT EXISTS aqy_alarm_equipment (
  id BIGINT NOT NULL AUTO_INCREMENT,
  project_id BIGINT DEFAULT NULL,
  eqmt_name VARCHAR(255) DEFAULT NULL,
  eqmt_code VARCHAR(100) DEFAULT NULL,
  online_status TINYINT DEFAULT NULL,
  alarm_time DATETIME DEFAULT NULL,
  longitude DECIMAL(18, 8) DEFAULT NULL,
  latitude DECIMAL(18, 8) DEFAULT NULL,
  create_time DATETIME DEFAULT NULL,
  create_uid BIGINT DEFAULT NULL,
  is_delete TINYINT DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_aqy_alarm_equipment_project (project_id),
  KEY idx_aqy_alarm_equipment_code (eqmt_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报警设备';

CREATE TABLE IF NOT EXISTS aqy_camera (
  id BIGINT NOT NULL AUTO_INCREMENT,
  project_id BIGINT DEFAULT NULL,
  eqmt_name VARCHAR(255) DEFAULT NULL,
  qmt_code VARCHAR(100) DEFAULT NULL,
  ip VARCHAR(100) DEFAULT NULL,
  port INT DEFAULT NULL,
  user_name VARCHAR(100) DEFAULT NULL,
  password VARCHAR(255) DEFAULT NULL,
  show_front TINYINT DEFAULT NULL,
  online_status TINYINT DEFAULT NULL,
  app_key VARCHAR(255) DEFAULT NULL,
  secret VARCHAR(255) DEFAULT NULL,
  device_serial VARCHAR(255) DEFAULT NULL,
  device_code VARCHAR(255) DEFAULT NULL,
  camera_url TEXT,
  type INT DEFAULT NULL,
  access_token TEXT,
  create_time DATETIME DEFAULT NULL,
  create_uid BIGINT DEFAULT NULL,
  is_delete TINYINT DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_aqy_camera_project (project_id),
  KEY idx_aqy_camera_code (qmt_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='摄像头';

CREATE TABLE IF NOT EXISTS aqy_equipment_file (
  id BIGINT NOT NULL AUTO_INCREMENT,
  project_id BIGINT DEFAULT NULL,
  eqmt_id BIGINT DEFAULT NULL,
  file_name VARCHAR(255) DEFAULT NULL,
  file_url TEXT,
  create_time DATETIME DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_aqy_equipment_file_eqmt (eqmt_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备文件';

CREATE TABLE IF NOT EXISTS aqy_equipment_image (
  id BIGINT NOT NULL AUTO_INCREMENT,
  equipment_id BIGINT DEFAULT NULL,
  image_url TEXT,
  create_time DATETIME DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_aqy_equipment_image_eqmt (equipment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备图片';

CREATE TABLE IF NOT EXISTS aqy_equipment_target (
  id BIGINT NOT NULL AUTO_INCREMENT,
  equipment_id BIGINT DEFAULT NULL,
  measitem_id BIGINT DEFAULT NULL,
  target_name VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_aqy_equipment_target_eqmt (equipment_id),
  KEY idx_aqy_equipment_target_measitem (measitem_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备测点';

CREATE TABLE IF NOT EXISTS aqy_equipment_target_data (
  id BIGINT NOT NULL AUTO_INCREMENT,
  target_id BIGINT DEFAULT NULL,
  measitem_id BIGINT DEFAULT NULL,
  `value` DECIMAL(18, 6) DEFAULT NULL,
  catch_time DATETIME DEFAULT NULL,
  create_time DATETIME DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_aqy_target_data_target_time (target_id, catch_time),
  KEY idx_aqy_target_data_measitem_time (measitem_id, catch_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备测点数据';

CREATE TABLE IF NOT EXISTS aqy_equipment_wy_raw (
  id BIGINT NOT NULL AUTO_INCREMENT,
  eqmt_id BIGINT DEFAULT NULL,
  eqmt_code VARCHAR(100) DEFAULT NULL,
  eqmt_name VARCHAR(255) DEFAULT NULL,
  catch_time DATETIME DEFAULT NULL,
  value_wy DECIMAL(18, 6) DEFAULT NULL,
  x_or_y VARCHAR(10) DEFAULT NULL,
  picture TEXT,
  create_time DATETIME DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_aqy_wy_raw_eqmt_time (eqmt_id, catch_time),
  KEY idx_aqy_wy_raw_code_time (eqmt_code, catch_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='位移原始数据';

CREATE TABLE IF NOT EXISTS aqy_equipment_lf_raw (
  id BIGINT NOT NULL AUTO_INCREMENT,
  eqmt_id BIGINT DEFAULT NULL,
  eqmt_code VARCHAR(100) DEFAULT NULL,
  eqmt_name VARCHAR(255) DEFAULT NULL,
  catch_time DATETIME DEFAULT NULL,
  lf_value DECIMAL(18, 6) DEFAULT NULL,
  temp_value DECIMAL(18, 6) DEFAULT NULL,
  create_time DATETIME DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_aqy_lf_raw_eqmt_time (eqmt_id, catch_time),
  KEY idx_aqy_lf_raw_code_time (eqmt_code, catch_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='裂缝原始数据';

CREATE TABLE IF NOT EXISTS aqy_equipment_qj_raw (
  id BIGINT NOT NULL AUTO_INCREMENT,
  eqmt_id BIGINT DEFAULT NULL,
  eqmt_code VARCHAR(100) DEFAULT NULL,
  eqmt_name VARCHAR(255) DEFAULT NULL,
  catch_time DATETIME DEFAULT NULL,
  x_value_qj DECIMAL(18, 6) DEFAULT NULL,
  y_value_qj DECIMAL(18, 6) DEFAULT NULL,
  z_value_qj DECIMAL(18, 6) DEFAULT NULL,
  temp_value DECIMAL(18, 6) DEFAULT NULL,
  create_time DATETIME DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_aqy_qj_raw_eqmt_time (eqmt_id, catch_time),
  KEY idx_aqy_qj_raw_code_time (eqmt_code, catch_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='倾角原始数据';

CREATE TABLE IF NOT EXISTS aqy_equipment_yl_raw (
  id BIGINT NOT NULL AUTO_INCREMENT,
  eqmt_id BIGINT DEFAULT NULL,
  eqmt_code VARCHAR(100) DEFAULT NULL,
  eqmt_name VARCHAR(255) DEFAULT NULL,
  catch_time DATETIME DEFAULT NULL,
  yl_value DECIMAL(18, 6) DEFAULT NULL,
  create_time DATETIME DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_aqy_yl_raw_eqmt_time (eqmt_id, catch_time),
  KEY idx_aqy_yl_raw_code_time (eqmt_code, catch_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='雨量原始数据';

CREATE TABLE IF NOT EXISTS aqy_section (
  id BIGINT NOT NULL AUTO_INCREMENT,
  project_id BIGINT DEFAULT NULL,
  section_name VARCHAR(255) DEFAULT NULL,
  longitude DECIMAL(18, 8) DEFAULT NULL,
  latitude DECIMAL(18, 8) DEFAULT NULL,
  create_time DATETIME DEFAULT NULL,
  create_uid BIGINT DEFAULT NULL,
  is_delete TINYINT DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_aqy_section_project (project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='断面';

CREATE TABLE IF NOT EXISTS aqy_section_eqmt (
  id BIGINT NOT NULL AUTO_INCREMENT,
  section_id BIGINT DEFAULT NULL,
  eqmt_type_id BIGINT DEFAULT NULL,
  create_time DATETIME DEFAULT NULL,
  create_uid BIGINT DEFAULT NULL,
  is_delete TINYINT DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_aqy_section_eqmt_section (section_id),
  KEY idx_aqy_section_eqmt_type (eqmt_type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='断面设备类型';

CREATE TABLE IF NOT EXISTS aqy_hik_wy (
  id BIGINT NOT NULL AUTO_INCREMENT,
  device_addr VARCHAR(100) DEFAULT NULL,
  value_wy_x DECIMAL(18, 6) DEFAULT NULL,
  value_wy_y DECIMAL(18, 6) DEFAULT NULL,
  value_wy_z DECIMAL(18, 6) DEFAULT NULL,
  catch_time DATETIME DEFAULT NULL,
  create_time DATETIME DEFAULT NULL,
  create_uid BIGINT DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_aqy_hik_wy_device_time (device_addr, catch_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='海康位移数据';

CREATE TABLE IF NOT EXISTS aqy_sdrk_wy (
  id BIGINT NOT NULL AUTO_INCREMENT,
  device_addr VARCHAR(100) DEFAULT NULL,
  node_id BIGINT DEFAULT NULL,
  value_wy_x DECIMAL(18, 6) DEFAULT NULL,
  value_wy_y DECIMAL(18, 6) DEFAULT NULL,
  catch_time DATETIME DEFAULT NULL,
  create_time DATETIME DEFAULT NULL,
  create_uid BIGINT DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_aqy_sdrk_wy_device_time (device_addr, catch_time),
  KEY idx_aqy_sdrk_wy_node_time (node_id, catch_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数达位移数据';

CREATE TABLE IF NOT EXISTS aqy_mqtt_cmd_message (
  id BIGINT NOT NULL AUTO_INCREMENT,
  cmd_type VARCHAR(100) DEFAULT NULL,
  eqmt_code VARCHAR(100) DEFAULT NULL,
  msg_id BIGINT DEFAULT NULL,
  msg_data TEXT,
  reply_rest TEXT,
  create_time DATETIME DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_aqy_mqtt_msg_id_type (msg_id, cmd_type),
  KEY idx_aqy_mqtt_eqmt_code (eqmt_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MQTT指令消息';
