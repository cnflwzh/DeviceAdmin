package com.eacg.frame.deviceinfoframe;

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
import javax.swing.JTextField;

import com.eacg.tools.DBToolSet;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

public class ModifyDeviceFrame extends JFrame {
    private JTextField deviceNameField, deviceTypeField, devicePriceField, deviceDiscountRateField;
    private JButton confirmButton, cancelButton;
    private JDatePickerImpl devicePurchaseDatePicker, deviceUseLimitDatePicker;
    private JComboBox<String> deviceStatusComboBox, deviceMaintainComboBox;

    ModifyDeviceFrame(DeviceInfoFrame deviceInfoFrame) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);
        setTitle("修改设备信息");
        String id = deviceInfoFrame.getId();
        String sql = "select * from busi_device_info where id = " + id;
        List<Map<String, Object>> selectResult = DBToolSet.selectSQL(sql);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(9, 2));
        panel.add(new JLabel("设备名"));
        deviceNameField = new JTextField(10);
        deviceNameField.setText(String.valueOf(selectResult.get(0).get("device_name")));
        panel.add(deviceNameField);

        panel.add(new JLabel("设备类型"));
        deviceTypeField = new JTextField(10);
        deviceTypeField.setText(String.valueOf(selectResult.get(0).get("device_type")));
        panel.add(deviceTypeField);

        panel.add(new JLabel("设备价格"));
        devicePriceField = new JTextField(10);
        devicePriceField.setText(String.valueOf(selectResult.get(0).get("device_price")));
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
        deviceDiscountRateField.setText(String.valueOf(selectResult.get(0).get("device_discount_rate")));
        panel.add(deviceDiscountRateField);

        panel.add(new JLabel("设备状态"));
        deviceStatusComboBox = new JComboBox<String>(new String[] { "待分配", "已调拨", "报废" });
        String deviceStatusString;
        switch (String.valueOf(selectResult.get(0).get("device_status"))) {
            case "0":
                deviceStatusString = "待分配";
                break;
            case "1":
                deviceStatusString = "已调拨";
                break;
            case "2":
                deviceStatusString = "报废";
                break;
            default:
                deviceStatusString = "全部";
                break;
        }
        deviceStatusComboBox.setSelectedItem(deviceStatusString);
        panel.add(deviceStatusComboBox);

        panel.add(new JLabel("是否维护过"));
        deviceMaintainComboBox = new JComboBox<String>(new String[] { "是", "否" });
        String deviceMaintainString;
        switch (String.valueOf(selectResult.get(0).get("device_is_maintain"))) {
            case "0":
                deviceMaintainString = "否";
                break;
            case "1":
                deviceMaintainString = "是";
                break;
            default:
                deviceMaintainString = "无";
                break;
        }
        deviceMaintainComboBox.setSelectedItem(deviceMaintainString);
        panel.add(deviceMaintainComboBox);

        confirmButton = new JButton("确认");
        cancelButton = new JButton("取消");

        confirmButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                // Get the values from the input fields
                String deviceName = deviceNameField.getText();
                String deviceType = deviceTypeField.getText();
                double devicePrice = Double.parseDouble(devicePriceField.getText());
                Date purchaseDate = (Date) devicePurchaseDatePicker.getModel().getValue();
                Date useLimitDate = (Date) deviceUseLimitDatePicker.getModel().getValue();
                double discountRate = Double.parseDouble(deviceDiscountRateField.getText());
                int deviceStatus = deviceStatusComboBox.getSelectedIndex();
                int deviceMaintain;
                String deviceMaintainString = deviceMaintainComboBox.getSelectedItem().toString();
                switch (deviceMaintainString) {
                    case "是":
                        deviceMaintain = 1;
                        break;
                    case "否":
                        deviceMaintain = 0;
                        break;
                    default:
                        deviceMaintain = -1;
                        break;
                }

                StringBuffer sqlBuffer = new StringBuffer();
                sqlBuffer.append("update busi_device_info set device_name=?,device_type=?,device_price=?");
                List<Object> params = new ArrayList<>();
                params.add(deviceName);
                params.add(deviceType);
                params.add(devicePrice);

                if (purchaseDate != null) {
                    sqlBuffer.append(",device_purchase_date=?");
                    params.add(purchaseDate);
                }
                if (useLimitDate != null) {
                    sqlBuffer.append(",device_use_limit=?");
                    params.add(useLimitDate);
                }
                sqlBuffer.append(",device_discount_rate=?,device_status=?,device_is_maintain=? where id=?");
                params.add(discountRate);
                params.add(deviceStatus);
                params.add(deviceMaintain);
                params.add(id);

                Object[] paramsArray = params.toArray();

                DBToolSet.updateSQL(sqlBuffer.toString(), paramsArray);
                JOptionPane.showMessageDialog(null, "修改成功");
                dispose();

            }

        });

        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }

        });

        setContentPane(panel);
        panel.add(confirmButton);
        panel.add(cancelButton);

    }
}
