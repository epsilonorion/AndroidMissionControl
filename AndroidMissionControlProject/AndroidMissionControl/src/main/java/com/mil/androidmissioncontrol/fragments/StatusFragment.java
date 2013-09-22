/**StatusFragment.java*******************************************************
 *       Author : Joshua Weaver
 * Last Revised : August 13, 2012
 *      Purpose : Fragment for displaying vehicle information received
 *      		  through ROS.  Currently a container, code complete in other
 *      		  version.  Displayed with ViewPager
 *    Call Path : MainActivity->StatusFragment
 *          XML : res->layout->status_fragment
 * Dependencies : ViewFragmentAdapter, ROSJava, Android-Core
 ****************************************************************************/
package com.mil.androidmissioncontrol.fragments;

import com.mil.androidmissioncontrol.MainApplication;
import com.mil.androidmissioncontrol.R;
import com.mil.androidmissioncontrol.util.StatusInfo;
//import com.mil.congregatorscontrol.util.VehicleStatus;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class StatusFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    //VehicleStatus vehicleStatus = null;

    StatusInfo statusInfo = null;

    TextView VehicleNameStatus;
    TextView LatitudeStatus;
    TextView LongitudeStatus;
    TextView AltitudeStatus;
    TextView HeadingStatus;
    TextView SpeedStatus;
    TextView PanAngleStatus;
    TextView TiltAngleStatus;
    TextView BatteryStatus;
    TextView GPSStatus;
    TextView CurrWaypointStatus;
    TextView CurrWaypointDistance;
    TextView StateStatus;

    Spinner VehicleNameSpinner;
    List<String> vehicleList = new ArrayList<String>();
    private ArrayAdapter<String> spinnerAdapter;

    HashMap<String, StatusInfo> vehicleStatusMap;

    String currentVehicleSelected;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.status_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        MainApplication mainApp = (MainApplication)getActivity().getApplicationContext();
        //vehicleStatus = mainApp.getVehicleStatus();
        vehicleStatusMap = mainApp.getVehicleStatusMap();


        VehicleNameSpinner = (Spinner) getActivity().findViewById(R.id.VehicleNameSpinner);
        spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, vehicleList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        VehicleNameSpinner.setAdapter(spinnerAdapter);
        VehicleNameSpinner.setOnItemSelectedListener(this);

        VehicleNameStatus = (TextView) getActivity().findViewById(
                R.id.lblVehicleNameValue);

        LatitudeStatus = (TextView) getActivity().findViewById(
                R.id.lblLatitudeStatusValue);
        LongitudeStatus = (TextView) getActivity().findViewById(
                R.id.lblLongitudeStatusValue);
        AltitudeStatus = (TextView) getActivity().findViewById(
                R.id.lblAltitudeStatusValue);
        HeadingStatus = (TextView) getActivity().findViewById(
                R.id.lblHeadingStatusValue);
        SpeedStatus = (TextView) getActivity().findViewById(
                R.id.lblSpeedStatusValue);
        PanAngleStatus = (TextView) getActivity().findViewById(
                R.id.lblPanAngleStatusValue);
        TiltAngleStatus = (TextView) getActivity().findViewById(
                R.id.lblTiltAngleStatusValue);
        BatteryStatus = (TextView) getActivity().findViewById(
                R.id.lblBatteryStatusValue);
        GPSStatus = (TextView) getActivity()
                .findViewById(R.id.lblGPStatusValue);
        CurrWaypointStatus = (TextView) getActivity().findViewById(
                R.id.lblCurrWaypointStatusValue);
        CurrWaypointDistance = (TextView) getActivity().findViewById(
                R.id.lblCurrWaypointDistanceValue);
        StateStatus = (TextView) getActivity().findViewById(
                R.id.lblStateValue);

        if (statusInfo != null) {
            //StatusInfo vehicleStatusInfo = vehicleStatus.getVehicleStatus();

            VehicleNameStatus.setText(statusInfo.getVehicleName()
                    + " Status");

            LatitudeStatus.setText("" + statusInfo.getLatitude());
            LongitudeStatus.setText("" + statusInfo.getLongitude());
            AltitudeStatus.setText("" + statusInfo.getAltitude());
            HeadingStatus.setText("" + statusInfo.getHeading());

            SpeedStatus.setText("" + statusInfo.getSpeed());
            PanAngleStatus.setText("" + statusInfo.getPanAngle());
            TiltAngleStatus.setText("" + statusInfo.getTiltAngle());
            BatteryStatus.setText("" + statusInfo.getBatteryStatus());

            GPSStatus.setText("" + statusInfo.getGpsStatus());

            CurrWaypointStatus
                    .setText("" + statusInfo.getCurrWaypoint());
            CurrWaypointDistance
                    .setText("" + statusInfo.getCurrWaypointDistance());

            StateStatus
                    .setText("" + statusInfo.getState());
        }
    }

    /**
     * Function called by UIHandler to update the displayed status information on the fragment.
     * Information to be displayed is selected by the current value on the Vehicle Name Spinner.
     */
    public void updateStatusInfo() {
        String vehicleName = VehicleNameSpinner.getSelectedItem().toString();
        statusInfo = vehicleStatusMap.get(vehicleName);

        if (statusInfo != null) {
            LatitudeStatus.setText("" + statusInfo.getLatitude());
            LongitudeStatus.setText("" + statusInfo.getLongitude());
            AltitudeStatus.setText("" + statusInfo.getAltitude());
            HeadingStatus.setText("" + statusInfo.getHeading());

            SpeedStatus.setText("" + statusInfo.getSpeed());
            PanAngleStatus.setText("" + statusInfo.getPanAngle());
            TiltAngleStatus.setText("" + statusInfo.getTiltAngle());
            BatteryStatus.setText("" + statusInfo.getBatteryStatus());

            GPSStatus.setText("" + statusInfo.getGpsStatus());

            CurrWaypointStatus
                    .setText("" + statusInfo.getCurrWaypoint());
            CurrWaypointDistance
                    .setText("" + statusInfo.getCurrWaypointDistance());

            StateStatus
                    .setText("" + statusInfo.getState());
        }

    }

    public void setStatusInfo(String vehicleName) {
        statusInfo = vehicleStatusMap.get(vehicleName);

        if (statusInfo != null) {
            LatitudeStatus.setText("" + statusInfo.getLatitude());
            LongitudeStatus.setText("" + statusInfo.getLongitude());
            AltitudeStatus.setText("" + statusInfo.getAltitude());
            HeadingStatus.setText("" + statusInfo.getHeading());

            SpeedStatus.setText("" + statusInfo.getSpeed());
            PanAngleStatus.setText("" + statusInfo.getPanAngle());
            TiltAngleStatus.setText("" + statusInfo.getTiltAngle());
            BatteryStatus.setText("" + statusInfo.getBatteryStatus());

            GPSStatus.setText("" + statusInfo.getGpsStatus());

            CurrWaypointStatus
                    .setText("" + statusInfo.getCurrWaypoint());
            CurrWaypointDistance
                    .setText("" + statusInfo.getCurrWaypointDistance());

            StateStatus
                    .setText("" + statusInfo.getState());
        } else {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "NO STATUS INFO", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * Add an item to the List for the Vehicle Name Spinner.
     * @param vehicleName
     */
    public void addItemVehicleNameSpinner(String vehicleName){
        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "ADD ITEM", Toast.LENGTH_SHORT);
        toast.show();

        if (VehicleNameSpinner.isSelected()) {
            toast = Toast.makeText(getActivity().getApplicationContext(), "Selected" + VehicleNameSpinner.getSelectedItem().toString() + " at Index " + VehicleNameSpinner.getSelectedItemPosition(), Toast.LENGTH_SHORT);
            toast.show();
        }

        vehicleList.add(vehicleName);

        Collections.sort(vehicleList);

        spinnerAdapter.notifyDataSetChanged();

        // If the first vehicle being added to the list, save as current selected and set selection
        if (currentVehicleSelected == null) {
            currentVehicleSelected = vehicleName;
        }

        // Make sure that the currentVehicleSelected is always the current selection.  The selection
        // would change due to the sort of the vehicleList.
        VehicleNameSpinner.setSelection(vehicleList.indexOf(currentVehicleSelected), false);

        String test = VehicleNameSpinner.getSelectedItem().toString();
        if (test != null) {
            toast = Toast.makeText(getActivity().getApplicationContext(), "Selected " + test + " at Index " + VehicleNameSpinner.getSelectedItemPosition(), Toast.LENGTH_SHORT);
            toast.show();
        } else {
            toast = Toast.makeText(getActivity().getApplicationContext(), "Nothing Selected", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public Handler UIHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.getData().getInt("VEHICLE_STATUS")) {
                case 1: {
                    updateStatusInfo();
                }
                break;

                case 2: {
                    addItemVehicleNameSpinner(msg.getData().getString("VEHICLE_NAME"));
                }
                break;
            }
        };
    };

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Item Selected", Toast.LENGTH_SHORT);
        toast.show();

        String vehicleName = VehicleNameSpinner.getItemAtPosition(position).toString();
        currentVehicleSelected = vehicleName;

        toast = Toast.makeText(getActivity().getApplicationContext(), vehicleName, Toast.LENGTH_SHORT);
        toast.show();

        setStatusInfo(vehicleName);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
