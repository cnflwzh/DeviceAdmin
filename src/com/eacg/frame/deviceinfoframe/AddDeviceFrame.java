package com.eacg.frame.deviceinfoframe;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.*;

import com.eacg.tools.DBToolSet;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

public class AddDeviceFrame extends JFrame {
    private JTextField deviceNameField;
    private JTextField deviceTypeField;
    private JTextField devicePriceField;
    private JTextField deviceDiscountRateField;
    private JButton confirmButton, cancelButton;
    private DeviceInfoFrame deviceInfoFrame;
    Snowflake snowflake = IdUtil.getSnowflake(1, 1);
    private JDatePickerImpl devicePurchaseDatePicker;
    private JDatePickerImpl deviceUseLimitDatePicker;
    

    public AddDeviceFrame(DeviceInfoFrame deviceInfoFrame) {
        this.deviceInfoFrame = deviceInfoFrame;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setTitle("添加设备信息");
        String userName = deviceInfoFrame.getUserName();

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 2));
        panel.add(new JLabel("设备名"));
        deviceNameField = new JTextField(10);
        panel.add(deviceNameField);
        panel.add(new JLabel("设备类型"));
        deviceTypeField = new JTextField(10);
        panel.add(deviceTypeField);
        panel.add(new JLabel("设备价格"));
        devicePriceField = new JTextField(10);
        panel.add(devicePriceField);

        panel.add(new JLabel("采购时间"));
        UtilDateModel purchaseDateModel = new UtilDateModel();
        JDatePanelImpl purchaseDatePanel = new JDatePanelImpl(purchaseDateModel);
        devicePurchaseDatePicker = new JDatePickerImpl(purchaseDatePanel);
        panel.add(devicePurchaseDatePicker);

        panel.add(new JLabel("使用截止日期"));
        UtilDateModel useLimitModel = new UtilDateModel();
        JDatePanelImpl useLimitPanel = new JDatePanelImpl(useLimitModel);
        deviceUseLimitDatePicker = new JDatePickerImpl(useLimitPanel);
        panel.add(deviceUseLimitDatePicker);

        panel.add(new JLabel("折现率"));
        deviceDiscountRateField = new JTextField(10);
        panel.add(deviceDiscountRateField);

        confirmButton = new JButton("确认");
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Add device
                Date purchaseDate = (Date) devicePurchaseDatePicker.getModel().getValue();

                Date useLimitDate = (Date) deviceUseLimitDatePicker.getModel().getValue();

                String sql = "INSERT INTO busi_device_info ( id, device_name, device_type, device_price, device_purchase_date, device_use_limit, device_discount_rate, device_status, device_is_maintain, create_date, create_user, is_deleted, status ) VALUES(?,?,?,?,?,?,?, 0, 0,?,?, 0, 0 )";
                DBToolSet.insertSQL(sql, snowflake.nextId(), deviceNameField.getText(), deviceTypeField.getText(),
                        devicePriceField.getText(), purchaseDate, useLimitDate, deviceDiscountRateField.getText(),
                        new Date(),userName );

                // Refresh device info table
                deviceInfoFrame.reloadData();
                // Close this frame
                dispose();
            }
        });
        panel.add(confirmButton);
        JButton cancelButton = new JButton("取消");
        panel.add(cancelButton);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        setContentPane(panel);
    }
}
