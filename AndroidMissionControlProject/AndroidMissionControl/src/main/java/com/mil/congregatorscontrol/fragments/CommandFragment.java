/**CommandFragment.java*******************************************************
 *       Author : Joshua Weaver
 * Last Revised : August 26, 2012
 *      Purpose : Class for controlling the Command Fragment.  Control
 *      		  involves receiving button presses from a user and sending
 *       		  these commands to a connected vehicle through ROS
 *    Call Path : MainActivity->CommandFragment
 * 			XML : res->layout->command_fragment
 * Dependencies : ViewFragmentAdapter, ROSJava, Android-Core
 ****************************************************************************/
package com.mil.congregatorscontrol.fragments;

//import org.ros.address.InetAddressFactory;
//import org.ros.node.DefaultNodeMainExecutor;
//import org.ros.node.NodeConfiguration;
//import org.ros.node.NodeMainExecutor;

import com.mil.congregatorscontrol.MainApplication;
import com.mil.congregatorscontrol.R;
//import com.mil.congregatorscontrol.StatusInfo;
//import com.mil.congregatorscontrol.VehicleStatus;
//import com.mil.congregatorscontrol.WaypointList;
//import com.mil.congregatorscontrol.ros.ROSVehicleBridge;

import android.app.Activity;
import android.app.Fragment;
        import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
        import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class CommandFragment extends Fragment {
    public static final int START_COMMAND = 0;
    public static final int PAUSE_COMMAND = 1;
    public static final int HALT_COMMAND = 2;
    public static final int RTB_COMMAND = 7;
    public static final int LAUNCH_COMMAND = 8;
    public static final int LAND_COMMAND = 9;

//    private VehicleStatus vehicleStatusObject = null;
//    private WaypointList wayptListObject = null;

    boolean ConnectedToVehicle = false;
    boolean isLaunched = false;
    boolean isLaunchCommandSent = false;

//    private NodeMainExecutor nodeMainExecutor;
//    private NodeConfiguration nodeConfiguration;

    private MainApplication mainApp;

    private CommandFragment commandFragInstance;

    // Listener/Callbacks
    CommandFragmentListener mCallback;

    // Container Activity must implement this interface
    public interface CommandFragmentListener {
        public void sendWaypoints();

        public void sendCommand(int cmd);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (CommandFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement CustomMapFragmentListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.command_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        mainApp = (MainApplication) getActivity().getApplicationContext();
//        wayptListObject = mainApp.getWayptList();
//        vehicleStatusObject = mainApp.getVehicleStatus();

        commandFragInstance = this;

        // Capture our button from layout
        Button btnSendWaypts = (Button) getActivity().findViewById(
                R.id.btnSendWaypoints);
        Button btnStartMission = (Button) getActivity().findViewById(
                R.id.btnStartMission);
        Button btnPauseMission = (Button) getActivity().findViewById(
                R.id.btnPauseMission);
        Button btnHaltMission = (Button) getActivity().findViewById(
                R.id.btnHaltMission);
        Button btnReturnToBase = (Button) getActivity().findViewById(
                R.id.btnReturnToBase);
        Button btnLaunch = (Button) getActivity().findViewById(
                R.id.btnLaunchLand);

        btnSendWaypts.setOnClickListener(mAddListener);
        btnStartMission.setOnClickListener(mAddListener);
        btnPauseMission.setOnClickListener(mAddListener);
        btnHaltMission.setOnClickListener(mAddListener);
        btnReturnToBase.setOnClickListener(mAddListener);
        btnLaunch.setOnClickListener(mAddListener);
    }

    private OnClickListener mAddListener = new OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.btnSendWaypoints:
//                    if (mainApp.isConnectedToVehicle()) {
//                        ROSVehicleBridge rosVehicleBridge = mainApp.getROSVehicleBridge();
//
//                        if (rosVehicleBridge == null) {
//                            Toast.makeText(getActivity(), "Failure in Vehicle Connection",
//                                    Toast.LENGTH_SHORT).show();
//
//                            break;
//                        }
//                        rosVehicleBridge.sendWaypoints();
//
                        mCallback.sendWaypoints();

                        Toast.makeText(getActivity(), "Sending Waypoints",
                                Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(getActivity(), "Vehicle Not Connected",
//                                Toast.LENGTH_SHORT).show();
//                    }
                    break;

                case R.id.btnStartMission:
//                    if (mainApp.isConnectedToVehicle()) {
//                        ROSVehicleBridge rosVehicleBridge = mainApp.getROSVehicleBridge();
//
//                        if (rosVehicleBridge == null) {
//                            Toast.makeText(getActivity(), "Failure in Vehicle Connection",
//                                    Toast.LENGTH_SHORT).show();
//
//                            break;
//                        }
//                        rosVehicleBridge.sendCommand(ROSVehicleBridge.START_COMMAND);

                    mCallback.sendCommand(START_COMMAND);

                        Toast.makeText(getActivity(), "Starting Mission",
                                Toast.LENGTH_SHORT).show();
//
//                    } else {
//                        Toast.makeText(getActivity(), "Vehicle Not Connected",
//                                Toast.LENGTH_SHORT).show();
//                    }
                    break;

                case R.id.btnPauseMission:
//                    if (mainApp.isConnectedToVehicle()) {
//                        ROSVehicleBridge rosVehicleBridge = mainApp.getROSVehicleBridge();
//
//                        if (rosVehicleBridge == null) {
//                            Toast.makeText(getActivity(), "Failure in Vehicle Connection",
//                                    Toast.LENGTH_SHORT).show();
//
//                            break;
//                        }
//                        rosVehicleBridge.sendCommand(ROSVehicleBridge.PAUSE_COMMAND);
//
                    mCallback.sendCommand(PAUSE_COMMAND);

                        Toast.makeText(getActivity(), "Pausing Mission",
                                Toast.LENGTH_SHORT).show();
//
//                    } else {
//                        Toast.makeText(getActivity(), "Vehicle Not Connected",
//                                Toast.LENGTH_SHORT).show();
//                    }

                    break;

                case R.id.btnHaltMission:
//                    if (mainApp.isConnectedToVehicle()) {
//                        ROSVehicleBridge rosVehicleBridge = mainApp.getROSVehicleBridge();
//
//                        if (rosVehicleBridge == null) {
//                            Toast.makeText(getActivity(), "Failure in Vehicle Connection",
//                                    Toast.LENGTH_SHORT).show();
//
//                            break;
//                        }
//                        rosVehicleBridge.sendCommand(ROSVehicleBridge.HALT_COMMAND);
//
                    mCallback.sendCommand(HALT_COMMAND);

                        Toast.makeText(getActivity(), "Halting Mission",
                                Toast.LENGTH_SHORT).show();
//
//                    } else {
//                        Toast.makeText(getActivity(), "Vehicle Not Connected",
//                                Toast.LENGTH_SHORT).show();
//                    }

                    break;
                case R.id.btnReturnToBase:
//                    if (mainApp.isConnectedToVehicle()) {
//                        ROSVehicleBridge rosVehicleBridge = mainApp.getROSVehicleBridge();
//
//                        if (rosVehicleBridge == null) {
//                            Toast.makeText(getActivity(), "Failure in Vehicle Connection",
//                                    Toast.LENGTH_SHORT).show();
//
//                            break;
//                        }
//                        rosVehicleBridge.sendCommand(ROSVehicleBridge.RTB_COMMAND);
//
                    mCallback.sendCommand(RTB_COMMAND);

                        Toast.makeText(getActivity(), "Returning to Base",
                                Toast.LENGTH_SHORT).show();
//
//                    } else {
//                        Toast.makeText(getActivity(), "Vehicle Not Connected",
//                                Toast.LENGTH_SHORT).show();
//                    }

                    break;
                case R.id.btnLaunchLand:
//                    if (mainApp.isConnectedToVehicle()) {
//                        if (isLaunched) {
//                            ROSVehicleBridge rosVehicleBridge = mainApp.getROSVehicleBridge();
//
//                            if (rosVehicleBridge == null) {
//                                Toast.makeText(getActivity(), "Failure in Vehicle Connection",
//                                        Toast.LENGTH_SHORT).show();
//
//                                break;
//                            }
//                            rosVehicleBridge.sendCommand(ROSVehicleBridge.LAND_COMMAND);
//
//                            Toast.makeText(getActivity(), "Landing Vehicle",
//                                    Toast.LENGTH_SHORT).show();
//
//                            isLaunchCommandSent = true;
//                        } else {
//                            ROSVehicleBridge rosVehicleBridge = mainApp.getROSVehicleBridge();
//
//                            if (rosVehicleBridge == null) {
//                                Toast.makeText(getActivity(), "Failure in Vehicle Connection",
//                                        Toast.LENGTH_SHORT).show();
//
//                                break;
//                            }
//                            rosVehicleBridge.sendCommand(ROSVehicleBridge.LAUNCH_COMMAND);
//
                    mCallback.sendCommand(LAUNCH_COMMAND);

                            Toast.makeText(getActivity(), "Launching Vehicle",
                                    Toast.LENGTH_SHORT).show();
//
//                            isLaunchCommandSent = true;
//                        }
//                    } else {
//                        Toast.makeText(getActivity(), "Vehicle Not Connected",
//                                Toast.LENGTH_SHORT).show();
//                    }

                    break;
            }

        }
    };

    public Handler UIHandler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.getData().getInt("WAYPOINT_SENT")) {
                case 1: {
                    Toast.makeText(getActivity(), "Failed to send Waypoints",
                            Toast.LENGTH_SHORT).show();

                }
                break;
                case 2: {
                    Toast.makeText(getActivity(), "Waypoints Received",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            }

            switch (msg.getData().getInt("COMMAND_SENT")) {
                case 1: {
                    Toast.makeText(getActivity(), "Failed to send Command",
                            Toast.LENGTH_SHORT).show();
                }
                break;
                case 2: {
                    Toast.makeText(getActivity(), "Command Received",
                            Toast.LENGTH_SHORT).show();

                    if (isLaunchCommandSent) {
                        if (isLaunched) {
                            Button btnLaunchLand = (Button) getActivity().findViewById(R.id.btnLaunchLand);
                            btnLaunchLand.setText("Launch Vehicle");

                            isLaunchCommandSent = false;
                            isLaunched = false;
                        } else {
                            Button btnLaunchLand = (Button) getActivity().findViewById(R.id.btnLaunchLand);
                            btnLaunchLand.setText("Land Vehicle");

                            isLaunchCommandSent = false;
                            isLaunched = true;
                        }
                    }
                }
                break;
            }
        };
    };
}
