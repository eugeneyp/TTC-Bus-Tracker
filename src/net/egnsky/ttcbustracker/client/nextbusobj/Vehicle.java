package net.egnsky.ttcbustracker.client.nextbusobj;

import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.xml.client.Element;

public class Vehicle {
	private double latitude = 0;
	private double longitude = 0;
	private int heading = 0;
	
	public Vehicle(Element xmlElement){
		latitude = Double.parseDouble(xmlElement.getAttribute("lat"));
		longitude = Double.parseDouble(xmlElement.getAttribute("lon"));
		heading = Integer.parseInt(xmlElement.getAttribute("heading"));
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public int getHeading() {
		return heading;
	}
	
	public LatLng getLatLng() {
		return LatLng.newInstance(latitude, longitude);
	}
}
