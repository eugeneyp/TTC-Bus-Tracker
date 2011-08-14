package net.egnsky.ttcbustracker.client;

import java.util.List;

import net.egnsky.ttcbustracker.client.nextbusobj.NextBusClient;
import net.egnsky.ttcbustracker.client.nextbusobj.NextBusClientCallback;
import net.egnsky.ttcbustracker.client.nextbusobj.Route;
import net.egnsky.ttcbustracker.client.nextbusobj.Vehicle;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.geom.Size;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class TTCBusTracker implements EntryPoint, NextBusClientCallback{
	private LatLng finchStation;
	private List<Vehicle> vehicleLocations;
	private List<Route> routeList;
	private MapWidget map;
	private ListBox busRoutesListBox;
	private NextBusClient nextBusClient = new NextBusClient(this);

  // GWT module entry point method.
  public void onModuleLoad() {
   /*
    * Asynchronously loads the Maps API.
    *
    * The first parameter should be a valid Maps API Key to deploy this
    * application on a public server, but a blank key will work for an
    * application served from localhost.
   */
   Maps.loadMapsApi("ABQIAAAAr-QycL_IBlgzk3BvmI_rchRu2MPYjHh51xEJwX5upq-1WtQueBRDcAm0t0TkwNOidsCtMUgWZ3FeYg", "2", false, new Runnable() {
      public void run() {
        buildUi();
      }
    });
  }

  private void buildUi() {
    // Open a map centred on Finch station
	finchStation = LatLng.newInstance(43.779729, -79.415454);

    map = new MapWidget(finchStation, 13);
    map.setSize("100%", "100%");
    // Add some controls for the zoom level
    map.addControl(new LargeMapControl());
    
    busRoutesListBox = new ListBox();
    busRoutesListBox.setVisibleItemCount(1); // make it into a drop-down
    busRoutesListBox.addChangeHandler(new ChangeHandler(){
    	public void onChange(ChangeEvent event){
    		int index = busRoutesListBox.getSelectedIndex();
    		nextBusClient.requestVehicleLocations(routeList.get(index));
    	}
    });
    
    final HorizontalPanel headerDock = new HorizontalPanel();
    headerDock.add(busRoutesListBox);

    final DockLayoutPanel dock = new DockLayoutPanel(Unit.EM);
    dock.addNorth(headerDock, 2);
    dock.add(map);

    // Add the map to the HTML host page
    RootLayoutPanel.get().add(dock);
    	 
    nextBusClient.requestRouteList();
  }
 
  
  public void routeListHandler(List<Route> routeList){
	  for (int i = 0; i < routeList.size(); i++){
		  busRoutesListBox.addItem(routeList.get(i).getTitle());
	  }
	  this.routeList = routeList;
  }
  
  public void vehicleLocationsHandler(List<Vehicle> vehicleLocations){
	  if (vehicleLocations.size() == 0){
		  Window.alert("No buses in service");
	  }
	  
	  map.clearOverlays();
	  double avgLat = 0, avgLong = 0; 
	  for (int i = 0; i < vehicleLocations.size(); i++){
		 int heading = vehicleLocations.get(i).getHeading();
		 int approxHeading = ((heading + 5) / 10 * 10) % 360;
		 Icon busIcon = Icon.newInstance(GWT.getModuleBaseURL() + "images/bus_icon" + approxHeading + ".gif");
		 busIcon.setIconSize(Size.newInstance(60, 60));
		 busIcon.setIconAnchor(Point.newInstance(30, 30));
		 busIcon.setInfoWindowAnchor(Point.newInstance(30, 30));
		 
		 MarkerOptions options = MarkerOptions.newInstance();
		 options.setIcon(busIcon);
		 map.addOverlay(new Marker(vehicleLocations.get(i).getLatLng(), options));
		 
		 avgLat += vehicleLocations.get(i).getLatitude();
		 avgLong += vehicleLocations.get(i).getLongitude();
	  }
	  this.vehicleLocations = vehicleLocations;
	  
	  // centre the map, at the centre point of all vehicles
	  if (vehicleLocations.size() != 0){
		  avgLat /= vehicleLocations.size();
		  avgLong /= vehicleLocations.size();
		  map.panTo(LatLng.newInstance(avgLat, avgLong));
	  }
  }
}
