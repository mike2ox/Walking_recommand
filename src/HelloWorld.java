import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.opencsv.CSVReader;

import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Feature;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Geometry;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.LineString;
import de.micromata.opengis.kml.v_2_2_0.MultiGeometry;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import pt.karambola.gpx.beans.Gpx;
import pt.karambola.gpx.beans.Track;
import pt.karambola.gpx.beans.TrackPoint;
import pt.karambola.gpx.io.GpxFileIo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class TrackData{
	private String name;
	private ArrayList<Coordinate> coords;
	
	public void SetName(String _name){
		name = _name;
	}
	
	public void SetCoords(ArrayList<Coordinate> _coords){
		coords = _coords;
	}
	
	public String GetName(){
		return name;
	}
	
	public ArrayList<Coordinate> GetCoords(){
		return coords;
	}
}

public class HelloWorld {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub		
		BuildDB();
		//Coordinate coord = GetCoordKML("data/한국관광공사_걷기여행길등록코스GPS정보_20160906/한국관광공사_gpx_20160906/xml/gps1_3.kml");
		//System.out.println(coord.getLongitude() + " : " + coord.getLatitude());
	}
	
	
	public static void BuildDB(){
		//data/한국관광공사_걷기여행길등록코스GPS정보_20160906/한국관광공사_걷기여행길등록코스GPS파일정보_20160906.csv
		try {
			Connection conn= DriverManager.getConnection("DB ADDRESS!!!!!!! /walkingcourse","root","apmsetup");
			java.sql.Statement stmt = conn.createStatement();			
			
			CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream("data/한국관광공사_걷기여행길등록코스GPS정보_20160906/한국관광공사_걷기여행길등록코스GPS파일정보_20160906.csv"),Charset.forName("EUC-KR")));

			String[] s;
			HashMap<String,Integer> hashMap = new HashMap<String,Integer>();
			s=reader.readNext();
			ArrayList<TrackData> trackList = null;
			
			int count =0;
			while((s=reader.readNext())!=null){
				if(s[2].equals(""))	continue;
				String[] splitted = s[2].split("[.]");
				if(splitted[splitted.length-1].equals("kml"))
					trackList = GetCoordKML("data/한국관광공사_걷기여행길등록코스GPS정보_20160906/한국관광공사_gpx_20160906/xml/" + s[2]);
				else if(splitted[splitted.length-1].equals("gpx")){
					trackList = GetCoordGPX("data/한국관광공사_걷기여행길등록코스GPS정보_20160906/한국관광공사_gpx_20160906/xml/" + s[2]);
				}
				else
					continue;
				
				count++;
				
				Iterator<TrackData> i = trackList.iterator();		
				while(i.hasNext()){
					int num =1;
					TrackData coords=i.next();
					if(hashMap.containsKey(s[0])){
						num=hashMap.get(s[0])+1;
					}
					hashMap.put(s[0], num);
					
					String stringPath="";
					Iterator<Coordinate> j = coords.GetCoords().iterator();
					while(j.hasNext()){
						Coordinate co = j.next();
						stringPath += co.getLongitude();
						stringPath += ",";
						stringPath += co.getLatitude();
						stringPath += ",";
						stringPath += co.getAltitude();
						if(j.hasNext())
							stringPath += ",";
					}
					
					int rs = stmt.executeUpdate("insert into courseinfo(RoadName,CourseNum,EntryPoint,Path,CourseDescription,Country1,Country2,Difficulty,Distance,ExactDistance,Description,Duration,Water,Restroom,Snackbar) values('" 
					+s[0]+ "','"	//RoadName 
					+num + "','"	//CourseNum
					+coords.GetCoords().get(0).getLongitude() + "," +coords.GetCoords().get(0).getLatitude()  + "','" 	//EntryPoint
					+stringPath + "','"	//Path
					+s[3]+ "','"
					+s[4]+ "','"
					+s[5]+ "','"
					+s[6]+ "','"
					+s[7]+ "','"
					+s[8]+ "','"
					+s[9]+ "','"
					+s[10]+ "','"
					+s[11]+ "','"
					+s[12]+ "','"
					+s[13]+ "');"
					);
					
					System.out.println(s[0] + "(" + num + "번 코스) "+ coords.GetCoords().get(0).getLongitude()+ " : " +coords.GetCoords().get(0).getLatitude() + " : " + coords.GetCoords().get(0).getAltitude());
				}
			}
			reader.close();
			conn.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	public static ArrayList<TrackData> GetCoordGPX(String path){
		Gpx gpx = GpxFileIo.parseIn(path);
		ArrayList<TrackData> td= new ArrayList<TrackData>();
		ArrayList<Track> tracks = (ArrayList<Track>) gpx.getTracks();
		
		Iterator<Track> i = tracks.iterator();
		while(i.hasNext()){
			ArrayList<TrackPoint> trackPoints =(ArrayList<TrackPoint>) i.next().getTrackPoints();
			TrackData trackData = new TrackData();
			Iterator<TrackPoint> j = trackPoints.iterator();
			
			ArrayList<Coordinate> coordList = new ArrayList<Coordinate>();
			while(j.hasNext()){
				TrackPoint tp = j.next();
				Coordinate coord;
				if(tp.getElevation()!=null){
					coord = new Coordinate(tp.getLongitude(),tp.getLatitude(),tp.getElevation());					
				}else{
					coord = new Coordinate(tp.getLongitude(),tp.getLatitude(),0);
				}
				coordList.add(coord);
			}
			trackData.SetCoords(coordList);
			td.add(trackData);
		} 
		return td;
	}
	
	public static ArrayList<TrackData> GetCoordKML(String path){
		Kml kml = Kml.unmarshal(new File(path));

		ArrayList<TrackData> tracks = new ArrayList<TrackData>();
		
		Feature document;
		List<Feature> features;
		
		if(kml.getFeature() instanceof Document){
			document = kml.getFeature();
			features = ((Document)document).getFeature();
		}
		else if(kml.getFeature() instanceof Placemark){
			features = new ArrayList<Feature>();
			features.add((Placemark) kml.getFeature());
		}
		else if(kml.getFeature() instanceof Folder){
			features = new ArrayList<Feature>();
			features.add(kml.getFeature());
		}
		else{
			features = new ArrayList<Feature>();
		}
		
		while(!features.isEmpty()){
			Feature feature = features.get(0);
			features.remove(0);
			
			if(feature instanceof Folder){	//if feature == folder then
				features.addAll(0, ((Folder)feature).getFeature());	//add all features
			}
			else if(feature instanceof Document){
				document = feature;
				features.addAll(0,((Document)document).getFeature());
			}
			else if(feature instanceof Placemark){
				Geometry geometry = ((Placemark)feature).getGeometry();
				
				LineString lineString;
				
				if(geometry instanceof LineString){
					lineString = (LineString) geometry;
					if(lineString.getCoordinates().size()!=0)
					{
						TrackData td = new TrackData();
						td.SetName(feature.getName());
						td.SetCoords((ArrayList<Coordinate>) lineString.getCoordinates());
						
						tracks.add(td);
						
					}
				}
				else if(geometry instanceof MultiGeometry){
					lineString = (LineString) ((MultiGeometry)geometry).getGeometry().get(0);
					if(lineString.getCoordinates().size()!=0)
					{
						TrackData td = new TrackData();
						td.SetName(feature.getName());
						td.SetCoords((ArrayList<Coordinate>) lineString.getCoordinates());
						tracks.add(td);
					}
					
				}
			}
			else{
				continue;
			}	
		}
		return tracks;
		
	}
}