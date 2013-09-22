package com.mil.androidmissioncontrol;

import android.app.Application;

import com.mil.androidmissioncontrol.util.StatusInfo;
import com.mil.androidmissioncontrol.util.WaypointList;

import java.util.HashMap;

public class MainApplication extends Application{

    private WaypointList wayptList;
    private StatusInfo statusInfo;
    private boolean ConnectedToVehicle = false;
    HashMap<String, StatusInfo> vehicleStatusMap;

    public HashMap<String, StatusInfo> getVehicleStatusMap() {
        return vehicleStatusMap;
    }

    public void setVehicleStatusMap(HashMap<String, StatusInfo> vehicleStatusMap) {
        this.vehicleStatusMap = vehicleStatusMap;
    }

    public boolean isConnectedToVehicle() {
        return ConnectedToVehicle;
    }

    public void setConnectedToVehicle(boolean connectedToVehicle) {
        ConnectedToVehicle = connectedToVehicle;
    }

    public WaypointList getWayptList() {
        return wayptList;
    }

    public void setWayptList(WaypointList wayptList) {
        this.wayptList = wayptList;
    }

    public StatusInfo getStatusInfo() {
        return statusInfo;
    }

    public void setStatusInfo(StatusInfo statusInfo) {
        this.statusInfo = statusInfo;
    }
}
