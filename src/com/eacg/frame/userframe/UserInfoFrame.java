package com.eacg.frame.userframe;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.eacg.frame.deviceinfoframe.DeviceInfoFrame;
import com.eacg.tools.DBToolSet;
import cn.hutool.core.date.DateUtil;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
public class UserInfoFrame extends JFrame {
    static String userId;
    static String loginNickname;

    private JTextField nicknamField, usernameField, phoneNumberField;
    private JButton searchButton, addButton, modifyButton, deleteButton, clearButton;
    private JComboBox<String> departmentComboBox;
    private JTable table;
    private DefaultTableModel tableModel;
    private DeviceInfoFrame deviceInfoFrame;

    public UserInfoFrame(DeviceInfoFrame deviceInfoFrame) {
        this.deviceInfoFrame = deviceInfoFrame;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setTitle("用户信息管理");
        this.loginNickname=deviceInfoFrame.getUserName();
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new FlowLayout());
        filterPanel.add(new JLabel("昵称"));
        nicknamField = new JTextField(10);
        filterPanel.add(nicknamField);
        filterPanel.add(new JLabel("用户名"));
        usernameField = new JTextField(10);
        filterPanel.add(usernameField);
        filterPanel.add(new JLabel("电话号"));
        phoneNumberField = new JTextField(11);
        filterPanel.add(phoneNumberField);
        filterPanel.add(new JLabel("部门"));
        departmentComboBox = new JComboBox<>();
        departmentComboBox.addItem("全部");
        filterPanel.add(departmentComboBox);

        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(new FlowLayout());
        addButton = new JButton("添加用户");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddUserFrame(UserInfoFrame.this).setVisible(true);
            }
        });

        modifyButton = new JButton("修改用户");
        modifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(UserInfoFrame.this, "请选中一行数据");
                    return;
                }
                String id = (String) table.getValueAt(selectedRow, 0);
                UserInfoFrame.this.userId=id;
                new ModifyUserFrame(UserInfoFrame.this).setVisible(true);
            }
        });

        deleteButton = new JButton("删除用户");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(UserInfoFrame.this, "请选中一行数据");
                    return;
                }
                String id = (String) table.getValueAt(selectedRow, 0);
                int result = JOptionPane.showConfirmDialog(UserInfoFrame.this, "确认删除？");
                if (result == JOptionPane.OK_OPTION) {
                    DBToolSet.updateSQL("update busi_userinfo set is_deleted = 1 where id = ?", id);
                     tableModel.setRowCount(0); 
                    JOptionPane.showMessageDialog(UserInfoFrame.this, "删除成功");
                    loadData();
                }
            }
        });
        searchButton = new JButton("查询");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sql = "SELECT * FROM busi_userinfo where is_deleted = 0";
                List<Object> params = new ArrayList<>();
                String nickname = nicknamField.getText().trim();
                if (!nickname.isEmpty()) {
                    sql += " and nickname like ?";
                    params.add("%" + nickname + "%");
                }
                String username = usernameField.getText().trim();
                if (!username.isEmpty()) {
                    sql += " and username like ?";
                    params.add("%" + username + "%");
                }
                String phoneNumber = phoneNumberField.getText().trim();
                if (!phoneNumber.isEmpty()) {
                    sql += " and phone_number like ?";
                    params.add("%" + phoneNumber + "%");
                }
                String department = (String) departmentComboBox.getSelectedItem();
                if (!department.equals("全部")) {
                    sql += " and department_id = ?";
                    params.add(department);
                }
                List<Map<String, Object>> users = DBToolSet.selectSQL(sql, params.toArray());
                tableModel.setRowCount(0);
                for (Map<String, Object> user : users) {
                    Object[] rowData = new Object[6];
                    rowData[0] = user.get("id");
                    rowData[1] = user.get("nickname");
                    rowData[2] = user.get("username");
                    rowData[3] = user.get("phone_number");
                    rowData[4] = user.get("department_id");
                    rowData[5] = user.get("create_date");
                    tableModel.addRow(rowData);
                }
            }
        });
        
        clearButton = new JButton("清空");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nicknamField.setText("");
                usernameField.setText("");
                phoneNumberField.setText("");
                departmentComboBox.setSelectedIndex(0);
            }
        });
        actionsPanel.add(addButton);
        actionsPanel.add(modifyButton);
        actionsPanel.add(deleteButton);
        actionsPanel.add(searchButton);
        actionsPanel.add(clearButton);

        String[] columnNames = { "ID","昵称", "用户名", "电话号", "部门", "创建时间" };
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        setLayout(new BorderLayout());
        add(filterPanel, BorderLayout.NORTH);
        add(actionsPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        // Load data
        loadData();

    }

    private void loadData() {
        List<Map<String, Object>> users = DBToolSet.selectSQL("SELECT * FROM busi_userinfo where is_deleted = 0");
        for (Map<String, Object> user : users) {
            Object[] rowData = new Object[6];
            rowData[0] = user.get("id");
            rowData[1] = user.get("nickname");
            rowData[2] = user.get("username");
            rowData[3] = user.get("phone_number");
            rowData[4] = user.get("department_id");
            rowData[5] = user.get("create_date");
         
        
            tableModel.addRow(rowData);
        }
    }
}
