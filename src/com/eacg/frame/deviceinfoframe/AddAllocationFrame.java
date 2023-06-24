package com.eacg.frame.deviceinfoframe;

import java.awt.GridLayout;
import java.awt.Panel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.eacg.tools.DBToolSet;

import cn.hutool.core.lang.Snowflake;

public class AddAllocationFrame  extends JFrame{
    private JComboBox<String> departmentComboBox;
    private JTextField deviceIdField;
     private JButton confirmButton,cancelButton;
    Snowflake snowflake = new Snowflake(1, 1);

    public AddAllocationFrame(DeviceInfoFrame deviceInfoFrame){
        this.setTitle("添加设备分配信息");
        this.setSize(500, 400);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        deviceInfoFrame.setEnabled(false);
        String editingId = deviceInfoFrame.getId();

        Panel panel = new Panel(new GridLayout(5,2));
        panel.add(new JLabel("设备编号："));
        deviceIdField = new JTextField(10);
        deviceIdField.setText(editingId);
        deviceIdField.setEditable(false);
        panel.add(deviceIdField);


        List<Map<String,Object>> deparList = DBToolSet.selectSQL("select id,department_name from busi_department_info where is_deleted=0"); 
        panel.add(new JLabel("分配部门："));
        departmentComboBox = new JComboBox<>();
        for(Map<String,Object> map:deparList){
            departmentComboBox.addItem(map.get("department_name").toString());
        }
        panel.add(departmentComboBox);


        confirmButton = new JButton("确定");
        confirmButton.addActionListener(e->{
            String departmentString = departmentComboBox.getSelectedItem().toString();
            String departmentId = String.valueOf( DBToolSet.selectSQL("select id from busi_department_info where is_deleted=0 and department_name=?", departmentString).get(0).get("id"));;
            String sql = "insert into busi_device_allocation(id,device_id,department_id,create_date,create_user,is_deleted,status) values(?,?,?,?,?,0,0)";
            ArrayList<Object> params = new ArrayList<>();
            params.add(snowflake.nextId());
            params.add(editingId);
            params.add(departmentId);
            params.add(new Date());
            params.add(deviceInfoFrame.getUserName());
            DBToolSet.updateSQL("update busi_device_info set device_status=1,update_user=?,update_date=? where id=?", deviceInfoFrame.getUserName(),new Date(),editingId);
            DBToolSet.insertSQL(sql, params.toArray());
            JOptionPane.showMessageDialog(AddAllocationFrame.this, "添加成功", "提示", JOptionPane.INFORMATION_MESSAGE);
            deviceInfoFrame.reloadData();
            this.dispose();
        });
        panel.add(confirmButton);
        cancelButton = new JButton("取消");
        cancelButton.addActionListener(e->{
            deviceInfoFrame.setEnabled(true);
            this.dispose();
        });
        panel.add(cancelButton);
        this.add(panel);
        deviceInfoFrame.setEnabled(true);
        deviceInfoFrame.reloadData();


    }
}
