-- 恢复被逻辑删除的 VOD 分类（del_flag: 0正常 2删除）
UPDATE media_vod_category SET del_flag = '0' WHERE category_id IN (1, 2, 3);
SELECT category_id, category_name, status, del_flag FROM media_vod_category ORDER BY order_num, category_id;
