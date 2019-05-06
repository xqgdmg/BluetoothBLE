package com.example.bluetoothble.bean;

import java.util.Objects;

/**
 * 蓝牙设备实体
 *
 * @author cWX708605
 * @version [V6.0.0.1, 2019/4/29]
 * @since V6.0.0.1
 */
public class DeviceBean {

    // 蓝牙名称
    private String name;

    // 蓝牙地址
    private String adreess;

    public DeviceBean(String name, String adreess) {
        this.name = name;
        this.adreess = adreess;
    }

    public String getName() {
        return name;
    }

    public String getAdreess() {
        return adreess;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAdreess(String adreess) {
        this.adreess = adreess;
    }

    @Override
    public String toString() {
        return "DeviceBean{" +
                "name='" + name + '\'' +
                ", adreess='" + adreess + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceBean that = (DeviceBean) o;
        return Objects.equals(adreess, that.adreess);
    }

    @Override
    public int hashCode() {
        return Objects.hash(adreess);
    }
}
