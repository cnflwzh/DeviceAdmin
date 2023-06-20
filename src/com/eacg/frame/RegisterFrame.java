package com.eacg.frame;

import javax.swing.*;

import com.eacg.tools.DBToolSet;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class RegisterFrame extends JFrame {
    private JTextField usernameField,nicknameField,phoneField;
    private JPasswordField passwordField,confirmPasswordField;
    private JComboBox<String> departmentComboBox;
    private JButton registerButton,cancelButton;
    Snowflake snowflake = IdUtil.getSnowflake(1,1);

    public RegisterFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 300);
        setLocationRelativeTo(null);

        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        nicknameField = new JTextField(20);
        confirmPasswordField = new JPasswordField(20);
        phoneField = new JTextField(20);
        String sql = "SELECT department_name FROM busi_department_info WHERE is_deleted = 0";
        List<Map<String, Object>> resultList = DBToolSet.selectSQL(sql);
        List<String> departmentNames = new ArrayList<>();
        for (Map<String, Object> result : resultList) {
            departmentNames.add((String) result.get("department_name"));
        }

        departmentComboBox = new JComboBox<>(departmentNames.toArray(new String[0]));

        registerButton = new JButton("Register");
        cancelButton = new JButton("Cancel");

        setLayout(new GridLayout(7,2));
        add(new JLabel("Nickname:"));
        add(nicknameField);
        add(new JLabel("Username:"));
        add(usernameField);
        add(new JLabel("Password:"));
        add(passwordField);
        add(new JLabel("Confirm Password:"));
        add(confirmPasswordField);
        add(new JLabel("Department:"));
        add(departmentComboBox);
        add(new JLabel("Phone:"));
        add(phoneField);
        add(cancelButton);
        add(registerButton);
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RegisterFrame.this.dispose();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String nickname = nicknameField.getText();
                String confirmPassword = new String(confirmPasswordField.getPassword());
                String department = departmentComboBox.getSelectedItem().toString();
                String phone = phoneField.getText();
                if (username.isEmpty() || password.isEmpty() || nickname.isEmpty() || confirmPassword.isEmpty() || department.isEmpty() || phone.isEmpty()) {
                    JOptionPane.showMessageDialog(RegisterFrame.this, "Please fill in all the fields.");
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(RegisterFrame.this, "The passwords you entered do not match.");
                    return;
                }
                if (password.length() < 6) {
                    JOptionPane.showMessageDialog(RegisterFrame.this, "The password must be at least 6 characters long.");
                    return;
                }
                if (phone.length() != 11) {
                    JOptionPane.showMessageDialog(RegisterFrame.this, "The phone number must be 11 digits long.");
                    return;
                }
                if (department.equals("")) {
                    JOptionPane.showMessageDialog(RegisterFrame.this, "Please select a department.");
                    return;
                }
                String passwdSHA1=SecureUtil.sha1(password);
                List<Map<String,Object>> departmentID = DBToolSet.selectSQL("select id from busi_department_info where department_name=? and is_deleted=0", department);
                department = departmentID.get(0).get("id").toString();
                String sql = "INSERT INTO busi_userinfo "+
                "(id, nickname,username,password,phone_number,department_id,create_date,create_user,is_deleted,status) "+
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                DBToolSet.insertSQL(sql, snowflake.nextId(),nickname,username,passwdSHA1,phone,department,new Date(),username,0,0);

                JOptionPane.showMessageDialog(RegisterFrame.this, "Registered successfully.");
                RegisterFrame.this.dispose();
            }
        });
    }
}
