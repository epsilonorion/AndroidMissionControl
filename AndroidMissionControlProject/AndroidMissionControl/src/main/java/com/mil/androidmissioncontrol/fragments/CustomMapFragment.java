/**MapFragment.java*********************************************************************************
 *       Author : Joshua Weaver
 *      Created : June 11, 2012
 * Last Revised : September 22, 2012
 *      Purpose : Fragment for controlling the MapView.  Uses google maps api 2.  Handles current
 *                GPS position, waypoint drawing, track generation, vehicle display, etc.  It also
 *                holds the object for the SlidingDrawer and the subsequent buttons.
 *    Call Path : MainActivity->MapFragment
 *          XML : res->layout->map_fragment
 * Dependencies : SlidingDrawerWrapper, WaypointsOverlay
 **************************************************************************************************/

package com.mil.androidmissioncontrol.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
//import com.google.android.maps.MapController;
//import com.google.android.maps.MapView;
//import com.google.android.maps.Overlay;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mil.androidmissioncontrol.MainApplication;
import com.mil.androidmissioncontrol.R;
import com.mil.androidmissioncontrol.util.SlidingDrawerWrapper;
import com.mil.androidmissioncontrol.util.WaypointInfo;
import com.mil.androidmissioncontrol.util.WaypointList;

import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.Toast;
import android.widget.ToggleButton;


public class CustomMapFragment extends MapFragment implements GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener, OnDrawerOpenListener, OnDrawerCloseListener,
        GoogleMap.OnMarkerDragListener {
    private WaypointList wayptList = null;

    private View mView;
    public static CustomMapFragment instance;

    // MapView Variables
    private GoogleMap map = null;
    private MapView mapView = null;
//    private MapController mc = null;
    boolean satelliteOn = true;

    private static final int DEFAULT_ZOOM = 19;

    private SlidingDrawerWrapper sd;

    private SharedPreferences prefs;

    // UI Variables
    private ToggleButton addButton;
    private ToggleButton deleteButton;
    private ToggleButton moveButton;
    private ToggleButton modifyButton;
    private Button newButton;
    private Button clearButton;

    // Flag Variables
    private Boolean addWayptFlag = false;
    private Boolean deleteWayptFlag = false;
    private Boolean moveWayptFlag = false;
    private Boolean modifyWayptFlag = false;

    private LatLng FLAVET = new LatLng(29.646654,-82.354249);

    Marker dragMarker;

    MainApplication mainApp;

    // Listener/Callbacks
    CustomMapFragmentListener mCallback;

    Marker testMarker;

    /**
     * Interface to pass function calls from fragment back to Container Activity.  Container
     * Activity must implement this interface.
     *
     * Interface handles callbacks for waypoint modification within Map Fragment
      */
    public interface CustomMapFragmentListener {
        public void onWaypointCreated(Marker marker);

        public void onWaypointDeleted(int pos);

        public void onWaypointModified(int pos, WaypointInfo waypt);
    }

    /**
     * Control creation of fragment instance
     * @return
     */
    public static CustomMapFragment newInstance() {
        Log.d("Josh", "newInstance Started");
        CustomMapFragment frag = new CustomMapFragment();
        Bundle args = new Bundle();
        //args.putInt("instanceNumber", countInstance);
        //args.putParcelable("wayptList", wayptList);
        frag.setArguments(args);
        //countInstance++;

        Log.d("Josh", "newInstance Ended");
        return frag;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (CustomMapFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement CustomMapFragmentListener");
        }
    }

    public static CustomMapFragment getInstance() {
        Log.d("Josh", "getInstance Started");
        if (instance == null){
            instance = newInstance();
        }

        Bundle args = new Bundle();

        //args.putInt("instanceNumber", countInstance);
        //args.putParcelable("wayptList", wayptList);

        instance.setArguments(args);
        //countInstance++;

        Log.d("Josh", "getInstance Ended");
        return instance;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);

        mView = inflater.inflate(R.layout.map_fragment, container, false);

        FragmentManager myFragmentManager = getFragmentManager();
        MapFragment CustomMapFragment = (MapFragment)myFragmentManager.findFragmentById(R.id.map);
        map = CustomMapFragment.getMap();

        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        map.moveCamera(CameraUpdateFactory.newLatLng(FLAVET));
        map.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
        map.setMyLocationEnabled(true);
        map.setOnMapClickListener(this);
        map.setOnMarkerClickListener(this);
        map.setOnMarkerDragListener(this);
        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setInfoWindowAdapter(new CustomInfoWindowAdapter());

        mainApp = (MainApplication) getActivity().getApplicationContext();
        wayptList = mainApp.getWayptList();

        // Bundle bundle = getArguments();
        // if (bundle != null) {
        // wayptList = bundle.getParcelable("wayptList");
        // }

        /*mainApp = (MainApplication) getActivity().getApplicationContext();
        // wayptList = mainApp.getWayptList();
        vehicleStatusInfo = mainApp.getVehicleStatus();
        wayptList = mainApp.getWayptList();

        MapFragment mapFragment = this;

        wayptList.setupMapFragment(mapFragment);
        */
        // Grab preferences of the application
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Capture our button from mView layout
        addButton = (ToggleButton) mView.findViewById(R.id.btnAdd);
        deleteButton = (ToggleButton) mView.findViewById(R.id.btnDelete);
        moveButton = (ToggleButton) mView.findViewById(R.id.btnMove);
        modifyButton = (ToggleButton) mView.findViewById(R.id.btnModify);
        newButton = (Button) mView.findViewById(R.id.btnNew);
        clearButton = (Button) mView.findViewById(R.id.btnClear);

        addButton.setOnCheckedChangeListener(mCheckedListener);
        deleteButton.setOnCheckedChangeListener(mCheckedListener);
        moveButton.setOnCheckedChangeListener(mCheckedListener);
        modifyButton.setOnCheckedChangeListener(mCheckedListener);
        newButton.setOnClickListener(mClickListener);
        clearButton.setOnClickListener(mClickListener);

        sd = (SlidingDrawerWrapper) mView.findViewById(R.id.sg_below);
        sd.setOnDrawerOpenListener(this);
        sd.setOnDrawerCloseListener(this);

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * When sliding drawer is closed, reset all toggle buttons to not checked and all flags to
     * false.  Change control icon.
     *
     * TODO The control icon glitches when closed.  This may be due to the slidingdrawer code being
     * TODO phased out
     */
    public void onDrawerClosed() {
        sd.getHandle().setBackgroundResource(R.drawable.icon_up);

        Toast.makeText(getActivity(), "Finished Editing Markers",
                Toast.LENGTH_SHORT).show();

        addButton.setChecked(false);
        deleteButton.setChecked(false);
        moveButton.setChecked(false);
        modifyButton.setChecked(false);

        addWayptFlag = false;
        deleteWayptFlag = false;
        moveWayptFlag = false;
        modifyWayptFlag = false;

    }

    /**
     * When sliding drawer is opened, change control icon.
     */
    public void onDrawerOpened() {
        sd.getHandle().setBackgroundResource(R.drawable.icon_down);

        Toast.makeText(getActivity(), "Editing Markers", Toast.LENGTH_SHORT)
                .show();
    }

    /**
     * Handle on Map clicks.  If the addWayptFlag is true, then create a waypoint where the user
     * clicked the map.
     * @param latLng
     */
    @Override
    public void onMapClick(LatLng latLng) {
        if (addWayptFlag) {

            String label = "";

            if (wayptList.size() == 0) {
                label = "RTB";
            } else {
                label = "Waypoint" + wayptList.size();
            }

            Marker marker = map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(label)
                    .snippet("")
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));

            WaypointInfo waypt = new WaypointInfo(label,
                    latLng.latitude, latLng.longitude,
                    Double.parseDouble(prefs.getString("default_speed",
                            "25")), Double.parseDouble(prefs.getString(
                    "default_altitude", "10")),
                    Double.parseDouble(prefs.getString(
                            "default_hold_time", "10")),
                    Double.parseDouble(prefs.getString(
                            "default_yaw_from", "0")),
                    Double.parseDouble(prefs.getString(
                            "default_position_accuracy", "0")),
                    Double.parseDouble(prefs.getString(
                            "default_pan_position", "100")),
                    Double.parseDouble(prefs.getString(
                            "default_tilt_position", "100")));

            wayptList.put(marker, waypt);

            Log.d("MAPDEBUG", "lat = " + waypt.getLatitude() + ", Lon = " +  waypt.getLongitude());
            Log.d("MAPDEBUG", "Size of list is " + wayptList.size());

            mCallback.onWaypointCreated(marker);
        }
    }

    // ---when a menu item is selected---
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // return MenuChoice(item);
        // ---obtain an instance of the activity---
        switch (item.getItemId()) {

            case R.id.SatelliteViewItem:
                Toast.makeText(getActivity(), "Satellite View turned on",
                        Toast.LENGTH_SHORT).show();
                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                satelliteOn = true;
                // }

                return true;

            case R.id.MapViewItem:
                Toast.makeText(getActivity(), "Map View turned on",
                        Toast.LENGTH_SHORT).show();
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                satelliteOn = false;

                return true;

            case R.id.mylocation:
                Location myLoc = map.getMyLocation();

                if (myLoc != null) {
                    map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(myLoc.getLatitude(),
                            myLoc.getLongitude())));
                } else {
                    Toast.makeText(getActivity(),
                            "Current Position is not detected", Toast.LENGTH_LONG)
                            .show();
                }
                return true;

            case R.id.vehiclelocation:
                /*if (mainApp.isConnectedToVehicle() || mainApp.isInPlayback()) {

                    StatusInfo statusInfo = vehicleStatusInfo.getVehicleStatus();

                    GeoPoint vehLoc = new GeoPoint(
                            (int) (statusInfo.getLatitude() * 1E6),
                            (int) (statusInfo.getLongitude() * 1E6));
                    mc.animateTo(vehLoc);
                } else {
                    Toast.makeText(getActivity(), "Vehicle position unknown.",
                            Toast.LENGTH_LONG).show();
                }*/
                return true;
        }
        return false;
    }

    /**
     * Handle controls per each button on sliding drawer.  Mostly, reset boxes checked state as
     * needed as wel and toggling control flags.  Used for add, delete, move, and modify toggle
     * buttons
     */
    private OnCheckedChangeListener mCheckedListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            if (buttonView == addButton) {

                if (isChecked) {
                    deleteButton.setChecked(false);
                    moveButton.setChecked(false);
                    modifyButton.setChecked(false);

                    addWayptFlag = true;
                    deleteWayptFlag = false;
                    moveWayptFlag = false;
                    modifyWayptFlag = false;
                } else {
                    addWayptFlag = false;
                    deleteWayptFlag = false;
                    moveWayptFlag = false;
                    modifyWayptFlag = false;
                }

            }

            if (buttonView == deleteButton) {

                if (isChecked) {
                    addButton.setChecked(false);
                    moveButton.setChecked(false);
                    modifyButton.setChecked(false);

                    addWayptFlag = false;
                    deleteWayptFlag = true;
                    moveWayptFlag = false;
                    modifyWayptFlag = false;
                } else {
                    addWayptFlag = false;
                    deleteWayptFlag = false;
                    moveWayptFlag = false;
                    modifyWayptFlag = false;
                }
            }

            if (buttonView == moveButton) {
                if (isChecked) {
                    addButton.setChecked(false);
                    deleteButton.setChecked(false);
                    modifyButton.setChecked(false);

                    addWayptFlag = false;
                    deleteWayptFlag = false;
                    moveWayptFlag = true;
                    modifyWayptFlag = false;
                } else {
                    addWayptFlag = false;
                    deleteWayptFlag = false;
                    moveWayptFlag = false;
                    modifyWayptFlag = false;
                }

            }

            if (buttonView == modifyButton) {
                if (isChecked) {
                    addButton.setChecked(false);
                    deleteButton.setChecked(false);
                    moveButton.setChecked(false);

                    addWayptFlag = false;
                    deleteWayptFlag = false;
                    moveWayptFlag = false;
                    modifyWayptFlag = true;
                } else {
                    addWayptFlag = false;
                    deleteWayptFlag = false;
                    moveWayptFlag = false;
                    modifyWayptFlag = false;
                }
            }

        }

    };

    /**
     * Handle button clicks for clear and new commands.
     */
    private OnClickListener mClickListener = new OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.btnClear:
                    map.clear();
                    break;

                case R.id.btnNew:
                    OpenNewEntryDialog();
                    break;

            }
        }
    };

    /**
     * Handle clicks that occur on markers.  If the deleteWayptFlag is true, delete the marker
     * clicked.  If the modifyWayptFlag is true, setup to modify that waypoints data.  Otherwise,
     * just show the info window for that waypoint.
     * @param marker
     * @return
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d("Josh", "onInfo");

        if (deleteWayptFlag) {
            Log.d("Josh", "Marker Clicked Lat= " + marker.getPosition().latitude + ", Lon= " + marker.getPosition().longitude);
            // Callback to Main Activity for marker to be removed, passing to WaypointListFragment
            int pos = wayptList.getPosition(marker);
            mCallback.onWaypointDeleted(pos);

            testMarker = marker;
            testMarker.hideInfoWindow();

            // Remove marker from hashmap and from google map
            marker.hideInfoWindow();
            wayptList.remove(marker);
            marker.remove();
        } else if (modifyWayptFlag) {
            // Call Dialog to modify information of current marker.  Call functions to modify the
            // marker from inside the dialog
            OpenModifyEntryDialog(marker);
        } else {
            marker.showInfoWindow();
        }

        return true;
    }

    /**
     * Create a dialog box to enter waypoint data manually.  Data is pre-populated with default
     * waypoint information that is given through the preferences menu.  Once data is complete, a
     * new waypoint should be created given the desired information.
     */
    private void OpenNewEntryDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setTitle("New Waypoint Info");

        // Set an EditText view to get user input
        LayoutInflater layoutInflater = (LayoutInflater) getActivity()
                .getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater
                .inflate(R.layout.waypoint_entry_dialog, null);

        alert.setView(view);

        // Populate all text boxes with default values
        // Use Current Position as default Lat/Long coordinates
        double myLocLat = 0.0;
        double myLocLong = 0.0;

//        LatLng myLoc = map.getMyLocation();
        LatLng myLoc = FLAVET;

        final double defaultLatitude = myLocLat;
        final double defaultLongitude = myLocLong;

        // Use preference values as default values for other components
        final double defaultSpeed = Double.parseDouble(prefs.getString(
                "default_speed", ""));
        final double defaultAltitude = Double.parseDouble(prefs.getString(
                "default_altitude", ""));
        final double defaultHoldTime = Double.parseDouble(prefs.getString(
                "default_hold_time", ""));
        final double defaultPanAngle = Double.parseDouble(prefs.getString(
                "default_pan_position", ""));
        final double defaultTiltAngle = Double.parseDouble(prefs.getString(
                "default_tilt_position", ""));
        final double defaultHeading = Double.parseDouble(prefs.getString(
                "default_yaw_from", ""));
        final double defaultPosAcc = Double.parseDouble(prefs.getString(
                "default_position_accuracy", ""));

        final EditText wayptNameTxt = (EditText) view
                .findViewById(R.id.txtWaypointName);
        final EditText latitudeTxt = (EditText) view
                .findViewById(R.id.txtLatitude);
        final EditText longitudeTxt = (EditText) view
                .findViewById(R.id.txtLongitude);
        final EditText speedToTxt = (EditText) view
                .findViewById(R.id.txtSpeedTo);
        final EditText holdTimeTxt = (EditText) view
                .findViewById(R.id.txtHoldTime);
        final EditText altitudeTxt = (EditText) view
                .findViewById(R.id.txtAltitude);
        final EditText headingTxt = (EditText) view
                .findViewById(R.id.txtDesiredHeading);
        final EditText panAngleTxt = (EditText) view
                .findViewById(R.id.txtPanAngle);
        final EditText tiltAngleTxt = (EditText) view
                .findViewById(R.id.txtTiltAngle);
        final EditText posAccTxt = (EditText) view
                .findViewById(R.id.txtPosAccuracy);

        wayptNameTxt.setText("Waypoint");
        latitudeTxt.setText("" + defaultLatitude);
        longitudeTxt.setText("" + defaultLongitude);
        speedToTxt.setText("" + defaultSpeed);
        holdTimeTxt.setText("" + defaultHoldTime);
        altitudeTxt.setText("" + defaultAltitude);
        headingTxt.setText("" + defaultHeading);
        panAngleTxt.setText("" + defaultPanAngle);
        tiltAngleTxt.setText("" + defaultTiltAngle);
        posAccTxt.setText("" + defaultPosAcc);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String wayptNameStr = wayptNameTxt.getText().toString();
                String latitudeStr = latitudeTxt.getText().toString();
                String longitudeStr = longitudeTxt.getText().toString();
                String speedToStr = speedToTxt.getText().toString();
                String holdTimeStr = holdTimeTxt.getText().toString();
                String altitudeStr = altitudeTxt.getText().toString();
                String headingStr = headingTxt.getText().toString();
                String panAngleStr = panAngleTxt.getText().toString();
                String tiltAngleStr = tiltAngleTxt.getText().toString();
                String posAccStr = posAccTxt.getText().toString();

                double latitude, longitude, speedTo, holdTime, altitude, heading;
                double panAngle, tiltAngle, posAcc;

                try {
                    latitude = Double.parseDouble(latitudeStr);
                } catch (final NumberFormatException e) {
                    latitude = 0.0;
                }

                try {
                    longitude = Double.parseDouble(longitudeStr);
                } catch (final NumberFormatException e) {
                    longitude = 0.0;
                }

                try {
                    speedTo = Double.parseDouble(speedToStr);
                } catch (final NumberFormatException e) {
                    speedTo = defaultSpeed;
                }

                try {
                    holdTime = Double.parseDouble(holdTimeStr);
                } catch (final NumberFormatException e) {
                    holdTime = defaultHoldTime;
                }

                try {
                    altitude = Double.parseDouble(altitudeStr);
                } catch (final NumberFormatException e) {
                    altitude = defaultAltitude;
                }

                try {
                    heading = Double.parseDouble(headingStr);
                } catch (final NumberFormatException e) {
                    heading = defaultHeading;
                }

                try {
                    panAngle = Double.parseDouble(panAngleStr);
                } catch (final NumberFormatException e) {
                    panAngle = defaultPanAngle;
                }

                try {
                    tiltAngle = Double.parseDouble(tiltAngleStr);
                } catch (final NumberFormatException e) {
                    tiltAngle = defaultTiltAngle;
                }

                try {
                    posAcc = Double.parseDouble(posAccStr);
                } catch (final NumberFormatException e) {
                    posAcc = defaultPosAcc;
                }

                WaypointInfo waypt = new WaypointInfo(wayptNameStr, latitude,
                        longitude, speedTo, altitude, holdTime, panAngle,
                        tiltAngle, heading, posAcc);

                // Add waypoint to waypoint list object
                //wayptList.add(waypt);
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

    /**
     * Create a dialog box to enter waypoint data manually.  Data is pre-populated with default
     * waypoint information for the current marker pass in.  Once data is entered, the current
     * marker should be modified with the new data.
     * @param oldMarker
     */
    private void OpenModifyEntryDialog(final Marker oldMarker) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setTitle("Modify Waypoint Info");

        // Set an EditText view to get user input
        LayoutInflater layoutInflater = (LayoutInflater) getActivity()
                .getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater
                .inflate(R.layout.waypoint_entry_dialog, null);

        alert.setView(view);

        // Use tapped waypoint as default values
        WaypointInfo modifiedMarker = wayptList.getWaypoint(oldMarker);

        final String defaultName = modifiedMarker.getName();
        final double defaultLatitude = modifiedMarker.getLatitude();
        final double defaultLongitude = modifiedMarker.getLongitude();
        final double defaultSpeed = modifiedMarker.getSpeedTo();
        final double defaultAltitude = modifiedMarker.getAltitude();
        final double defaultHoldTime = modifiedMarker.getHoldTime();
        final double defaultPanAngle = modifiedMarker.getPanAngle();
        final double defaultTiltAngle = modifiedMarker.getTiltAngle();
        final double defaultHeading = modifiedMarker.getYawFrom();
        final double defaultPosAcc = modifiedMarker.getPosAcc();

        final EditText wayptNameTxt = (EditText) view
                .findViewById(R.id.txtWaypointName);
        final EditText latitudeTxt = (EditText) view
                .findViewById(R.id.txtLatitude);
        final EditText longitudeTxt = (EditText) view
                .findViewById(R.id.txtLongitude);
        final EditText speedToTxt = (EditText) view
                .findViewById(R.id.txtSpeedTo);
        final EditText holdTimeTxt = (EditText) view
                .findViewById(R.id.txtHoldTime);
        final EditText altitudeTxt = (EditText) view
                .findViewById(R.id.txtAltitude);
        final EditText headingTxt = (EditText) view
                .findViewById(R.id.txtDesiredHeading);
        final EditText panAngleTxt = (EditText) view
                .findViewById(R.id.txtPanAngle);
        final EditText tiltAngleTxt = (EditText) view
                .findViewById(R.id.txtTiltAngle);
        final EditText posAccTxt = (EditText) view
                .findViewById(R.id.txtPosAccuracy);

        wayptNameTxt.setText(defaultName);
        latitudeTxt.setText("" + defaultLatitude);
        longitudeTxt.setText("" + defaultLongitude);
        speedToTxt.setText("" + defaultSpeed);
        holdTimeTxt.setText("" + defaultHoldTime);
        altitudeTxt.setText("" + defaultAltitude);
        headingTxt.setText("" + defaultHeading);
        panAngleTxt.setText("" + defaultPanAngle);
        tiltAngleTxt.setText("" + defaultTiltAngle);
        posAccTxt.setText("" + defaultPosAcc);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String wayptNameStr = wayptNameTxt.getText().toString();
                String latitudeStr = latitudeTxt.getText().toString();
                String longitudeStr = longitudeTxt.getText().toString();
                String speedToStr = speedToTxt.getText().toString();
                String holdTimeStr = holdTimeTxt.getText().toString();
                String altitudeStr = altitudeTxt.getText().toString();
                String headingStr = headingTxt.getText().toString();
                String panAngleStr = panAngleTxt.getText().toString();
                String tiltAngleStr = tiltAngleTxt.getText().toString();
                String posAccStr = posAccTxt.getText().toString();

                double latitude, longitude, speedTo, holdTime, altitude, heading;
                double panAngle, tiltAngle, posAcc;

                try {
                    latitude = Double.parseDouble(latitudeStr);
                } catch (final NumberFormatException e) {
                    latitude = defaultLatitude;
                }

                try {
                    longitude = Double.parseDouble(longitudeStr);
                } catch (final NumberFormatException e) {
                    longitude = defaultLongitude;
                }

                try {
                    speedTo = Double.parseDouble(speedToStr);
                } catch (final NumberFormatException e) {
                    speedTo = defaultSpeed;
                }

                try {
                    holdTime = Double.parseDouble(holdTimeStr);
                } catch (final NumberFormatException e) {
                    holdTime = defaultHoldTime;
                }

                try {
                    altitude = Double.parseDouble(altitudeStr);
                } catch (final NumberFormatException e) {
                    altitude = defaultAltitude;
                }

                try {
                    heading = Double.parseDouble(headingStr);
                } catch (final NumberFormatException e) {
                    heading = defaultHeading;
                }

                try {
                    panAngle = Double.parseDouble(panAngleStr);
                } catch (final NumberFormatException e) {
                    panAngle = defaultPanAngle;
                }

                try {
                    tiltAngle = Double.parseDouble(tiltAngleStr);
                } catch (final NumberFormatException e) {
                    tiltAngle = defaultTiltAngle;
                }

                try {
                    posAcc = Double.parseDouble(posAccStr);
                } catch (final NumberFormatException e) {
                    posAcc = defaultPosAcc;
                }

                WaypointInfo waypt = new WaypointInfo(wayptNameStr, latitude,
                        longitude, speedTo, altitude, holdTime, panAngle,
                        tiltAngle, heading, posAcc);

                modifyMarker(oldMarker, waypt);

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

    /**
     * Modify an old marker with new waypoint information.  This should modify the local wayptList
     * of markers as well as the Waypoint List Fragment's list of markers.
     * @param oldMarker
     * @param waypt
     */
    public void modifyMarker(Marker oldMarker, WaypointInfo waypt) {
        Log.d("Josh", "Modify Marker");

        // Create marker with new information
        Marker newMarker = map.addMarker(new MarkerOptions()
                .position(new LatLng(waypt.getLatitude(), waypt.getLongitude()))
                .title(waypt.getName())
                .snippet("")
                .draggable(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));

        // Modify oldMarker on the hashmap handle the callback with new marker
        wayptList.modify(oldMarker, newMarker, waypt);

        int pos = wayptList.getPosition(oldMarker);

        // Callback to Main Activity for marker to be removed, passing to WaypointListFragment
        mCallback.onWaypointModified(pos, waypt);

        // Now remove the old marker given that ever
        oldMarker.remove();
    }

    /**
     * Grab a copy of the original markers location before it was dragged
     * @param marker
     */
    @Override
    public void onMarkerDragStart(Marker marker) {
        dragMarker = marker;
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    /**
     * Once a marker has finished being dragged, the new markers information should be updated in
     * the wayptList as well as the information on Waypoint List Fragment should be updated for the
     * specific waypoint.
     * @param marker
     */
    @Override
    public void onMarkerDragEnd(Marker marker) {
        WaypointInfo modifiedMarker = wayptList.getWaypoint(dragMarker);

        LatLng markerLatLng = marker.getPosition();
        modifiedMarker.setLatitude(markerLatLng.latitude);
        modifiedMarker.setLongitude(markerLatLng.longitude);

        // Modify oldMarker on the hashmap handle the callback with new marker
        wayptList.modify(dragMarker, marker, modifiedMarker);

        int pos = wayptList.getPosition(dragMarker);

        // Callback to Main Activity for marker to be removed, passing to WaypointListFragment
        mCallback.onWaypointModified(pos, modifiedMarker);
    }

    /**
     * Code to setup a custom info window for on marker clicks
     * TODO NEEDS COMPLETED
     */
    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        @Override
        public View getInfoWindow(Marker marker) {


            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            if ((testMarker == null)) {
                Log.d("MAPDEBUG", "Info sent me here1");
            } else {
                Log.d("MAPDEBUG", "Info sent me here2");

                marker.hideInfoWindow();
            }

            return null;
        }
    }
}
