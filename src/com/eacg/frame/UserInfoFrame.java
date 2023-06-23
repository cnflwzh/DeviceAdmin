package com.eacg.frame;

import javax.swing.JFrame;

public class UserInfoFrame extends JFrame{
    /**
     * CREATE TABLE `busi_userinfo` (
     * `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL
     * COMMENT '用户id',
     * `nickname` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT
     * NULL COMMENT '昵称',
     * `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT
     * NULL COMMENT '用户名',
     * `password` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT
     * NULL COMMENT '密码',
     * `phone_number` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci
     * NULL DEFAULT NULL COMMENT '电话号',
     * `department_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci
     * NOT NULL COMMENT '部门id',
     * `create_date` datetime NULL DEFAULT NULL COMMENT '创建时间',
     * `create_user` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci
     * NULL DEFAULT NULL COMMENT '创建用户',
     * `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
     * `update_user` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci
     * NULL DEFAULT NULL COMMENT '更新用户',
     * `is_deleted` int(1) NULL DEFAULT NULL COMMENT '是否删除0否1是',
     * `status` int(1) NULL DEFAULT NULL COMMENT '0正常1禁用',
     * `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL
     * DEFAULT NULL COMMENT '备注',
     * PRIMARY KEY (`id`) USING BTREE
     * ) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci
     * ROW_FORMAT = Dynamic;
     */
    
    public UserInfoFrame(DeviceInfoFrame deviceInfoFrame) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 600);
        setLocationRelativeTo(null);



        
        
    }
}
