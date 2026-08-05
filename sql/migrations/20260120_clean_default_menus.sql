-- Clean customer-visible default framework menus and add AQY business menus.
-- Safe for migrated databases: only removes known stock monitor/tool menu ids.

DELETE FROM sys_role_menu
WHERE menu_id IN (2,3,109,110,111,112,113,114,115,116,117,1046,1047,1048,1049,1050,1051,1052,1053,1054,1055,1056,1057,1058,1059,1060);

DELETE FROM sys_menu
WHERE menu_id IN (2,3,109,110,111,112,113,114,115,116,117,1046,1047,1048,1049,1050,1051,1052,1053,1054,1055,1056,1057,1058,1059,1060);

INSERT IGNORE INTO sys_menu VALUES
(4, '数据看板', 0, 2, 'screen', 'screen/index', '', '', 1, 0, 'C', '0', '0', '', 'dashboard', 'admin', NOW(), '', NULL, '安全云数据看板'),
(5, '项目管理', 0, 3, 'aqyProject', NULL, '', '', 1, 0, 'M', '0', '0', '', 'tree', 'admin', NOW(), '', NULL, '项目与标段管理'),
(6, '设备管理', 0, 4, 'aqyEquipment', NULL, '', '', 1, 0, 'M', '0', '0', '', 'monitor', 'admin', NOW(), '', NULL, '设备与网关管理'),
(7, '告警管理', 0, 5, 'aqyAlarm', NULL, '', '', 1, 0, 'M', '0', '0', '', 'message', 'admin', NOW(), '', NULL, '告警规则、记录和通知管理'),
(8, '数据报表', 0, 6, 'aqyReport', NULL, '', '', 1, 0, 'M', '0', '0', '', 'chart', 'admin', NOW(), '', NULL, '实时、历史和原始数据报表'),
(200, '项目列表', 5, 1, 'project', 'aqy/project/index', '', '', 1, 0, 'C', '0', '0', 'aqy:project:list', 'tree', 'admin', NOW(), '', NULL, '项目列表'),
(201, '标段管理', 5, 2, 'section', 'aqy/section/index', '', '', 1, 0, 'C', '0', '0', 'aqy:section:list', 'tree-table', 'admin', NOW(), '', NULL, '标段管理'),
(202, '采集设备', 6, 1, 'aqyEquipment', 'aqy/aqyEquipment/index', '', '', 1, 0, 'C', '0', '0', 'aqy:aqyEquipment:list', 'monitor', 'admin', NOW(), '', NULL, '采集设备'),
(203, '设备部署', 6, 2, 'equipmentDeploy', 'aqy/equipmentDeploy/index', '', '', 1, 0, 'C', '0', '0', 'aqy:aqySectionEqmt:list', 'build', 'admin', NOW(), '', NULL, '设备部署'),
(204, '网关设备', 6, 3, 'gatwayEquipment', 'aqy/gatwayEquipment/index', '', '', 1, 0, 'C', '0', '0', 'aqy:gatwayEquipment:list', 'server', 'admin', NOW(), '', NULL, '网关设备'),
(205, '设备类型', 6, 4, 'aqyEquipmentType', 'aqy/aqyEquipmentType/index', '', '', 1, 0, 'C', '0', '0', 'aqy:aqyEquipmentType:list', 'dict', 'admin', NOW(), '', NULL, '设备类型'),
(206, '摄像头管理', 6, 5, 'camera', 'aqy/aqyCamera/index', '', '', 1, 0, 'C', '0', '0', 'aqy:camera:list', 'eye-open', 'admin', NOW(), '', NULL, '摄像头管理'),
(207, '设备文件', 6, 6, 'equipmentFile', 'aqy/aqyEquipmentFile/index', '', '', 1, 0, 'C', '0', '0', 'aqy:aqyEquipmentFile:list', 'documentation', 'admin', NOW(), '', NULL, '设备文件'),
(208, '告警记录', 7, 1, 'alarmRecord', 'aqy/alarmRecord/index', '', '', 1, 0, 'C', '0', '0', 'aqy:alarmRecord:list', 'message', 'admin', NOW(), '', NULL, '告警记录'),
(209, '告警等级', 7, 2, 'alarmLevel', 'aqy/alarmLevel/index', '', '', 1, 0, 'C', '0', '0', 'aqy:alarmLevel:list', 'dict', 'admin', NOW(), '', NULL, '告警等级'),
(210, '告警设备', 7, 3, 'alarmEquipment', 'aqy/alarmEquipment/index', '', '', 1, 0, 'C', '0', '0', 'aqy:alarmEquipment:list', 'monitor', 'admin', NOW(), '', NULL, '告警设备'),
(211, '短信接收人', 7, 4, 'alarmPerson', 'aqy/alarmPerson/index', '', '', 1, 0, 'C', '0', '0', 'aqy:alarmPerson:list', 'peoples', 'admin', NOW(), '', NULL, '告警短信接收人'),
(212, '实时数据', 8, 1, 'realTimeReport', 'aqy/aqyRealTimeReport/index', '', '', 1, 0, 'C', '0', '0', '', 'chart', 'admin', NOW(), '', NULL, '实时数据报表'),
(213, '历史数据', 8, 2, 'historyReport', 'aqy/aqyHistoryReport/index', '', '', 1, 0, 'C', '0', '0', '', 'date', 'admin', NOW(), '', NULL, '历史数据报表'),
(214, '原始数据', 8, 3, 'rawReport', 'aqy/aqyRawReport/index', '', '', 1, 0, 'C', '0', '0', '', 'form', 'admin', NOW(), '', NULL, '原始数据报表'),
(215, '倾角原始数据', 8, 4, 'qjRaw', 'aqy/aqyEquipmentQjRaw/index', '', '', 1, 0, 'C', '0', '0', 'aqy:aqyEquipmentQjRaw:list', 'form', 'admin', NOW(), '', NULL, '倾角原始数据'),
(216, '雨量原始数据', 8, 5, 'ylRaw', 'aqy/aqyEquipmentYlRaw/index', '', '', 1, 0, 'C', '0', '0', 'aqy:aqyEquipmentYlRaw:list', 'form', 'admin', NOW(), '', NULL, '雨量原始数据'),
(217, '裂缝原始数据', 8, 6, 'lfRaw', 'aqy/aqyEquipmentLfRaw/index', '', '', 1, 0, 'C', '0', '0', 'aqy:aqyEquipmentLfRaw:list', 'form', 'admin', NOW(), '', NULL, '裂缝原始数据'),
(218, '水位原始数据', 8, 7, 'wyRaw', 'aqy/aqyEquipmentWyRaw/index', '', '', 1, 0, 'C', '0', '0', 'aqy:aqyEquipmentWyRaw:list', 'form', 'admin', NOW(), '', NULL, '水位原始数据'),
(219, 'MQTT指令', 8, 8, 'mqttCmdMessage', 'aqy/mqttCmdMessage/index', '', '', 1, 0, 'C', '0', '0', 'aqy:mqttCmdMessage:list', 'redis', 'admin', NOW(), '', NULL, 'MQTT指令消息'),
(220, '水电入库', 8, 9, 'sdrk', 'aqy/sdrk/index', '', '', 1, 0, 'C', '0', '0', 'aqy:sdrkwy:list', 'form', 'admin', NOW(), '', NULL, '水电入库数据'),
(221, '海康位移', 8, 10, 'hikwy', 'aqy/hikwy/index', '', '', 1, 0, 'C', '0', '0', 'aqy:hikgnss:list', 'form', 'admin', NOW(), '', NULL, '海康位移数据');

INSERT IGNORE INTO sys_role_menu(role_id, menu_id)
SELECT 2, m.menu_id
FROM sys_menu m
WHERE m.menu_id IN (4,5,6,7,8,200,201,202,203,204,205,206,207,208,209,210,211,212,213,214,215,216,217,218,219,220,221);
