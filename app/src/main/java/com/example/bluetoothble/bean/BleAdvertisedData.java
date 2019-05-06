package com.example.bluetoothble.bean;

import java.util.List;
import java.util.UUID;

/**
 * TODO
 *
 * @author cWX708605
 * @version [V6.0.0.1, 2019/5/6]
 * @since V6.0.0.1
 */
public class BleAdvertisedData {
    private List<UUID> mUuids;
    private String mName;

    public BleAdvertisedData(List<UUID> uuids, String name) {
        mUuids = uuids;
        mName = name;
    }

    public List<UUID> getUuids() {
        return mUuids;
    }

    public String getName() {
        return mName;
    }
}
