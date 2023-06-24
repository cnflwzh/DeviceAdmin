package com.eacg.frame.userframe;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
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

import cn.hutool.crypto.SecureUtil;

public class ModifyUserFrame extends JFrame {

    private JTextField nickNameField, usernamField, phoneNumberField;
    private JPasswordField passwordField;
    private JButton confirmButton, cancelButton;
    private UserInfoFrame userInfoFrame;
    private JComboBox<String> departmentComboBox;

    public ModifyUserFrame(UserInfoFrame userInfoFrame) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setTitle("修改用户信息");
        String id = userInfoFrame.userId;

        String sqlString = "select * from busi_userinfo where id=?";
        List<Map<String, Object>> selectSQL = DBToolSet.selectSQL(sqlString, id);
        Map<String, Object> map = selectSQL.get(0);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 2));
        panel.add(new JLabel("昵称"));
        nickNameField = new JTextField(10);
        nickNameField.setText(String.valueOf(map.get("nickname")));
        panel.add(nickNameField);
        panel.add(new JLabel("用户名"));
        usernamField = new JTextField(10);
        usernamField.setText(String.valueOf(map.get("username")));
        panel.add(usernamField);
        panel.add(new JLabel("密码"));
        passwordField = new JPasswordField(10);
        panel.add(passwordField);
        panel.add(new JLabel("手机号"));
        phoneNumberField = new JTextField(10);
        phoneNumberField.setText(String.valueOf(map.get("phone_number")));
        panel.add(phoneNumberField);
        panel.add(new JLabel("部门"));

        List<Map<String, Object>> departmentList = DBToolSet
                .selectSQL("select id,department_name from busi_department_info where is_deleted=0");

        departmentComboBox = new JComboBox<String>();
        for (Map<String, Object> map2 : departmentList) {
            departmentComboBox.addItem(map2.get("department_name").toString());
        }
        for (int i = 0; i < departmentList.size(); i++) {
            if (String.valueOf(departmentList.get(i).get("id")).equals(String.valueOf(map.get("id")))) {
                departmentComboBox.setSelectedItem(String.valueOf(departmentList.get(i).get("department_name")));
                ;
            }
        }
        panel.add(departmentComboBox);

        confirmButton = new JButton("确认");
        confirmButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (nickNameField.getText().equals("") || usernamField.getText().equals("")
                        || phoneNumberField.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "请填写完整信息", "提示", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                List<Map<String, Object>> haveUserName = DBToolSet
                        .selectSQL("select * from busi_userinfo where username=?", usernamField.getText());
                if (haveUserName.size() > 1) {
                    JOptionPane.showMessageDialog(ModifyUserFrame.this, "用户名已存在");
                    return;
                }
                List<Map<String, Object>> department_id = DBToolSet.selectSQL(
                        "select id from busi_department_info where is_deleted=0 and department_name=?",
                        departmentComboBox.getSelectedItem());
                ;

                StringBuffer sqlString = new StringBuffer();
                List<Object> params = new ArrayList<>();
                sqlString.append("update busi_userinfo set nickname=?,username=?");
                params.add(nickNameField.getText());
                params.add(usernamField.getText());
                if (!passwordField.getText().equals("")) {
                    sqlString.append(",password=?");
                    params.add(SecureUtil.sha1(passwordField.getText()));
                }
                sqlString.append(",phone_number=?,department_id=?,update_date=?,update_user=? where id=?");
                params.add(phoneNumberField.getText());
                params.add(department_id.get(0).get("id"));
                params.add(new Date());
                params.add(userInfoFrame.loginNickname);
                params.add(id);

                DBToolSet.updateSQL(sqlString.toString(), params.toArray());
                JOptionPane.showMessageDialog(ModifyUserFrame.this, "修改成功");
                userInfoFrame.refreshData();
                ModifyUserFrame.this.dispose();

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
