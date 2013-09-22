/**WaypointList.java********************************************************************************
 *       Author : Joshua Weaver
 *      Created : June 11, 2012
 * Last Revised : Sept 22, 2013
 *      Purpose : Class object for collection of waypoint items into Array
 *      		  List.
 *    Call Path : MainActivity->WaypointList
 *    		XML :
 * Dependencies : WaypointInfo
 **************************************************************************************************/
package com.mil.androidmissioncontrol.util;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WaypointList {
    Map<Marker, WaypointInfo> markerInfoMap = new HashMap<Marker, WaypointInfo>();
    List<Marker> markerPosList = new ArrayList<Marker>();

    /**
     * Place a marker and waypoint info set on the marker map while also setting the marker on the
     * marker list to hold accurate order of markers.
     * @param marker
     * @param waypt
     */
    public void put(Marker marker, WaypointInfo waypt) {
        // Put marker and waypoint set on map.  Put marker on ordered list
        markerInfoMap.put(marker, waypt);
        markerPosList.add(marker);
    }

    /**
     * Clear all Markers
     */
    public void clear() {
        // Clear marker map and list
        markerInfoMap.clear();
        markerPosList.clear();
    }

    /**
     * Remove a given marker from the marker map and the marker list
     * @param marker
     */
    public void remove(Marker marker) {
        // Get position of marker in marker list
        int pos = markerPosList.indexOf(marker);

        // Remove marker and waypoint set from map.  Remove marker from ordered list
        markerInfoMap.remove(marker);
        markerPosList.remove(pos);
    }

    /**
     * Remove the marker at a given position from both the marker map and marker list
     * @param pos
     */
    public void remove(int pos) {
        // Get marker from marker list at given position
        Marker marker = markerPosList.get(pos);

        // Remove marker and waypoint set from map.  Remove marker from ordered list
        markerInfoMap.remove(marker);
        markerPosList.remove(pos);
    }

    /**
     * Modify a given marker when there is only new Waypoint Information to be changed.
     * @param marker
     * @param waypt
     */
    public void modify(Marker marker, WaypointInfo waypt) {
        // Modify a given marker
        markerInfoMap.put(marker, waypt);
    }

    /**
     * Modify a marker given position when there is only new Waypoint Information to be changed.
     * @param pos
     * @param waypt
     */
    public void modify(int pos, WaypointInfo waypt) {
        // Get marker from marker list at given position
        Marker marker = markerPosList.get(pos);

        // Modify a given marker
        markerInfoMap.put(marker, waypt);
    }

    /**
     * Modify a given marker with a new marker and updated Waypoint Information.  This occurs when
     * both marker and waypoint information have been updated.
     * @param oldMarker
     * @param newMarker
     * @param waypt
     */
    public void modify(Marker oldMarker, Marker newMarker, WaypointInfo waypt) {
        // Get position of marker in marker list
        int pos = markerPosList.indexOf(oldMarker);

        // Remove the changed marker and place the new one on the marker map
        markerInfoMap.remove(oldMarker);
        markerInfoMap.put(newMarker, waypt);

        // Place the new marker data at the position in the marker list
        markerPosList.set(pos, newMarker);
    }

    /**
     * Modify a marker given position with a new marker and updated Waypoint Information.  This
     * occurs when both marker and waypoint information have been updated.
     * @param pos
     * @param newMarker
     * @param waypt
     */
    public void modify(int pos, Marker newMarker, WaypointInfo waypt) {
        // Get marker from marker list at given position
        Marker oldMarker = markerPosList.get(pos);

        // Remove the changed marker and place the new one on the marker map
        markerInfoMap.remove(oldMarker);
        markerInfoMap.put(newMarker, waypt);

        // Place the new marker data at the position in the marker list
        markerPosList.set(pos, newMarker);
    }


    /**
     * Get maker at a given position
     * @param pos
     * @return
     */
    public Marker getMarker(int pos) {
        return markerPosList.get(pos);
    }


    /**
     * Get Waypoint info of marker at a given position
     * @param pos
     * @return
     */
    public WaypointInfo getWaypoint(int pos) {
        Marker marker = markerPosList.get(pos);

        return markerInfoMap.get(marker);
    }


    /**
     * Get Waypoint info of a marker
     * @param marker
     * @return
     */
    public WaypointInfo getWaypoint(Marker marker) {
        return markerInfoMap.get(marker);
    }


    /**
     * Get position of a specific marker
     * @param marker
     * @return
     */
    public int getPosition(Marker marker) {
        return markerPosList.indexOf(marker);
    }


    /**
     * Get the number of markers within the waypoint list
     * @return
     */
    public int size() {
        return markerPosList.size();
    }
}
