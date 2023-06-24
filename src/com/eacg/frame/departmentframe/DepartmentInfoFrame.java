package com.eacg.frame.departmentframe;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import com.eacg.frame.deviceinfoframe.DeviceInfoFrame;
import com.eacg.tools.DBToolSet;


public class DepartmentInfoFrame extends JFrame {
    static String editingId;
    static String loginUserName;

    private JTextField departmenIdField, departmentNameField, departmentCodeField;
    private JButton searchButton, addButton, modifyButton, deleteButton, clearButton;
    private JTable table;
    private DefaultTableModel tableModel;

    public DepartmentInfoFrame(DeviceInfoFrame deviceInfoFrame) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setTitle("部门信息管理");

        JPanel filterPanel = new JPanel();

        filterPanel.setLayout(new FlowLayout());
        filterPanel.add(new JLabel("部门id"));
        departmenIdField = new JTextField(10);
        filterPanel.add(departmenIdField);

        filterPanel.add(new JLabel("部门名称"));
        departmentNameField = new JTextField(10);
        filterPanel.add(departmentNameField);

        filterPanel.add(new JLabel("部门CODE"));
        departmentCodeField = new JTextField(10);
        filterPanel.add(departmentCodeField);

        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(new FlowLayout());
        this.loginUserName = deviceInfoFrame.getUserName();
        addButton = new JButton("添加部门");
        addButton.addActionListener(e -> new AddDepartmentFrame(DepartmentInfoFrame.this).setVisible(true));
        modifyButton = new JButton("修改部门");
        modifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = table.getSelectedRows();
                if (selectedRows.length == 0) {
                    JOptionPane.showMessageDialog(DepartmentInfoFrame.this, "请选择要修改的部门");
                    return;
                }
                String id = (String) tableModel.getValueAt(selectedRows[0], 0);
                DepartmentInfoFrame.editingId = id;
                new ModifyDepartmentFrame(DepartmentInfoFrame.this)
                        .setVisible(true);
            }
        });
        deleteButton = new JButton("删除部门");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = table.getSelectedRows();
                if (selectedRows.length == 0) {
                    JOptionPane.showMessageDialog(DepartmentInfoFrame.this, "请选择要删除的部门");
                    return;
                }
                int result = JOptionPane.showConfirmDialog(DepartmentInfoFrame.this, "确认删除？");
                if (result == JOptionPane.OK_OPTION) {
                    for (int i = selectedRows.length - 1; i >= 0; i--) {
                        String id = (String) tableModel.getValueAt(selectedRows[i], 0);
                        DBToolSet.updateSQL("UPDATE busi_department_info SET is_deleted = 1 WHERE id = ?", id);
                        tableModel.removeRow(selectedRows[i]);
                    }
                }
            }
        });
        searchButton = new JButton("查询部门");
        searchButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String departmentId = departmenIdField.getText();
                String departmentName = departmentNameField.getText();
                String departmentCode = departmentCodeField.getText();
                String sql = "SELECT * FROM busi_department_info WHERE is_deleted = 0";
                if (departmentId != null && !departmentId.isEmpty()) {
                    sql += " AND id = '" + departmentId + "'";
                }
                if (departmentName != null && !departmentName.isEmpty()) {
                    sql += " AND department_name = '" + departmentName + "'";
                }
                if (departmentCode != null && !departmentCode.isEmpty()) {
                    sql += " AND department_code = '" + departmentCode + "'";
                }
                List<Map<String, Object>> users = DBToolSet.selectSQL(sql);
                tableModel.setRowCount(0);
                for (Map<String, Object> user : users) {
                    Object[] rowData = new Object[5];
                    rowData[0] = user.get("id");
                    rowData[1] = user.get("department_name");
                    rowData[2] = user.get("department_code");
                    rowData[3] = user.get("create_date");
                    rowData[4] = user.get("create_user");
                    tableModel.addRow(rowData);
                }
            }

        });
        clearButton = new JButton("清空查询条件");
        clearButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                departmenIdField.setText("");
                departmentNameField.setText("");
                departmentCodeField.setText("");
            }

        });
        actionsPanel.add(addButton);
        actionsPanel.add(modifyButton);
        actionsPanel.add(deleteButton);
        actionsPanel.add(searchButton);
        actionsPanel.add(clearButton);

        String[] columnNames = { "ID", "部门名称", "部门代号", "创建时间", "创建者" };
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        setLayout(new BorderLayout());
        add(filterPanel, BorderLayout.NORTH);
        add(actionsPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        // Load data
        loadData();

    }

    private void loadData() {
        List<Map<String, Object>> users = DBToolSet.selectSQL("SELECT * FROM busi_department_info where is_deleted = 0");
        for (Map<String, Object> user : users) {
            Object[] rowData = new Object[5];
            rowData[0] = user.get("id");
            rowData[1] = user.get("department_name");
            rowData[2] = user.get("department_code");
            rowData[3] = user.get("create_date");
            rowData[4] = user.get("create_user");
            tableModel.addRow(rowData);
        }
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        loadData();
    }

}
