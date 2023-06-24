package com.eacg.frame.departmentframe;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.eacg.tools.DBToolSet;

public class ModifyDepartmentFrame extends JFrame {
    private JTextField departmentNameField, departmentCodeField;
    private DepartmentInfoFrame departmentInfoFrame;
    private JButton addButton, cancelButton;

    public ModifyDepartmentFrame(DepartmentInfoFrame departmentInfoFrame) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);
        setTitle("修改部门");
        JPanel panel = new JPanel();
        Map<String, Object> map = DBToolSet
                .selectSQL("select * from busi_department_info where is_deleted=0 and id=?",
                        departmentInfoFrame.editingId)
                .get(0);

        panel.setLayout(new GridLayout(3, 2));
        panel.add(new JLabel("部门名称"));
        departmentNameField = new JTextField(10);
        departmentNameField.setText(String.valueOf(map.get("department_name")));
        panel.add(departmentNameField);
        panel.add(new JLabel("部门编码"));
        departmentCodeField = new JTextField(10);
        departmentCodeField.setText(String.valueOf(map.get("department_code")));
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
                String sql = "update busi_department_info set department_name=?,department_code=?,update_user=?,update_date=? where id=?";

                DBToolSet.updateSQL(sql, departmentName, departmentCode,
                        departmentInfoFrame.loginUserName, new Date(), departmentInfoFrame.editingId);
                JOptionPane.showMessageDialog(ModifyDepartmentFrame.this, "修改成功");
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
