package net.egnsky.ttcbustracker.client.nextbusobj;

import com.google.gwt.xml.client.Element;

public class Route {
	private String tag;
	private String title;
	
	public Route(Element xmlElement){
		tag = xmlElement.getAttribute("tag");
		title = xmlElement.getAttribute("title");
	}
	
	public String getTag(){
		return tag;
	}
	
	public String getTitle(){
		return title;
	}

}
