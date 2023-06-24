package com.eacg.frame.loginframe;

import javax.swing.*;

import com.eacg.frame.deviceinfoframe.DeviceInfoFrame;
import com.eacg.tools.DBToolSet;


import cn.hutool.crypto.SecureUtil;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

public class LoginFrame extends JFrame {
    

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton,registerButton;

    public LoginFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 150);
        setLocationRelativeTo(null);
        setTitle("登录");

        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");

        setLayout(new FlowLayout());
        add(new JLabel("Username:"));
        add(usernameField);
        add(new JLabel("Password:"));
        add(passwordField);
        add(registerButton);
        add(loginButton);

        usernameField.setText("cnflwzh");
        passwordField.setText("123456");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RegisterFrame().setVisible(true);
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = SecureUtil.sha1(new String(passwordField.getPassword()));

                String sql = "SELECT * FROM busi_userinfo WHERE username = ? AND password = ?";
                List<Map<String, Object>> users = DBToolSet.selectSQL(sql, username, password);

                if (!users.isEmpty()) {
                   DeviceInfoFrame deviceInfoFrame = new DeviceInfoFrame();
                    deviceInfoFrame.setUserName(users.get(0).get("nickname").toString());
                    deviceInfoFrame.setVisible(true);
                    LoginFrame.this.dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Invalid username or password.");
                }
            }
        });
    }
}
