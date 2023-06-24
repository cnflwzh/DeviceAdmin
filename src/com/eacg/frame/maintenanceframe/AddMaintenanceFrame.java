package com.eacg.frame.maintenanceframe;

import java.awt.GridLayout;
import java.awt.Panel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.eacg.frame.deviceinfoframe.DeviceInfoFrame;
import com.eacg.tools.DBToolSet;

import cn.hutool.core.lang.Snowflake;

public class AddMaintenanceFrame extends JFrame {
    private DeviceInfoFrame deviceInfoFrame;
    private JTextField deviceIdField;
    private JTextArea maintenanceInfoField;
    private JButton confirmButton, cancelButton;
    Snowflake snowflake = new Snowflake(1, 1);

    public AddMaintenanceFrame(DeviceInfoFrame deviceInfoFrame) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
        setTitle("添加维护记录");
        this.deviceInfoFrame = deviceInfoFrame;
        String editingId = deviceInfoFrame.getId();

        Panel panel = new Panel(new GridLayout(5, 2));
        panel.add(new JLabel("设备ID"));
        deviceIdField = new JTextField(10);
        deviceIdField.setText(editingId);
        deviceIdField.setEditable(false);
        panel.add(deviceIdField);

        panel.add(new JLabel("维护内容"));
        maintenanceInfoField = new JTextArea();
        maintenanceInfoField.setLineWrap(true);
        maintenanceInfoField.setWrapStyleWord(true);
        maintenanceInfoField.setRows(10);
        panel.add(maintenanceInfoField);

        confirmButton = new JButton("确认");
        confirmButton.addActionListener(e -> {
            String deviceId = deviceIdField.getText();
            String maintenanceInfo = maintenanceInfoField.getText();
            if (deviceId.equals("") || maintenanceInfo.equals("")) {
                JOptionPane.showMessageDialog(null, "请填写完整信息", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            } 
            String insertMaintenanceTable = "insert into busi_device_maintenance(id, device_id, maintenance_info, create_date,create_user,is_deleted,status)values(?,?,?,?,?,0,0)";
            List<Object> params = new ArrayList<>();
            params.add(snowflake.nextId());
            params.add(editingId);
            params.add(maintenanceInfo);
            params.add(new Date());
            params.add(deviceInfoFrame.getUserName());
            DBToolSet.insertSQL(insertMaintenanceTable,params.toArray());
            String updateDeviceInfoTable = "update busi_device_info set device_is_maintain = 1 where id = ?";
            DBToolSet.updateSQL(updateDeviceInfoTable,editingId);
            JOptionPane.showMessageDialog(AddMaintenanceFrame.this, "添加成功", "提示", JOptionPane.INFORMATION_MESSAGE);
            deviceInfoFrame.reloadData();
            dispose();
        });
        panel.add(confirmButton);
        cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> {
            dispose();
        });
        panel.add(cancelButton);

        add(panel);
        deviceInfoFrame.reloadData();

    }

}
