package net.egnsky.ttcbustracker.client.nextbusobj;

import java.util.List;

public interface NextBusClientCallback {
	
	public void routeListHandler(List<Route> routeList);
	
	public void vehicleLocationsHandler(List<Vehicle> vehicleLocations);

}
