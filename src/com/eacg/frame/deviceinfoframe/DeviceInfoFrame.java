package com.eacg.frame.deviceinfoframe;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.eacg.frame.allocationframe.AddAllocationFrame;
import com.eacg.frame.allocationframe.DeviceAllocationFrame;
import com.eacg.frame.departmentframe.DepartmentInfoFrame;
import com.eacg.frame.maintenanceframe.AddMaintenanceFrame;
import com.eacg.frame.maintenanceframe.DeviceMaintenanceFrame;
import com.eacg.frame.userframe.UserInfoFrame;
import com.eacg.tools.DBToolSet;
import cn.hutool.core.date.DateUtil;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeviceInfoFrame extends JFrame {
    private String userName;
    private String id;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private JTextField deviceNameField;
    private JTextField deviceTypeField;
    private JTextField devicePriceField;
    private JComboBox<String> deviceStatusField;
    private JComboBox<String> deviceMaintenanceField;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton searchButton, clearButton, addDeviceButton, modifyDeviceButton, deleteDeviceButton,maintenanceButton,allocationButton;

    public DeviceInfoFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setTitle("设备信息管理");

        // Top menu
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createMenu("用户信息管理", e -> {
            UserInfoFrame userInfoFrame = new UserInfoFrame(DeviceInfoFrame.this);
            userInfoFrame.setVisible(true);
        }));
        menuBar.add(createMenu("部门信息管理", e -> {
            DepartmentInfoFrame departmentInfoFrame = new DepartmentInfoFrame(DeviceInfoFrame.this);
            departmentInfoFrame.setVisible(true);
        }));
        menuBar.add(createMenu("设备维护管理", e -> {
            DeviceMaintenanceFrame deviceMaintenanceFrame = new DeviceMaintenanceFrame(DeviceInfoFrame.this);
            deviceMaintenanceFrame.setVisible(true);
        }));
        menuBar.add(createMenu("设备调拨管理", e -> {
            DeviceAllocationFrame deviceAllocationFrame = new DeviceAllocationFrame(DeviceInfoFrame.this);
            deviceAllocationFrame.setVisible(true);
        }));
        setJMenuBar(menuBar);
        // Filter section
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new FlowLayout());
        filterPanel.add(new JLabel("设备名"));
        deviceNameField = new JTextField(10);
        filterPanel.add(deviceNameField);
        filterPanel.add(new JLabel("设备类型"));
        deviceTypeField = new JTextField(10);
        filterPanel.add(deviceTypeField);
        filterPanel.add(new JLabel("设备价格"));
        devicePriceField = new JTextField(10);
        filterPanel.add(devicePriceField);
        filterPanel.add(new JLabel("设备状态"));
        deviceStatusField = new JComboBox<String>(new String[] { "全部", "待分配", "已调拨", "报废" });
        filterPanel.add(deviceStatusField);
        filterPanel.add(new JLabel("是否维护过"));
        deviceMaintenanceField = new JComboBox<String>(new String[] { "全部", "否", "是" });
        filterPanel.add(deviceMaintenanceField);

        // Buttons for device actions
        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(new FlowLayout());
        addDeviceButton = new JButton("添加设备");
        addDeviceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddDeviceFrame(DeviceInfoFrame.this).setVisible(true);
            }
        });

        actionsPanel.add(addDeviceButton);
        modifyDeviceButton = new JButton("修改设备");
        modifyDeviceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    String deviceId = (String) tableModel.getValueAt(selectedRow, 0);
                    DeviceInfoFrame.this.setId(deviceId);
                    DeviceInfoFrame.this.setUserName(deviceId);
                    new ModifyDeviceFrame(DeviceInfoFrame.this).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(DeviceInfoFrame.this, "请先选择一个设备！", "错误", JOptionPane.ERROR_MESSAGE);
                }

                
            }
        });
        actionsPanel.add(modifyDeviceButton);
        deleteDeviceButton = new JButton("删除设备");
        deleteDeviceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get selected row
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    // Get device ID of selected row
                    String deviceId = (String) tableModel.getValueAt(selectedRow, 0);
                    // Update is_deleted in database
                    DBToolSet.updateSQL("UPDATE busi_device_info SET is_deleted = 1 WHERE id = ?", deviceId);
                    // Refresh table
                    tableModel.setRowCount(0); // Clear table
                    loadData(); // Reload data
                } else {
                    JOptionPane.showMessageDialog(DeviceInfoFrame.this, "请先选择一个设备！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        maintenanceButton = new JButton("维护设备");
        maintenanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    DeviceInfoFrame.this.setId((String) tableModel.getValueAt(selectedRow, 0));

                   new AddMaintenanceFrame(DeviceInfoFrame.this).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(DeviceInfoFrame.this, "请先选择一个设备！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        actionsPanel.add(maintenanceButton);



        allocationButton = new JButton("调拨设备");
        allocationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    // Get device ID of selected row
                    DeviceInfoFrame.this.setId((String) tableModel.getValueAt(selectedRow, 0));
                    new AddAllocationFrame(DeviceInfoFrame.this).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(DeviceInfoFrame.this, "请先选择一个设备！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        actionsPanel.add(allocationButton);


        actionsPanel.add(deleteDeviceButton);
        searchButton = new JButton("搜索");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StringBuilder sql = new StringBuilder("SELECT * FROM busi_device_info WHERE is_deleted = 0");
                List<Object> params = new ArrayList<>();

                String deviceName = deviceNameField.getText().trim();
                if (!deviceName.isEmpty()) {
                    sql.append(" AND device_name LIKE ?");
                    params.add("%" + deviceName + "%");
                }
                String deviceType = deviceTypeField.getText().trim();
                if (!deviceType.isEmpty()) {
                    sql.append(" AND device_type LIKE ?");
                    params.add("%" + deviceType + "%");
                }
                String devicePrice = devicePriceField.getText().trim();
                if (!devicePrice.isEmpty()) {
                    sql.append(" AND device_price = ?");
                    params.add(devicePrice);
                }
                String deviceStatus = (String) deviceStatusField.getSelectedItem();
                if (!"全部".equals(deviceStatus)) {
                    int statusValue = "待分配".equals(deviceStatus) ? 0 : "已调拨".equals(deviceStatus) ? 1 : 2;
                    sql.append(" AND device_status = ?");
                    params.add(statusValue);
                }
                String deviceMaintenance = (String) deviceMaintenanceField.getSelectedItem();
                if (!"全部".equals(deviceMaintenance)) {
                    int maintenanceValue = "否".equals(deviceMaintenance) ? 0 : 1;
                    sql.append(" AND device_is_maintain = ?");
                    params.add(maintenanceValue);
                }

                // Clear table and load data
                tableModel.setRowCount(0);
                List<Map<String, Object>> devices = DBToolSet.selectSQL(sql.toString(), params.toArray());
                for (Map<String, Object> device : devices) {
                    Object[] rowData = new Object[11];
                    rowData[0] = device.get("id");
                    rowData[1] = device.get("device_name");
                    rowData[2] = device.get("device_type");
                    rowData[3] = device.get("device_price");
                    rowData[4] = device.get("device_purchase_date");
                    rowData[5] = device.get("device_use_limit");

                    // Add percent sign after discount rate
                    rowData[6] = device.get("device_discount_rate") + "%";

                    // Translate device status to Chinese
                    int deviceStatusTable = Integer.parseInt(String.valueOf(device.get("device_status")));
                    if (deviceStatusTable == 0) {
                        rowData[7] = "待分配";
                    } else if (deviceStatusTable == 1) {
                        rowData[7] = "已调拨";
                    } else {
                        rowData[7] = "报废";
                    }

                    // Translate maintenance status to Chinese
                    int maintenanceStatus = Integer.parseInt(String.valueOf(device.get("device_is_maintain")));
                    rowData[8] = maintenanceStatus == 0 ? "否" : "是";

                    rowData[9] = DateUtil.format(DateUtil.parse(String.valueOf(device.get("create_date"))),
                            "yyyy-MM-dd HH:mm:ss");
                    rowData[10] = device.get("create_user");
                    tableModel.addRow(rowData);
                }
            }
        });

        actionsPanel.add(searchButton);
        clearButton = new JButton("清空");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deviceNameField.setText("");
                deviceTypeField.setText("");
                devicePriceField.setText("");
                deviceStatusField.setSelectedIndex(0);
                deviceMaintenanceField.setSelectedIndex(0);

            }
        });
        actionsPanel.add(clearButton);

        // Table for device info
        String[] columnNames = { "设备ID", "设备名", "设备类型", "设备价格", "采购时间", "使用年限", "折现率", "设备状态", "是否维护过", "创建时间",
                "创建用户" };
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Add all panels to the frame
        setLayout(new BorderLayout());
        add(filterPanel, BorderLayout.NORTH);
        add(actionsPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        // Load data
        loadData();
    }

    private JMenu createMenu(String title, ActionListener listener) {
        JMenu menu = new JMenu(title);
        JMenuItem menuItem = new JMenuItem(title);
        menuItem.addActionListener(listener);
        menu.add(menuItem);
        return menu;
    }

    private void loadData() {
        List<Map<String, Object>> devices = DBToolSet.selectSQL("SELECT * FROM busi_device_info where is_deleted = 0");
        for (Map<String, Object> device : devices) {
            Object[] rowData = new Object[11];
            rowData[0] = device.get("id");
            rowData[1] = device.get("device_name");
            rowData[2] = device.get("device_type");
            rowData[3] = device.get("device_price");
            rowData[4] = device.get("device_purchase_date");
            rowData[5] = device.get("device_use_limit");

            // Add percent sign after discount rate
            rowData[6] = device.get("device_discount_rate") + "%";

            // Translate device status to Chinese
            int deviceStatus = Integer.parseInt(String.valueOf(device.get("device_status")));
            if (deviceStatus == 0) {
                rowData[7] = "待分配";
            } else if (deviceStatus == 1) {
                rowData[7] = "已调拨";
            } else {
                rowData[7] = "报废";
            }

            // Translate maintenance status to Chinese
            int maintenanceStatus = Integer.parseInt(String.valueOf(device.get("device_is_maintain")));
            rowData[8] = maintenanceStatus == 0 ? "否" : "是";

            rowData[9] = DateUtil.format(DateUtil.parse(String.valueOf(device.get("create_date"))),
                    "yyyy-MM-dd HH:mm:ss");
            rowData[10] = device.get("create_user");
            tableModel.addRow(rowData);
        }
    }

    public void reloadData() {
        // Clear table
        tableModel.setRowCount(0);
        // Reload data
        loadData();
    }
}
