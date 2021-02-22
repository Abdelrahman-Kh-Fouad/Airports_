package module6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.geo.Location;
import parsing.ParseFeed;
import processing.core.PApplet;

/** An applet that shows airports (and routes)
 * on a world map.  
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 * MOOC team
 *
 */
public class AirportMap extends PApplet {
	
	UnfoldingMap map;
	private List<Marker> airportList;
	List<Marker> routeList;
	
	private CommonMarker lastSelected = null ; 
	
	public void setup() {
		// setting up PAppler
		size(800,600);
		
		// setting up map and default events
		map = new UnfoldingMap(this, 50, 50, 750, 550);
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// get features from airport data
		List<PointFeature> features = ParseFeed.parseAirports(this, "airports.dat");
		
		// list for markers, hashmap for quicker access when matching with routes
		airportList = new ArrayList<Marker>();
		HashMap<Integer, Location> airports = new HashMap<Integer, Location>();
		
		// create markers from features
		for(PointFeature feature : features) {
			AirportMarker m = new AirportMarker(feature);
	
			m.setRadius(5);
			airportList.add(m);
			//System.out.println(feature.getId());
			// put airport in hashmap with OpenFlights unique id for key
			airports.put(Integer.parseInt(feature.getId()), feature.getLocation());
			
		}
		
		
		// parse route data
		List<ShapeFeature> routes = ParseFeed.parseRoutes(this, "routes.dat");
		routeList = new ArrayList<Marker>();
		for(ShapeFeature route : routes) {
			
			// get source and destination airportIds
			int source = Integer.parseInt((String)route.getProperty("source"));
			int dest = Integer.parseInt((String)route.getProperty("destination"));
			
			// get locations for airports on route
			if(airports.containsKey(source) && airports.containsKey(dest)) {
				route.addLocation(airports.get(source));
				route.addLocation(airports.get(dest));
			}
			
			SimpleLinesMarker sl = new SimpleLinesMarker(route.getLocations(), route.getProperties());
		
			//System.out.println(source + " "+dest);
			
			//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
			routeList.add(sl);
			
			
			
				
		}
		
		
		
		hide_route(true);

//		for (Marker marker : routeList)
//		{
//			SimpleLinesMarker mark = (SimpleLinesMarker)marker;
//			System.out.println(mark.getLocations());
//		}
//		for (Marker marker : airportList)
//			System.out.println(marker.get);
		//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
		map.addMarkers(routeList);
		map.addMarkers(airportList);
		
	}
	
	
	@Override
	public void mouseMoved()
	{
		// clear the last selection
		if (lastSelected != null) {
			lastSelected.setSelected(false);
			lastSelected = null;
			hide_airports(false );
			hide_route(true );
		}
		selectMarkerIfHover(airportList);
		
		
	}
	
	// If there is a marker selected 
	private void selectMarkerIfHover(List<Marker> markers)
	{
		if (lastSelected != null) {
			
			return;
		}
		
		for (Marker m : markers) 
		{
			CommonMarker marker = (CommonMarker)m;
			if (marker.isInside(map,  mouseX, mouseY)) {
				lastSelected = marker;
				marker.setSelected(true);
				hide_airports(true);
				hide_route(false );
				return;
			}
		}
	}
	
	
	
	private void hide_airports(boolean flag )
	{
		for (Marker marker : airportList)
		{
			if(!marker.equals(lastSelected))
			{
				marker.setHidden(flag);
			}
		}
	}
	
	private void hide_route(boolean flag )
	{
		if (!flag)
		{
			for(Marker marker :  routeList)
			{
				SimpleLinesMarker mark = (SimpleLinesMarker)marker;
				if (lastSelected!=null)
				{
					if(mark.getLocations().get(0).equals(lastSelected.getLocation()))
						marker.setHidden(flag);
					
				}
			}
		}
		else 
			for(Marker marker :  routeList)
				marker.setHidden(flag);
	}
	public void draw() {
		background(0);
		
		map.draw();
	
		
	}
	

}
