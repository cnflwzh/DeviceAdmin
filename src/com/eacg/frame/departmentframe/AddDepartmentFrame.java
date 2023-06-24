package com.eacg.frame.departmentframe;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.eacg.tools.DBToolSet;

import cn.hutool.core.lang.Snowflake;

public class AddDepartmentFrame extends JFrame {
    private JTextField departmentNameField, departmentCodeField;
    private DepartmentInfoFrame departmentInfoFrame;
    private JButton addButton, cancelButton;
    Snowflake snowflake = new Snowflake(1, 1);

    public AddDepartmentFrame(DepartmentInfoFrame departmentInfoFrame) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);
        setTitle("添加部门");

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));
        panel.add(new JLabel("部门名称"));
        departmentNameField = new JTextField(10);
        panel.add(departmentNameField);
        panel.add(new JLabel("部门编码"));
        departmentCodeField = new JTextField(10);
        panel.add(departmentCodeField);

        addButton = new JButton("添加");
        addButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String departmentName = departmentNameField.getText();
                String departmentCode = departmentCodeField.getText();
                if (departmentName == null || departmentName.equals("")) {
                    JOptionPane.showMessageDialog(null, "部门名称不能为空");
                    return;
                }
                if (departmentCode == null || departmentCode.equals("")) {
                    JOptionPane.showMessageDialog(null, "部门编码不能为空");
                    return;
                }
                String sql = "insert into busi_department_info(id,department_name,department_code,create_user,create_date,is_deleted,status) values(?,?,?,?,?,0,0)";
                DBToolSet.insertSQL(sql, snowflake.nextId(), departmentName, departmentCode, departmentInfoFrame.loginUserName, new Date());
                JOptionPane.showMessageDialog(AddDepartmentFrame.this, "添加成功");
                departmentInfoFrame.refreshTable();
                dispose();
            }
        });
        panel.add(addButton);
        cancelButton = new JButton("取消");
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        panel.add(cancelButton);

        add(panel);

    }

}
