package com.eacg.frame.maintenanceframe;

import java.awt.GridLayout;
import java.awt.Panel;
import java.util.Date;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.eacg.tools.DBToolSet;

public class ModifyDeviceMaintenanceFrame extends JFrame {
    private JTextField deviceIdField;
    private JTextArea maintenanceContentField;
    private JButton confirmButton, cancelButton;

    public ModifyDeviceMaintenanceFrame(DeviceMaintenanceFrame deviceMaintenanceFrame) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setTitle("修改设备维护");
        String editingId = String.valueOf(DeviceMaintenanceFrame.editingId);
        Map<String, Object> deviceInfo = DBToolSet
                .selectSQL("select * from busi_device_maintenance where id=?", editingId).get(0);
        ;

        Panel panel = new Panel(new GridLayout(3, 2));
        panel.add(new JLabel("设备ID"));
        deviceIdField = new JTextField(String.valueOf(deviceInfo.get("device_id")));
        deviceIdField.setEditable(false);
        panel.add(deviceIdField);

        panel.add(new JLabel("维护内容"));
        maintenanceContentField = new JTextArea(String.valueOf(deviceInfo.get("maintenance_info")));
        maintenanceContentField.setLineWrap(true);
        maintenanceContentField.setWrapStyleWord(true);
        maintenanceContentField.setRows(10);
        panel.add(maintenanceContentField);

        confirmButton = new JButton("确认");
        confirmButton.addActionListener(e -> {
            String maintenanceContent = maintenanceContentField.getText();
            if (maintenanceContent.equals("")) {
                JOptionPane.showMessageDialog(null, "请填写完整信息", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String updateMaintenanceTable = "update busi_device_maintenance set maintenance_info=? ,update_user=?,update_date=? where id=? and is_deleted=0";
            DBToolSet.updateSQL(updateMaintenanceTable, maintenanceContent, DeviceMaintenanceFrame.loginUserName,
                    new Date(), DeviceMaintenanceFrame.editingId);
            deviceMaintenanceFrame.reloadData();
            dispose();
        });
        panel.add(confirmButton);
        cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> {
            dispose();
        });
        panel.add(cancelButton);
        add(panel);
    }
}
