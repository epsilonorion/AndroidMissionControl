/**WaypointListFragment.java**************************************************
 *       Author : Joshua Weaver
 * Last Revised : August 13, 2012
 *      Purpose : Class for controlling the WaypointList Fragment.  Control
 *      		  involves using a ListFragment to display items of the
 *      		  WaypointList.
 *    Call Path : MainActivity->WaypointListFragment
 *    		XML :
 * Dependencies : ViewFragmentAdapter
 ****************************************************************************/

package com.mil.androidmissioncontrol.fragments;

import java.util.ArrayList;

import com.mil.androidmissioncontrol.MainApplication;
import com.mil.androidmissioncontrol.R;
import com.mil.androidmissioncontrol.util.WaypointInfo;
import com.mil.androidmissioncontrol.util.WaypointList;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class WaypointListFragment extends ListFragment {
    ArrayList<String> list = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    // private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST;

    WaypointList wayptList = null;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, list);
        setListAdapter(adapter);

        MainApplication mainApp = (MainApplication)getActivity().getApplicationContext();
        wayptList=mainApp.getWayptList();


        // Old Method of Grabbing fragment
//		WaypointListFragment wayptListFragment = (WaypointListFragment) getFragmentManager()
//				.findFragmentById(R.id.waypointListFragment);

        // TODO: Fix Numbering of fragment pages
        // Grab WaypointlistFragment from ViewPager
        WaypointListFragment wayptListFragment = (WaypointListFragment) getFragmentManager()
                .findFragmentByTag("android:switcher:" + R.id.pager + ":1");

        //wayptList.setupWayptListFragment(wayptListFragment);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        registerForContextMenu(getListView());
    }

    public void onListItemClick(ListView parent, View v, int position, long id)
    {
        // Obtain reference to MapFragment and use gotoLocation function
//        MapFragment mapFragment = (MapFragment) getFragmentManager()
//                .findFragmentById(R.id.mapFragment);
//        mapFragment.gotoLocation(list.get(position));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.waypoint_list_fragment, container,
                false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_ID:

                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                        .getMenuInfo();
                list.remove(0);

                // Obtain reference to MapFragment and use gotoLocation function
//                MapFragment mapFragment = (MapFragment) getFragmentManager()
//                        .findFragmentById(R.id.mapFragment);
//                mapFragment.removeWaypoint(info.position);

                adapter.notifyDataSetChanged();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    public void addWaypoint(WaypointInfo waypt) {
        String latlng = String.valueOf(waypt.getLatitude()) + ","
                + String.valueOf(waypt.getLongitude());
        addItem(latlng);
    }

    public void removeWaypoint(int wayptPos) {
        removeItem(wayptPos);
    }

    public void modifyWaypoint(int wayptPos, WaypointInfo waypt) {
        String latlng = String.valueOf(waypt.getLatitude()) + ","
                + String.valueOf(waypt.getLongitude());
        modifyItem(wayptPos, latlng);
    }

    public void clearWaypoints() {
        int count = list.size();

        for (int i = 0; i < count; i++) {
            removeItem(0);
        }
    }

    public void addItem(String str) {
        list.add(str);
        adapter.notifyDataSetChanged();
    }

    public void removeItem(int index) {
        list.remove(index);

        adapter.notifyDataSetChanged();
    }

    public void modifyItem(int index, String str) {
        list.remove(index);
        list.add(index, str);

        adapter.notifyDataSetChanged();
    }
}
