package com.eacg.frame.userframe;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.eacg.tools.DBToolSet;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;

public class AddUserFrame extends JFrame {

    private JTextField nickNameField, usernamField, phoneNumberField;
    private JPasswordField passwordField;
    private JButton confirmButton, cancelButton;
    private JComboBox<String> departmentComboBox;
    Snowflake snowflake = IdUtil.getSnowflake(1, 1);

    public AddUserFrame(UserInfoFrame userInfoFrame) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setTitle("添加用户信息");

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 2));
        panel.add(new JLabel("昵称"));
        nickNameField = new JTextField(10);
        panel.add(nickNameField);
        panel.add(new JLabel("用户名"));
        usernamField = new JTextField(10);
        panel.add(usernamField);
        panel.add(new JLabel("密码"));
        passwordField = new JPasswordField(10);
        panel.add(passwordField);
        panel.add(new JLabel("手机号"));
        phoneNumberField = new JTextField(10);
        panel.add(phoneNumberField);
        panel.add(new JLabel("部门"));
        departmentComboBox = new JComboBox<String>();
        List<Map<String, Object>> departmentList = DBToolSet
                .selectSQL("select id,department_name from busi_department_info where is_deleted=0");

        departmentComboBox = new JComboBox<String>();
        for (Map<String, Object> map2 : departmentList) {
            departmentComboBox.addItem(map2.get("department_name").toString());
        }
        panel.add(departmentComboBox);

        confirmButton = new JButton("确认");
        confirmButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (nickNameField.getText().equals("")) {
                    JOptionPane.showMessageDialog(AddUserFrame.this, "昵称不能为空");
                    return;
                }
                if (usernamField.getText().equals("")) {
                    JOptionPane.showMessageDialog(AddUserFrame.this, "用户名不能为空");
                    return;
                }
                List<Map<String, Object>> haveUserName = DBToolSet
                        .selectSQL("select * from busi_userinfo where username=?", usernamField.getText());
                if (haveUserName.size() > 0) {
                    JOptionPane.showMessageDialog(AddUserFrame.this, "用户名已存在");
                    return;
                }
                if (passwordField.getText().equals("")) {
                    JOptionPane.showMessageDialog(AddUserFrame.this, "密码不能为空");
                    return;
                }
                if (phoneNumberField.getText().equals("")) {
                    JOptionPane.showMessageDialog(AddUserFrame.this, "手机号不能为空");
                    return;
                }
                if (departmentComboBox.getSelectedItem().toString().equals("")) {
                    JOptionPane.showMessageDialog(AddUserFrame.this, "部门不能为空");
                    return;
                }
                List<Map<String, Object>> department_id = DBToolSet.selectSQL(
                        "select id from busi_department_info where is_deleted=0 and department_name=?",
                        departmentComboBox.getSelectedItem());
                ;

                String sqlString = "insert into busi_userinfo(id,nickname,username,password,phone_number,department_id,create_date,create_user,is_deleted,status)value(?,?,?,?,?,?,?,?,?,?)";
                DBToolSet.insertSQL(sqlString, snowflake.nextId(), nickNameField.getText(), usernamField.getText(),
                        SecureUtil.sha1(passwordField.getText()), phoneNumberField.getText(),
                        department_id.get(0).get("id"), new Date(), UserInfoFrame.loginNickname, 0, 0);
                JOptionPane.showMessageDialog(AddUserFrame.this, "添加成功");
                userInfoFrame.refreshData();
                dispose();

            }

        });
        cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> {
            dispose();
        });
        panel.add(confirmButton);
        panel.add(cancelButton);

        add(panel);

    }

}
