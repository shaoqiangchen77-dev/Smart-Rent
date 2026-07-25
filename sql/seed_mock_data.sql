-- =============================================
-- 将小程序 rent-app 的 mock 数据转为真实数据入库
-- 来源: rent-app/src/utils/mock-data.ts (mockHouses / mockMessages)
-- 执行: mysql -h127.0.0.1 -uroot -p1234 < sql/seed_mock_data.sql
-- =============================================

-- ============ 1) 房东 & 租客用户 (system_db) ============
-- mock 房源 landlordId 为 101/102/103，配套真实房东用户；另建演示租客(200) 作为消息接收者
-- 密码统一为 123456 的 BCrypt 哈希（与后台 admin 一致）
INSERT INTO system_db.user (id, username, password, phone, nickname, role, status) VALUES
(101, 'landlord101', '$2a$10$3VizJ3dJdUg6Xjd6ldMyK.c3GaGebfTBIaizuLGWa.w3i65JWmilS', '13900000101', '海淀张房东', 1, 1),
(102, 'landlord102', '$2a$10$3VizJ3dJdUg6Xjd6ldMyK.c3GaGebfTBIaizuLGWa.w3i65JWmilS', '13900000102', '朝阳李房东', 1, 1),
(103, 'landlord103', '$2a$10$3VizJ3dJdUg6Xjd6ldMyK.c3GaGebfTBIaizuLGWa.w3i65JWmilS', '13900000103', '昌平王房东', 1, 1),
(200, 'tenant200',   '$2a$10$3VizJ3dJdUg6Xjd6ldMyK.c3GaGebfTBIaizuLGWa.w3i65JWmilS', '13800000200', '租客小李',   0, 1);

-- ============ 2) 房源 (house_db.house) ============
-- floor 字段 '8/18层' 拆分为 floor='8层' + total_floor=18；facilities 存 JSON；其余字段一一对应
INSERT INTO house_db.house
  (id, landlord_id, title, description, area, address, longitude, latitude, price,
   house_type, rent_type, area_size, floor, total_floor, decoration, orientation,
   subway_distance, subway_station, has_elevator, has_parking, facilities,
   status, view_count, collect_count, avg_rating, review_count)
VALUES
(9001, 101, '万柳书院旁南向两居，近地铁16号线', '采光稳定，家具家电齐全，适合通勤中关村和海淀黄庄。', '海淀区', '万柳中路 · 蜂鸟家园', 116.3048, 39.9772, 6800.00,
 '两居', '整租', 72.00, '8层', 18, '精装', '南向', 520, '苏州桥', 1, 1,
 '["空调","洗衣机","电梯","独立阳台"]', 1, 248, 36, 4.8, 19),
(9002, 102, '朝阳公园精装一居，独立阳台可养宠', '周边生活配套成熟，楼下便利店、咖啡店和健身房。', '朝阳区', '甜水园街 · 公园1872', 116.4784, 39.9336, 5200.00,
 '一居', '整租', 48.00, '12层', 22, '精装', '东南', 780, '朝阳公园', 1, 0,
 '["冰箱","空调","燃气灶","阳台"]', 1, 186, 22, 4.6, 12),
(9003, 103, '西二旗合租主卧，互联网园区通勤友好', '室友稳定，公共区域整洁，适合单人入住。', '昌平区', '回龙观西大街 · 龙泽苑', 116.3192, 40.0709, 2600.00,
 '主卧', '合租', 22.00, '5层', 6, '简装', '南向', 640, '龙泽', 0, 0,
 '["空调","洗衣机","热水器","宽带"]', 1, 319, 44, 4.5, 27);

-- ============ 3) 房源图片 (house_db.house_image) ============
INSERT INTO house_db.house_image (house_id, image_url, image_type, sort_order) VALUES
(9001, 'https://smart-rent.oss-cn-beijing.aliyuncs.com/house/images/g1.png', 'exterior', 0),
(9002, 'https://smart-rent.oss-cn-beijing.aliyuncs.com/house/images/g2.png', 'exterior', 0),
(9003, 'https://smart-rent.oss-cn-beijing.aliyuncs.com/house/images/g3.png', 'exterior', 0);

-- ============ 4) 房源标签 (house_db.house_tag) ============
INSERT INTO house_db.house_tag (house_id, tag_name, tag_type) VALUES
(9001, '近地铁', 'system'), (9001, '采光好', 'custom'), (9001, '可长租', 'custom'),
(9002, '可养宠', 'custom'), (9002, '独立阳台', 'custom'), (9002, '公园旁', 'custom'),
(9003, '合租', 'custom'), (9003, '通勤友好', 'custom'), (9003, '性价比', 'custom');

-- ============ 5) 站内消息 (system_db.message) ============
-- createTime 字符串解析为具体时间；receiver 统一为演示租客(200)
INSERT INTO system_db.message (sender_id, receiver_id, msg_type, title, content, biz_type, biz_id, is_read, create_time) VALUES
(101, 200, 'appointment', '看房预约已确认', '你预约的“万柳书院旁南向两居”已确认，今天 18:30 可到现场看房。', 'appointment', NULL, 0, '2026-07-16 10:24:00'),
(NULL, 200, 'system', '房源推荐更新', '根据你的偏好，新增 3 套海淀区近地铁房源。', NULL, NULL, 0, '2026-07-15 21:18:00'),
(NULL, 200, 'bill', '租金账单提醒', '6 月账单已生成，请在 7 月 5 日前完成支付。', 'bill', NULL, 1, '2026-06-28 09:12:00');
