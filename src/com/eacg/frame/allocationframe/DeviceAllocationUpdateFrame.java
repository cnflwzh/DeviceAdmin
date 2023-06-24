package com.eacg.frame.allocationframe;

import java.awt.GridLayout;
import java.awt.Panel;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.eacg.tools.DBToolSet;

import cn.hutool.core.lang.Snowflake;

public class DeviceAllocationUpdateFrame extends JFrame {
    private JComboBox<String> departmentComboBox;
    private JTextField allocationIdField, deviceIdField;
    private JButton confirmButton, cancelButton;

    public DeviceAllocationUpdateFrame(DeviceAllocationFrame deviceAllocationFrame, String id) {
        this.setSize(500, 400);
        this.setTitle("添加设备分配信息");
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        deviceAllocationFrame.setEnabled(false);

        Panel panel = new Panel(new GridLayout(5, 2));
        panel.add(new JLabel("调拨编号："));
        allocationIdField = new JTextField(10);
        allocationIdField.setText(id);
        allocationIdField.setEditable(false);
        panel.add(allocationIdField);

        panel.add(new JLabel("设备编号："));
        deviceIdField = new JTextField(10);
        deviceIdField.setText(id);
        deviceIdField.setEditable(false);
        panel.add(deviceIdField);

        panel.add(new JLabel("分配部门："));
        departmentComboBox = new JComboBox<>();
        DBToolSet.selectSQL("select id,department_name from busi_department_info where is_deleted=0").forEach(map -> {
            departmentComboBox.addItem(String.valueOf(map.get("department_name")));
        });
        panel.add(departmentComboBox);

        confirmButton = new JButton("确定");
        confirmButton.addActionListener(e -> {
            String departmentString = departmentComboBox.getSelectedItem().toString();
            String departmentId = String.valueOf(
                    DBToolSet.selectSQL("select id from busi_department_info where is_deleted=0 and department_name=?",
                            departmentString).get(0).get("id"));
            ;
            String sql = "update busi_device_allocation set department_id=? where id=?";
            DBToolSet.updateSQL(sql, departmentId, id);
            deviceAllocationFrame.setEnabled(true);
            deviceAllocationFrame.reloadData();
            this.dispose();
        });
        panel.add(confirmButton);
        cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> {
            deviceAllocationFrame.setEnabled(true);
            this.dispose();
        });
        panel.add(cancelButton);
        add(panel);

    }

}
