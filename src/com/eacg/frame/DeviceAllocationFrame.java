package com.eacg.frame;

import javax.swing.JFrame;

import com.eacg.frame.deviceinfoframe.DeviceInfoFrame;

public class DeviceAllocationFrame extends JFrame {
    public DeviceAllocationFrame(DeviceInfoFrame deviceInfoFrame) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setTitle("设备调拨");


    }

}
