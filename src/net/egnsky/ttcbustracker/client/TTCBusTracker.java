package net.egnsky.ttcbustracker.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
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
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class TTCBusTracker implements EntryPoint {
	LatLng finchStation;
	List<LatLng> vehicleLocations;
	MapWidget map;

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
	//finchStation = LatLng.newInstance(39.509, -98.434);

    map = new MapWidget(finchStation, 13);
    //final MapWidget map = new MapWidget(toronto, 13);
    map.setSize("100%", "100%");
    // Add some controls for the zoom level
    map.addControl(new LargeMapControl());

    // Add an info window to highlight a point of interest
    //map.getInfoWindow().open(map.getCenter(),
        //new InfoWindowContent("World's Largest Ball of Sisal Twine"));

    final DockLayoutPanel dock = new DockLayoutPanel(Unit.PX);
    dock.addNorth(map, 1000);

    // Add the map to the HTML host page
    RootLayoutPanel.get().add(dock);
    
    String vehicleLocationUrl = "http://webservices.nextbus.com/service/publicXMLFeed?command=vehicleLocations&a=ttc&r=53&t=0";
    RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, vehicleLocationUrl);  
    
    try {
    	 requestBuilder.sendRequest(null, new RequestCallback() {
    		 public void onError(Request request, Throwable exception) {
    			 requestFailed(exception);
    		 }
    		 public void onResponseReceived(Request request, Response response) {
    			 vehicleLocations = parseVehicleLocations(response.getText());
    			 Icon busIcon = Icon.newInstance(GWT.getModuleBaseURL() + "images/bus_icon.gif");
    			 //busIcon.setIconSize(Size.newInstance(12, 20));
    			 busIcon.setIconSize(Size.newInstance(80, 80));
    			 //busIcon.setShadowSize(Size.newInstance(22, 20));
    			 busIcon.setIconAnchor(Point.newInstance(40, 40));
    			 busIcon.setInfoWindowAnchor(Point.newInstance(40, 40));
    			 
    			 MarkerOptions options = MarkerOptions.newInstance();
    			 options.setIcon(busIcon);
    			 for (int i = 0; i < vehicleLocations.size(); i++){
    			    	map.addOverlay(new Marker(vehicleLocations.get(i), options));
    			 }
    		 }
    		 });
    } catch (RequestException ex) {
    		  requestFailed(ex);
    }

    
    
    	 
  }
  
  private void requestFailed(Throwable exception) {
	  Window.alert("Failed to send the message: " + exception.getMessage());
  }
  
  private List<LatLng> parseVehicleLocations(String xmlText) {	
	  List<LatLng> vehicleList = new ArrayList<LatLng>();
	  Document vehicleLocationDom = XMLParser.parse(xmlText);
	  NodeList vehicleNodeList = vehicleLocationDom.getElementsByTagName("vehicle");
	  
	  for (int i = 0; i < vehicleNodeList.getLength(); i++){
		  Element n = (Element)vehicleNodeList.item(i);
		  double latitude = Double.parseDouble(n.getAttribute("lat"));
		  double longitude = Double.parseDouble(n.getAttribute("lon"));
		  vehicleList.add(LatLng.newInstance(latitude, longitude));
	  }
	  
	  return vehicleList;
  }
}
