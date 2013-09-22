/**MainActivity.java********************************************************************************
 *       Author : Joshua Weaver
 *      Created : June 11, 2012
 * Last Revised : September 22, 2013
 *      Purpose : MainActivity for application.  Starts PreferenceManager,
 *      		  creates WaypointList, creates Fragments, creates ViewPager,
 *      		  starts ROS.
 *    Call Path : BASE
 *          XML : res->layout->main
 * Dependencies : ViewPagerIndicator<L>, ROSJava, Android-Core
 **************************************************************************************************/
package com.mil.congregatorscontrol;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;

import com.mil.congregatorscontrol.fragments.CommandFragment;
import com.mil.congregatorscontrol.fragments.StatusFragment;
import com.mil.congregatorscontrol.fragments.ViewFragmentAdapter;
import com.mil.congregatorscontrol.fragments.CustomMapFragment;
import com.mil.congregatorscontrol.preferences.PreferencesMenu;
import com.mil.congregatorscontrol.fragments.WaypointListFragment;
import com.mil.congregatorscontrol.util.StatusInfo;
import com.mil.congregatorscontrol.util.WaypointInfo;
import com.mil.congregatorscontrol.util.WaypointList;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.Uart;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;


public class MainActivity extends IOIOActivity implements CustomMapFragment.CustomMapFragmentListener,
        CommandFragment.CommandFragmentListener {
    private CustomMapFragment mapFrag;
    private CommandFragment commandFrag;
    private StatusFragment statusFrag;

    HashMap<String, StatusInfo> vehicleStatusMap;

    ViewFragmentAdapter mAdapter;
    ViewPager mPager;
    PageIndicator mIndicator;

    MainApplication mainApp;
    private WaypointList wayptList;

    private AnimationDrawable connectAnimation;
    private ImageView connectImage;
    Menu menu;
    MenuItem testItem;

    WaypointListFragment wayptListFrag;

    List<String> vehicles = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Set screen to always stay on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Load default preference debugLEDFlags if not performed before
        PreferenceManager.setDefaultValues(this, R.xml.general_preferences, true);
        PreferenceManager.setDefaultValues(this, R.xml.ros_preferences, true);
        PreferenceManager.setDefaultValues(this, R.xml.vehicle_preferences, true);

        // Grab preferences of the application
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Handle Theme Setup based on preference debugLEDFlag.
        //TODO
//        if (prefs.getString("theme_list", null).compareTo("Black Theme") == 0) {
//            setTheme(R.style.AppThemeBlack);
//        } else if (prefs.getString("theme_list", null).compareTo("White Theme") == 0) {
//            setTheme(R.style.AppThemeLight);
//        }

        // Start Waypoint List
        wayptList = new WaypointList();
        vehicleStatusMap = new HashMap<String, StatusInfo>();

        // Pass Variables to MainApplication class (GLOBAL TO ENTIRE PROJECT)
        mainApp = (MainApplication) getApplicationContext();
        mainApp.setWayptList(wayptList);
        mainApp.setVehicleStatusMap(vehicleStatusMap);

        // Setup Fragment Control for Application
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Start Map Fragment
        mapFrag = CustomMapFragment.newInstance();

        transaction.replace(R.id.mapFragment, mapFrag);
        transaction.commit();

        // Add ViewPager and Related Fragments. Start each one
        wayptListFrag = new WaypointListFragment();
        statusFrag = new StatusFragment();
        commandFrag = new CommandFragment();

        List<Fragment> fragments = new Vector<Fragment>();
        fragments.add(commandFrag);
        fragments.add(wayptListFrag);
        fragments.add(statusFrag);

//        mainApp.setStatusFrag(statusFrag);

        mAdapter = new ViewFragmentAdapter(fragmentManager, fragments);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setOffscreenPageLimit(4);
        mPager.setAdapter(mAdapter);

        mIndicator = (TitlePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
    }


    /**
     * Handle inflation of Menu when menu button pressed.  Also handle display of vehicle_connect
     * button
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        View v = (View) menu.findItem(R.id.vehicle_connect).getActionView();

        MenuItem connectItem = menu.findItem(R.id.vehicle_connect);
        testItem = connectItem;

        // LayoutInflater inflater = (LayoutInflater)
        // getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // View v = inflater.inflate(R.layout.connection_layout, null);
        // connectImage = (ImageView) v.findViewById(R.id.connectImage);
        connectImage = (ImageView) v.findViewById(R.id.connectImage);

        if (mainApp.isConnectedToVehicle()) {
            connectImage.setImageResource(R.drawable.icon_connected);
        } else {
            connectImage.setImageResource(R.drawable.icon_disconnected);
        }
        connectImage.setBackgroundResource(R.drawable.connection_animation);
        connectAnimation = (AnimationDrawable) connectImage.getBackground();

        connectImage.setOnClickListener(mClickListener);
        // connectItem.setActionView(connectImage);

        this.menu = menu;


        return true;
    }

    /**
     * Handle Menu Item Presses.
     * TODO Most functionality has not been finished from transfer from old application
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Bundle args;

        switch (item.getItemId()) {
            case R.id.menu_settings:
                startActivity(new Intent(this, PreferencesMenu.class));

                return (true);
            case R.id.load_mission:
//                args = new Bundle();
//                args.putString("startDirectory", MISSION_DIRECTORY);
//                args.putString("dialogType", "open");
//                args.putString("fileType", MISSION_FILE_EXTENSION);
//                args.putString("title", "Choose mission file to open");
//
//                FileChooserDialog openFileDialog = FileChooserDialog.newInstance(
//                        this, args);
//                openFileDialog.show(getFragmentManager(), "openFileDialogFragment");

                return (true);
            case R.id.save_mission:
//                if (wayptList.size() == 0) { // No waypoints to save
//                    Toast.makeText(this, "There is no mission to save!",
//                            Toast.LENGTH_SHORT).show();
//                } else {
//                    args = new Bundle();
//                    args.putString("startDirectory", MISSION_DIRECTORY);
//                    args.putString("dialogType", "save");
//                    args.putString("fileType", MISSION_FILE_EXTENSION);
//                    args.putString("title",
//                            "Choose folder and name for mission file to save");
//
//                    FileChooserDialog saveFileDialog = FileChooserDialog
//                            .newInstance(this, args);
//                    saveFileDialog.show(getFragmentManager(),
//                            "saveFileDialogFragment");
//                }
                return (true);
            case R.id.playback_mission:
//                mainApp.setInPlayback(true);
//
//                args = new Bundle();
//                args.putString("startDirectory", PLAYBACK_DIRECTORY);
//                args.putString("dialogType", "open");
//                args.putString("fileType", LOG_FILE_EXTENSION);
//                args.putString("title", "Choose log playback file to open");
//
//                FileChooserDialog playbackFileDialog = FileChooserDialog
//                        .newInstance(this, args);
//                playbackFileDialog.show(getFragmentManager(),
//                        "playbackFileDialogFragment");

                return (true);
            case R.id.swap_map_video:
//                Log.d("Test", "Swapping");
//                FragmentManager fm = getFragmentManager();
//                FragmentTransaction ft = fm.beginTransaction();
//
//                Fragment f1 = fm.findFragmentById(R.id.mapFragment);
//                Fragment f2 = fm.findFragmentById(R.id.mediaFragment);
//
//                ft.remove(f1);
//                ft.remove(f2);
//                ft.commit();
//                fm.executePendingTransactions();
//
//                ft = fm.beginTransaction();
//                // ft.replace(R.id.mapFragment, videoFrag);
//                ft.add(R.id.mediaFragment, f1);
//                ft.add(R.id.mapFragment, f2);
//                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//
//                ft.commit();

                return (true);
            case R.id.vehicle_connect:

                return (true);
        }

        return (super.onOptionsItemSelected(item));
    }

    /**
     *  Called each time the connectImage button on the menu bar is clicked.  Handles the process of
     *  playback control or vehicle connections
     */
    private View.OnClickListener mClickListener = new OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
//              TODO Currently Media Fragment is removed, fix below section when added back
//                case R.id.btnPlaybackPlay:
//                    if (playbackMissionTask != null) {
//                        if (playbackMissionTask.isRunning()) {
//                            if (playbackMissionTask.getIsPaused()) {
//                                playbackMissionTask.setIsPaused(false);
//
//                                ImageButton btn = (ImageButton) v
//                                        .findViewById(R.id.btnPlaybackPlay);
//                                btn.setImageResource(R.drawable.icon_pause);
//                            } else {
//                                playbackMissionTask.setIsPaused(true);
//
//                                ImageButton btn = (ImageButton) v
//                                        .findViewById(R.id.btnPlaybackPlay);
//                                btn.setImageResource(R.drawable.icon_play);
//                            }
//                        }
//                    }
//
//                    break;
//
//                case R.id.btnPlaybackStop:
//                    if (playbackMissionTask != null) {
//                        if (playbackMissionTask.isRunning()) {
//                            playbackMissionTask.exitPlayback();
//                        }
//                    }
//                    break;
//
//                case R.id.btnPlaybackFaster:
//                    if (playbackMissionTask != null) {
//                        if (playbackMissionTask.isRunning()) {
//                            playbackMissionTask.incSpeed();
//
//                            TextView lblMultiplier = (TextView) findViewById(R.id.lblMultiplier);
//
//                            lblMultiplier.setText("x"
//                                    + playbackMissionTask.getSpeed());
//                        }
//                    }
//                    break;
//
//                case R.id.btnPlaybackSlower:
//                    if (playbackMissionTask != null) {
//                        if (playbackMissionTask.isRunning()) {
//                            playbackMissionTask.decSpeed();
//
//                            TextView lblMultiplier = (TextView) findViewById(R.id.lblMultiplier);
//
//                            lblMultiplier.setText("x"
//                                    + playbackMissionTask.getSpeed());
//                        }
//                    }
//                    break;


                // TODO setup this menu item to connect to individual vehicles using ROS or XBee system
                case R.id.connectImage:
                    if (!mainApp.isConnectedToVehicle()) {
                        connectImage.setImageDrawable(null);
                        connectImage
                                .setBackgroundResource(R.drawable.connection_animation);
                        connectAnimation = (AnimationDrawable) connectImage
                                .getBackground();

                        testItem.setActionView(connectImage);
                        connectAnimation.start();

                        // Handle ROS Connect
                        // TODO ALL ROS components have been removed for the moment, add section
                        // TODO back once ROS is added back
                        // Grab preferences of the application
//                        SharedPreferences prefs = PreferenceManager
//                                .getDefaultSharedPreferences(MainActivity.this);
//
//                        // Configure ROS Node connection with host ip and port
//                        // addresses from preferences menu
//                        // Grab nodeMainExecutor and nodeConfiguration from global
//                        // set.
//                        mainApp = (MainApplication) getApplicationContext();
//                        nodeMainExecutor = mainApp.getNodeMainExecutor();
//                        nodeConfiguration = mainApp.getNodeConfiguration();
//
//                        nodeConfiguration = NodeConfiguration
//                                .newPublic(InetAddressFactory.newNonLoopback()
//                                        .getHostAddress());
//
//                        String hostMaster = prefs.getString("ros_IP", "");
//                        Integer port = Integer.parseInt(prefs.getString("ros_port",
//                                ""));
//                        URI uri = URI.create("http://" + hostMaster + ":" + port);
//
//                        Log.d("Test", "Master URI is " + uri);
//                        nodeConfiguration.setMasterUri(uri);
//                        nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
//
//                        // Set global variables to be used elsewhere.
//                        mainApp.setNodeMainExecutor(nodeMainExecutor);
//                        mainApp.setNodeConfiguration(nodeConfiguration);
//
//                        // Create nodes
//                        ROSVehicleBridge rosVehBridge = new ROSVehicleBridge(
//                                vehicleStatus, wayptList, statusFrag, commandFrag,
//                                getApplicationContext(), MainActivity.this);
//
//                        // Execute Nodes
//                        nodeMainExecutor.execute(rosVehBridge, nodeConfiguration);
//
//                        mainApp.setNodeMainExecutor(nodeMainExecutor);

                        // Give message and change button context
                        Toast.makeText(MainActivity.this, "Connecting to Vehicle",
                                Toast.LENGTH_LONG).show();

//                        mainApp.setROSVehicleBridge(rosVehBridge);
                    } else {
                        connectImage.setImageDrawable(null);
                        connectImage
                                .setBackgroundResource(R.drawable.connection_animation);
                        connectAnimation = (AnimationDrawable) connectImage
                                .getBackground();

                        testItem.setActionView(connectImage);
                        connectAnimation.start();

                        // Shutdown Node
                        // TODO Move to Async Task to remove UI Stop when
                        // Disconnecting.
                        mainApp = (MainApplication) getApplicationContext();
//                        nodeMainExecutor = mainApp.getNodeMainExecutor();
//                        nodeMainExecutor.shutdown();
//
//                        mainApp.setROSVehicleBridge(null);

                        // Give message and change button context
                        Toast.makeText(MainActivity.this,
                                "Disconnecting from  Vehicle", Toast.LENGTH_LONG)
                                .show();
                    }
                    break;

            }
        }
    };

    /**
     * Part of CustomMapFragmentListener.  Adds waypoints to Waypoint List Fragment each time one is
     * created in Map Fragment
     * @param marker
     */
    @Override
    public void onWaypointCreated(Marker marker) {
        //wayptListFrag.addWaypoint(markerMap.get(marker));
        wayptListFrag.addWaypoint(wayptList.getWaypoint(marker));
    }

    /**
     * Part of CustomMapFragmentListener.  Remove waypoint from Waypoint List Fragment each time one
     * is deleted in Map Fragment
     * @param pos
     */
    @Override
    public void onWaypointDeleted(int pos) {

        wayptListFrag.removeWaypoint(pos);
    }

    /**
     * Part of CustomMapFragmentListener.  Modify specific waypoint in Waypoint List Fragment each
     * time one is modified in Map Fragment
     * @param pos
     * @param waypt
     */
    @Override
    public void onWaypointModified(int pos, WaypointInfo waypt) {
        wayptListFrag.modifyWaypoint(pos, waypt);
    }

    /**
     * Dialog for handling connection to individual vehicle
     * TODO Needs to be completed
     */
    private void OpenDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Vehicle Connect");

        // Set an EditText view to get user input
        LayoutInflater layoutInflater = (LayoutInflater) this
                .getSystemService(this.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater
                .inflate(R.layout.vehicle_connection_dialog, null);

        alert.setView(view);



        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

        alert.show();
    }

    /***********************************************************************************************
     * IOIO Code section follows.  This code handles communication with an external XBee and is part
     * of the IOIOActivity component of the application.  The code will not function unless an IOIO
     * board is connected with the XBee Rx and Tx pins at pin 3 and 4 respectively.  XBee must be
     * set to 57600 8N1.
     **********************************************************************************************/

    /**
     * Function to send the current wayptList through IOIO Board
     * TODO Setup to send to specific vehicle
     */
    @Override
    public void sendWaypoints() {
        WaypointInfo waypt;
        int wayptListSize = wayptList.size();

        String wayptMsg = "WAY" + Integer.toString(wayptListSize) + ",";

        if (wayptListSize > 0) {
            // Create a string of waypoint items from waypoint 0 to 1 less than the number of points
            for (int i = 0; i < (wayptListSize - 1); i++) {
                waypt = wayptList.getWaypoint(i);

                wayptMsg = wayptMsg + Integer.toString((int) (waypt.getLatitude() * 1e7)) + "," +
                        Integer.toString((int) (waypt.getLongitude() * 1e7)) + "," +
                        Integer.toString((int) waypt.getSpeedTo()) + "," +
                        Integer.toString((int) waypt.getAltitude()) + "," +
                        Integer.toString((int) waypt.getHoldTime()) + "," +
                        Integer.toString((int) (waypt.getYawFrom() /* * 1000*/)) + "," +
                        Integer.toString((int) waypt.getPosAcc()) + "," +
                        Integer.toString((int) (waypt.getPanAngle() /* * 1000*/)) + "," +
                        Integer.toString((int) (waypt.getTiltAngle() /* * 1000*/)) + ",";
            }

            // Add final waypoint to list.  It is done this way to remove the final comma at the end of
            // the string
            waypt = wayptList.getWaypoint(wayptListSize - 1);

            wayptMsg = wayptMsg + Integer.toString((int) (waypt.getLatitude() * 1e7)) + "," +
                    Integer.toString((int) (waypt.getLongitude() * 1e7)) + "," +
                    Integer.toString((int) waypt.getSpeedTo()) + "," +
                    Integer.toString((int) waypt.getAltitude()) + "," +
                    Integer.toString((int) waypt.getHoldTime()) + "," +
                    Integer.toString((int) (waypt.getYawFrom() /* * 1000*/)) + "," +
                    Integer.toString((int) waypt.getPosAcc()) + "," +
                    Integer.toString((int) (waypt.getPanAngle() /* * 1000*/)) + "," +
                    Integer.toString((int) (waypt.getTiltAngle() /* * 1000*/));

            // Send Waypoint Message String
            try {
                ioioLooper.sendMsg(wayptMsg);
            } catch (ConnectionLostException e) {
                Toast.makeText(getApplicationContext(), "Could Not Send Message via IOIO XBee",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "No Waypoints To Send",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Function to send specific command through IOIO board
     * TODO Setup to send to specific vehicle
     * @param cmd
     */
    @Override
    public void sendCommand(int cmd) {
        String cmdMsg = "CMD" + Integer.toString(cmd);

        // Send Command Message String
        try {
            ioioLooper.sendMsg(cmdMsg);
        } catch (ConnectionLostException e) {
            Toast.makeText(getApplicationContext(), "Could Not Send Message via IOIO XBee",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This is the thread on which all the IOIO activity happens. It will be run
     * every time the application is resumed and aborted when it is paused. The
     * method setup() will be called right after a connection with the IOIO has
     * been established (which might happen several times!). Then, loop() will
     * be called repetitively until the IOIO gets disconnected.
     */
    class Looper extends BaseIOIOLooper {
        private DigitalOutput debugLED;
        private Uart uart;
        private InputStream in;
        private OutputStream out;

        private int bufferSize = 1000;

        private Boolean uartBusy = false;
        private Boolean debugLEDFlag = false;

        private int IDSIZE = 4;     // Number of characters for the ID of the comms message
        private int TAGSIZE = 3;    // Number of characters for the TAG of the comms message

        /**
         * Called every time a connection with IOIO has been established.
         * Creates a connection to the UART on pins 3 and 4, while also creating variables for
         * input stream, output stream, and debug LED.
         *
         * @throws ConnectionLostException
         *             When IOIO connection is lost.
         *
         * @see ioio.lib.util.BaseIOIOLooper
         */
        @Override
        protected void setup() throws ConnectionLostException {
            try {
                // Connect to UART on IOIO
                uart = ioio_.openUart(3, 4, 57600, Uart.Parity.NONE, Uart.StopBits.ONE);

                // Setup Input and Output Streams
                in = uart.getInputStream();
                out = uart.getOutputStream();

                // Create handle to debug LED pin
                debugLED = ioio_.openDigitalOutput(0, true);

                toast("IOIO XBee Connected");
            } catch (ConnectionLostException e) {
                toast("Could Not Connect to IOIO XBee");
            }
        }

        /**
         * Called repetitively while the IOIO is connected.
         *
         * @throws ConnectionLostException
         *             When IOIO connection is lost.
         *
         * @see ioio.lib.util.BaseIOIOLooper
         */
        @Override
        public void loop() throws ConnectionLostException {
            // Write debug led while also toggling debugLEDFlag
            debugLED.write(debugLEDFlag);
            debugLEDFlag = !debugLEDFlag;

            // If UART is not busy, get message from UART
            if (!uartBusy) {
                uartBusy = true;
                getMsg();
            }

            // Sleep for 10ms
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }

        /**
         * Handle what happens when the IOIO is disconnected.  Specifically, disconnect the UART.
         */
        @Override
        public void disconnected() {
            // TODO Causes failure when going to preferences menu.
            //uart.close();
        }

        /**
         * Grab the current Message on the UART.  The message is passed to parseMsg to be split into
         * data for the application
         * @throws ConnectionLostException
         */
        private void getMsg() throws ConnectionLostException {

            try {
                // Check if data is available
                int availableBytes = in.available();

                // Parse available data
                if (availableBytes > 0) {
                    byte[] readBuffer = new byte[bufferSize];
                    in.read(readBuffer, 0, availableBytes);
                    char[] charstring= (new String(readBuffer, 0, availableBytes)).toCharArray();
                    String msg = new String(charstring);

                    // Send Message to parseMsg function to be parsed
                    parseMsg(msg);
                }
            } catch (IOException e) {
                toast("Unable to Receive Message");
            }

            // UART is complete, so reset uartBusy Flag
            uartBusy = false;
        }

        /**
         * Send message through UART.  Called from Application functions, mostly from the
         * CommandFragment.
         * @param msg
         *
         * @throws ConnectionLostException
         */
        public void sendMsg(String msg) throws ConnectionLostException {
            // If uart variable is not null, write message
            if (uart != null) {
                uartBusy = true;

                try {
                    out.write(msg.getBytes());

                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    toast("Message Write was Interrupted");
                } catch (IOException e) {
                    toast("Unable to Send Message");
                }

                uartBusy = false;
            } else {
                toast("IOIO is not connected");
            }
        }

        /**
         * Message Parsing.  Messages will be in the following format:
         * [TOID][FROMID][TAG][Data];[TAG][DATA]....
         * NOTE: There are no brackets.  They are only used in the example to show separation.
         * TAGs may be: GPS, CUR, POS, CAM, STA, CMD, WAY, FAL, RAK, ACK, NAK
         * Data is specific to a tag and is separated by commas
         * @param msg
         */
        public void parseMsg(String msg) {
            toast(msg);
            String toString = msg.substring(0,IDSIZE);
            String fromString = msg.substring(IDSIZE,IDSIZE+IDSIZE); // AgentID
//            toast("To String: " + toString);
//            toast("From String: " + fromString);


            // Parse Messages with the TO ID of all devices or this tablet ID.
            if ((toString.equals("ALLD")) || toString.equals("TAB1")) {

                // Parse Messages with FROM ID for AG and AA vehicles (Expected "AGxx" or "AAxx")
                if ((fromString.contains("AG")) || (fromString.contains("AA"))){
                    // Get StatusInfo object for vehicle
                    StatusInfo vehicleStatus = vehicleStatusMap.get(fromString);

//                    toast("Agent ID: " + fromString);

                    // If a vehicleStatus Object has not been created for the current AgentID, it is
                    // a new ID and must be given a vehicleStatus Object.
                    if (vehicleStatus == null) {
                        vehicleStatus = new StatusInfo();

                        // Set vehicle name in vehicleStatus object
                        vehicleStatus.setVehicleName(fromString);

                        // Call function to update StatusFrag with new vehicle info
                        addVehicleStatusFrag(fromString);
                    }

                    // Split and Process all data messages.
                    // Messages split by a semicolon describe a separate data type
                    // Messages split by a comma describe data pertaining to a specific type (TAG)
                    String dataString = msg.substring(2*IDSIZE); // Starts after both IDS are read
//                    toast("Data String: " + dataString);

                    String[] tagStrings = dataString.split(";");

                    // Process all tagStrings (DATA). Split into tag and data.  Search for specific
                    // Tags and process the specific data for that tag.
                    for(String tagString : tagStrings) {
                        String tag = tagString.substring(0,TAGSIZE);
                        String[] data = tagString.substring(TAGSIZE).split(",");

                        // Parse Specific TAG messages and their specific data set
                        if ((tag.compareTo("GPS")) == 0) {
                            vehicleStatus.setLatitude(Double.parseDouble(data[0]));
                            vehicleStatus.setLongitude(Double.parseDouble(data[1]));
                            vehicleStatus.setGpsStatus(Integer.parseInt(data[2]));
                        } else if ((tag.compareTo("POS")) == 0) {
                            vehicleStatus.setAltitude(Double.parseDouble(data[0]));
                            vehicleStatus.setHeading(Double.parseDouble(data[1]));
                            vehicleStatus.setSpeed(Integer.parseInt(data[2]));
                        } else if ((tag.compareTo("CUR")) == 0) {
                            vehicleStatus.setCurrWaypoint(Integer.parseInt(data[0]));
                            vehicleStatus.setCurrWaypointDistance(Double.parseDouble(data[1]));
                        } else if ((tag.compareTo("STA")) == 0) {
                            vehicleStatus.setBatteryStatus(Integer.parseInt(data[0]));
                            vehicleStatus.setState(data[1]);
                        } else if ((tag.compareTo("CAM")) == 0) {
                            vehicleStatus.setPanAngle(Double.parseDouble(data[0]));
                            vehicleStatus.setTiltAngle(Double.parseDouble(data[1]));
                        }
                    }

                    //TODO Finsih adding hashmap to keep track of each vehicle
                    // Put updated vehicleStatus object on vehicleStatusMap for specific vehicle
                    vehicleStatusMap.put(fromString, vehicleStatus);

                    // Call function to update the StatusFragment
                    updateStatusFrag();
                }
            }
        }

        /**
         * Function to connect to statusFrag UIHandler and send command to update the fragment.
         */
        private void updateStatusFrag() {
            if (statusFrag != null) {
                Handler uiHandler = statusFrag.UIHandler;
                Bundle b = new Bundle();
                b.putInt("VEHICLE_STATUS", 1);      // Command to cause fragment ui update
                Message msgValue = Message.obtain(uiHandler);
                msgValue.setData(b);
                msgValue.sendToTarget();
            } else {
                toast("No Status Frag");
            }
        }

        /**
         * Function to connect to statusFrag UIHandler and send new message string representing
         * vehicle.
         * @param msg
         */
        private void addVehicleStatusFrag(final String msg) {
            if (statusFrag != null) {
                Handler uiHandler = statusFrag.UIHandler;

                Bundle b = new Bundle();
                b.putInt("VEHICLE_STATUS", 2);      // Command to trigger string update of spinner
                b.putString("VEHICLE_NAME", msg);   // vehicle name to be passed
                Message msgValue = Message.obtain(uiHandler);
                msgValue.setData(b);
                msgValue.sendToTarget();
            } else {
                toast("No Status Frag");
            }
        }
    }

    /**
     * A method to create IOIO thread and connect the global variable to the Looper
     *
     * @see ioio.lib.util.BaseIOIOLooper
     */
    @Override
    protected IOIOLooper createIOIOLooper() {
        ioioLooper = new Looper();

        return ioioLooper;
    }
    Looper ioioLooper;

    /**
     * Function to run on UIThread and trigger message to be Toasted.  Useful for IOIOLooper
     * @param msg
     */
    private void toast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
