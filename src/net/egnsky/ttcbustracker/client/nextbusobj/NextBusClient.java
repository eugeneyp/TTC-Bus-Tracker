package net.egnsky.ttcbustracker.client.nextbusobj;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class NextBusClient {
	
	private final String baseUrl = "http://webservices.nextbus.com/service/publicXMLFeed?";
	private final String agency = "ttc";
	private NextBusClientCallback callback;
	
	public NextBusClient(NextBusClientCallback callback){
		this.callback = callback;
	}
	
	public void requestRouteList(){
		String routeListUrl = baseUrl + "command=routeList" + "&a=" + agency;
	    RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, routeListUrl);  
	    
	    try {
	    	RouteListCallback routeListCallback = new RouteListCallback();
	    	 requestBuilder.sendRequest(null, routeListCallback); 
	    } catch (RequestException ex) {
	    	requestFailed(ex);
	    }
	}
	
	public void requestVehicleLocations(Route route){
		String vehicleLocationUrl = baseUrl + "command=vehicleLocations" + "&a=" + agency + "&r=" + route.getTag() + "&t=0";
	    RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, vehicleLocationUrl);  
	    
	    try {
	    	VehicleLocationCallback vehicleLocationCallback = new VehicleLocationCallback();
	    	 requestBuilder.sendRequest(null, vehicleLocationCallback); 
	    } catch (RequestException ex) {
	    	requestFailed(ex);
	    }
	}
	
	private void requestFailed(Throwable exception) {
		  Window.alert("Failed to send the message: " + exception.getMessage());
	}
	
	private class RouteListCallback implements RequestCallback {		
		
		public RouteListCallback(){
		}
		
		public void onResponseReceived(Request request, Response response) {
			List<Route> routeList = new ArrayList<Route>();
			Document routeListDom = XMLParser.parse(response.getText());
			NodeList routeNodeList = routeListDom.getElementsByTagName("route");
			
			for (int i = 0; i < routeNodeList.getLength(); i++){
				  Element n = (Element)routeNodeList.item(i);
				  routeList.add(new Route(n));
			}
			  
			callback.routeListHandler(routeList);
		}
		
		public void onError(Request request, Throwable exception) {
			 requestFailed(exception);
		}
	}
	
	private class VehicleLocationCallback implements RequestCallback {
		public VehicleLocationCallback(){
			
		}
		
		public void onResponseReceived(Request request, Response response) {
			List<Vehicle> vehicleList = new ArrayList<Vehicle>();
			
			Document vehicleLocationDom = XMLParser.parse(response.getText());
			NodeList vehicleNodeList = vehicleLocationDom.getElementsByTagName("vehicle");
			  
			for (int i = 0; i < vehicleNodeList.getLength(); i++){
				Element n = (Element)vehicleNodeList.item(i);
				vehicleList.add(new Vehicle(n));
			}
			  
			callback.vehicleLocationsHandler(vehicleList);
		}
		
		public void onError(Request request, Throwable exception) {
			requestFailed(exception);
		}
	}
}
