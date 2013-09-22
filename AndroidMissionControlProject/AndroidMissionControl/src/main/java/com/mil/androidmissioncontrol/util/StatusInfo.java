/**StatusInfo.java**********************************************************************************
 *       Author : Joshua Weaver
 *      Created : June 11, 2012
 * Last Revised : September 22, 2013
 *      Purpose : Class object for holding Status information of connected
 *      		  vehicles.  Information is gathered and placed in object by
 *      		  ROS.
 *    Call Path : MainActivity->StatusInfo
 *    		XML :
 * Dependencies :
 **************************************************************************************************/
package com.mil.androidmissioncontrol.util;

public class StatusInfo {
    private String vehicleName;
    private double latitude;
    private double longitude;
    private double heading;
    private double speed;
    private double altitude;
    private double panAngle;
    private double tiltAngle;
    private double batteryStatus;
    private int gpsStatus;
    private int currWaypoint;
    private double currWaypointDistance;
    private String state;

    public StatusInfo() {
        this.vehicleName = "";
        this.latitude = 0;
        this.longitude = 0;
        this.heading = 0;
        this.speed = 0;
        this.altitude = 0;
        this.panAngle = 0;
        this.tiltAngle = 0;
        this.batteryStatus = 0;
        this.gpsStatus = 0;
        this.currWaypoint = 0;
        this.currWaypointDistance = 0;

        this.state = "unknown";
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getPanAngle() {
        return panAngle;
    }

    public void setPanAngle(double panAngle) {
        this.panAngle = panAngle;
    }

    public double getTiltAngle() {
        return tiltAngle;
    }

    public void setTiltAngle(double tiltAngle) {
        this.tiltAngle = tiltAngle;
    }

    public double getBatteryStatus() {
        return batteryStatus;
    }

    public void setBatteryStatus(double batteryStatus) {
        this.batteryStatus = batteryStatus;
    }

    public int getGpsStatus() {
        return gpsStatus;
    }

    public void setGpsStatus(int gpsStatus) {
        this.gpsStatus = gpsStatus;
    }

    public int getCurrWaypoint() {
        return currWaypoint;
    }

    public void setCurrWaypoint(int currWaypoint) {
        this.currWaypoint = currWaypoint;
    }

    public double getCurrWaypointDistance() {
        return currWaypointDistance;
    }

    public void setCurrWaypointDistance(double currWaypointDistance) {
        this.currWaypointDistance = currWaypointDistance;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
