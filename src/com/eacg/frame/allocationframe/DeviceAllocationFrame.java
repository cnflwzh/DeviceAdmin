package com.eacg.frame.allocationframe;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.eacg.frame.deviceinfoframe.DeviceInfoFrame;
import com.eacg.tools.DBToolSet;

import cn.hutool.core.date.DateUtil;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

public class DeviceAllocationFrame extends JFrame {
    private JTextField allocationIdField, deviceNameField;
    private JComboBox<String> deviceStatusComboBox, deviceIsMaintainComboBox, departmentComboBox;
    private JDatePickerImpl allcationDatePickerImpl, deviceUseLimitDatePickerImpl;
    private JButton searchButton, clearButton, deleteButton, updateButton;
    private JTable table;
    private DefaultTableModel tableModel;

    public DeviceAllocationFrame(DeviceInfoFrame deviceInfoFrame) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setTitle("设备调拨");

        Panel filterPanel = new Panel(new FlowLayout());
        filterPanel.add(new JLabel("调拨编号"));
        allocationIdField = new JTextField(10);
        filterPanel.add(allocationIdField);
        filterPanel.add(new JLabel("设备名称"));
        deviceNameField = new JTextField(10);
        filterPanel.add(deviceNameField);
        filterPanel.add(new JLabel("设备状态"));
        deviceStatusComboBox = new JComboBox<String>();
        deviceStatusComboBox.addItem("全部");
        deviceStatusComboBox.addItem("待分配");
        deviceStatusComboBox.addItem("已调拨");
        deviceStatusComboBox.addItem("报废");
        filterPanel.add(deviceStatusComboBox);
        filterPanel.add(new JLabel("是否维修"));
        deviceIsMaintainComboBox = new JComboBox<String>();
        deviceIsMaintainComboBox.addItem("全部");
        deviceIsMaintainComboBox.addItem("是");
        deviceIsMaintainComboBox.addItem("否");
        filterPanel.add(deviceIsMaintainComboBox);
        filterPanel.add(new JLabel("部门"));
        departmentComboBox = new JComboBox<String>();
        departmentComboBox.addItem("全部");
        List<Map<String, Object>> departmentList = DBToolSet
                .selectSQL("select department_name from busi_department_info where is_deleted=0");
        ;
        for (Map<String, Object> departmentMap : departmentList) {
            departmentComboBox.addItem(String.valueOf(departmentMap.get("department_name")));
        }
        filterPanel.add(departmentComboBox);

        filterPanel.add(new JLabel("                "));
        filterPanel.add(new JLabel("调拨日期"));
        UtilDateModel allcationDateModel = new UtilDateModel();
        JDatePanelImpl allcationDatePanel = new JDatePanelImpl(allcationDateModel);
        allcationDatePickerImpl = new JDatePickerImpl(allcationDatePanel);
        filterPanel.add(allcationDatePickerImpl);

        filterPanel.add(new JLabel("报废时间"));
        UtilDateModel useLimitModel = new UtilDateModel();
        JDatePanelImpl useLimitPanel = new JDatePanelImpl(useLimitModel);
        deviceUseLimitDatePickerImpl = new JDatePickerImpl(useLimitPanel);
        filterPanel.add(deviceUseLimitDatePickerImpl);

        Panel actionPanel = new Panel(new FlowLayout());
        updateButton = new JButton("修改");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = table.getSelectedRows();
                if (selectedRows.length == 0) {
                    JOptionPane.showMessageDialog(DeviceAllocationFrame.this, "请选择要修改的数据", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (selectedRows.length > 1) {
                    JOptionPane.showMessageDialog(DeviceAllocationFrame.this, "一次只能修改一条数据", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String allocationId = String.valueOf(table.getValueAt(selectedRows[0], 0));
                new DeviceAllocationUpdateFrame(DeviceAllocationFrame.this,allocationId).setVisible(true);
            }
        });
        actionPanel.add(updateButton);
        deleteButton = new JButton("删除");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(JOptionPane.showConfirmDialog(DeviceAllocationFrame.this, "确认删除？", "确认", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                    return;
                }
                int[] selectedRows = table.getSelectedRows();
                if (selectedRows.length == 0) {
                    JOptionPane.showMessageDialog(DeviceAllocationFrame.this, "请选择要删除的数据", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                for (int i = 0; i < selectedRows.length; i++) {
                    String allocationId = String.valueOf(table.getValueAt(selectedRows[i], 0));
                    DBToolSet.updateSQL("update busi_device_allocation set is_deleted=1 where id=" + allocationId);
                }
                searchButton.doClick();
            }
        });
        actionPanel.add(deleteButton);

        searchButton = new JButton("查询");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String allocationId = allocationIdField.getText();
                String deviceName = deviceNameField.getText();
                String deviceStatus = String.valueOf(deviceStatusComboBox.getSelectedItem());
                int deviceStatusValue = -1;
                switch (deviceStatus) {
                    case "待分配":
                        deviceStatusValue = 0;
                        break;
                    case "已调拨":
                        deviceStatusValue = 1;
                        break;
                    case "报废":
                        deviceStatusValue = 2;
                        break;
                    default:
                        break;
                }

                String deviceIsMaintain = String.valueOf(deviceIsMaintainComboBox.getSelectedItem());
                int deviceIsMaintainValue = -1;
                switch (deviceIsMaintain) {
                    case "否":
                        deviceIsMaintainValue = 0;
                        break;
                    case "是":
                        deviceIsMaintainValue = 1;
                        break;
                    default:
                        break;
                }
                String department = String.valueOf(departmentComboBox.getSelectedItem());
                String allocationDate =  allcationDatePickerImpl.getJFormattedTextField().getText();
                String deviceUseLimit =deviceUseLimitDatePickerImpl.getJFormattedTextField().getText();
                ArrayList<Object> params = new ArrayList<>();
                String sqlString = """
                                            SELECT
                                            	bda.id AS allocationId,
                                            	bdi.device_name AS deviceName,
                                            	bdi.device_use_limit AS deviceUseLimit,
                                            	( SELECT department_name FROM busi_department_info WHERE id = bda.department_id ) AS departmentName,
                                            	bda.create_date AS allocationDate,
                                            	bda.create_user AS allocationUser,
                                            	bdi.device_status AS deviceStatus,
                                            	bdi.device_is_maintain AS deviceIsMaintain
                                            FROM
                                            	busi_device_allocation AS bda
                                            	LEFT JOIN busi_device_info AS bdi ON bda.device_id = bdi.id
                                            WHERE
                                            	bdi.is_deleted = 0
                                            	AND bda.is_deleted = 0
                                            """;
                if (!allocationId.isBlank()) {
                    sqlString += " AND bda.id = ?";
                    params.add(allocationId);
                }
                if (!deviceName.isBlank()) {
                    sqlString += " AND bdi.device_name = ?";
                    params.add(deviceName);
                }
                if (!deviceStatus.equals("全部")) {
                    sqlString += " AND bdi.device_status = ?";
                    params.add(deviceStatusValue);
                }
                if (!deviceIsMaintain.equals("全部")) {
                    sqlString += " AND bdi.device_is_maintain = ?";
                    params.add(deviceIsMaintainValue);
                }
                if (!department.equals("全部")) {
                   String departmentId = String.valueOf( DBToolSet.selectSQL("select id from busi_department_info where department_name = ?",department).get(0).get("id"));;
                    sqlString += " AND bda.department_id = ?";
                    params.add(departmentId);
                }
                if (allocationDate!=null&&!allocationDate.isEmpty()) {
                    sqlString += " AND bda.create_date >= ? AND bda.create_date <= ?";
                    params.add(DateUtil.parse(allocationDate + " 00:00:00"));
                    params.add(DateUtil.parse(allocationDate + " 23:59:59"));
                }
                if (deviceUseLimit!=null&&!deviceUseLimit.isEmpty()) {
                    sqlString += " AND bdi.device_use_limit >= ? AND bdi.device_use_limit <= ?";
                    params.add(DateUtil.parse(deviceUseLimit + " 00:00:00"));
                    params.add(DateUtil.parse(deviceUseLimit + " 23:59:59"));
                }
                List<Map<String, Object>> allocationList = DBToolSet.selectSQL(sqlString, params.toArray());
                tableModel.setRowCount(0);
                for (Map<String, Object> allocationMap : allocationList) {
                    String[] rowData = { String.valueOf(allocationMap.get("allocationId")),
                            String.valueOf(allocationMap.get("deviceName")),
                            String.valueOf(allocationMap.get("deviceUseLimit")),
                            String.valueOf(allocationMap.get("departmentName")),
                            String.valueOf(allocationMap.get("allocationDate")),
                            String.valueOf(allocationMap.get("allocationUser")),
                            "",
                            "" };
                    switch (String.valueOf(allocationMap.get("deviceStatus"))) {
                        case "0":
                            rowData[6] = "待分配";
                            break;
                        case "1":
                            rowData[6] = "已调拨";
                            break;
                        case "2":
                            rowData[6] = "报废";
                            break;
                        default:
                        break;
                    }
                        
                       switch(String.valueOf(allocationMap.get("deviceIsMaintain"))) {
                        case "0":
                            rowData[7] = "否";
                            break;
                        case "1":
                            rowData[7] = "是";
                            break;
                        default:
                            break;
                    }
                    tableModel.addRow(rowData);
                }
            }
        });
        actionPanel.add(searchButton);
        clearButton = new JButton("清空");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                allocationIdField.setText("");
                deviceNameField.setText("");
                deviceStatusComboBox.setSelectedIndex(0);
                deviceIsMaintainComboBox.setSelectedIndex(0);
                departmentComboBox.setSelectedIndex(0);
                allcationDatePickerImpl.getModel().setValue(null);
                deviceUseLimitDatePickerImpl.getModel().setValue(null);
            }
        });
        actionPanel.add(clearButton);
        String[] columnNames = { "调拨ID", "设备名称", "报废时间", "部门", "调拨时间", "调拨人", "设备状态", "是否维修" };
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Add all panels to the frame
        setLayout(new BorderLayout());
        filterPanel.setPreferredSize(new Dimension(getWidth(), 80));
        add(filterPanel, BorderLayout.NORTH);
        add(actionPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        // Load data
        loadData();

    }

    private void loadData() {
        String sqlString = """
                                    SELECT
                	bda.id AS allocationId,
                	bdi.device_name AS deviceName,
                	bdi.device_use_limit AS deviceUseLimit,
                	( SELECT department_name FROM busi_department_info WHERE id = bda.department_id ) AS departmentName,
                	bda.create_date AS allocationDate,
                	bda.create_user AS allocationUser,
                	bdi.device_status AS deviceStatus,
                	bdi.device_is_maintain AS deviceIsMaintain
                FROM
                	busi_device_allocation AS bda
                	LEFT JOIN busi_device_info AS bdi ON bda.device_id = bdi.id
                WHERE
                	bdi.is_deleted = 0
                	AND bda.is_deleted = 0;
                                    """;
        List<Map<String, Object>> devices = DBToolSet.selectSQL(sqlString);
        for (Map<String, Object> device : devices) {
            Object[] rowData = new Object[8];
            rowData[0] = device.get("allocationId");
            rowData[1] = device.get("deviceName");
            rowData[2] = device.get("deviceUseLimit");
            rowData[3] = device.get("departmentName");
            rowData[4] = device.get("allocationDate");
            rowData[5] = device.get("allocationUser");
            switch (String.valueOf(device.get("deviceStatus"))) {
                case "0":
                    rowData[6] = "待分配";
                    break;
                case "1":
                    rowData[6] = "已调拨";
                    break;
                case "2":
                    rowData[6] = "报废";
                    break;
                default:
                    rowData[6] = "未知";
                    break;
            }
            switch (String.valueOf(device.get("deviceIsMaintain"))) {
                case "0":
                    rowData[7] = "否";
                    break;
                case "1":
                    rowData[7] = "是";
                    break;
                default:
                    rowData[7] = "未知";
                    break;
            }
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
