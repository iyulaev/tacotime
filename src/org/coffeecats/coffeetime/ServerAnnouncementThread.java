package org.coffeecats.coffeetime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Set;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import org.coffeecats.coffeetime.gamelogic.GameInfo;
import org.coffeecats.coffeetime.utility.AnnouncementDatabase;
import org.coffeecats.coffeetime.utility.ServerAnnouncement;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class ServerAnnouncementThread extends Thread {
	private String announcementURL;
	private final String activitynametag = "ServerAnnouncementThread";
	
	AnnouncementDatabase announcementDatabase;
	
	private boolean url_is_valid = true;
	
	public ServerAnnouncementThread(Context caller) {
		announcementDatabase = new AnnouncementDatabase(caller);
		announcementURL = (String) caller.getResources().getString(R.string.announcement_url);
		
		if(announcementURL.equals("nourlspecified")) url_is_valid = false;
	}
	
	//This method lifted from http://stackoverflow.com/questions/4308554/simplest-way-to-read-json-from-a-url-in-java
	/** This method returns the String representation that can be extracted from the Reader 'rd'
	 * 
	 * @param rd Reader to extract String from
	 * @return String extracted from rd.
	 * @throws IOException
	 */
	private static String readAll(Reader rd) throws IOException {
	    StringBuilder sb = new StringBuilder();
	    int cp;
	    while ((cp = rd.read()) != -1) {
	      sb.append((char) cp);
	    }
	    return sb.toString();
	}
	
	/** This method will return the JSON-formatted string residing at URL "urlString"
	 * @param urlString String representing URL to fetch from
	 * @return String located at the URL represented by urlString
	 */
	private static String jsonStringFromURLString(String urlString) {
		URL targetURL;
		try { targetURL = new URL(urlString); } 
		catch (MalformedURLException e) { System.err.println(e); return(null);	}
		
		InputStream is;
		
		try { 
			HttpURLConnection huc = (HttpURLConnection)targetURL.openConnection();
			huc.setConnectTimeout(15000);
			huc.setReadTimeout(15000);
			huc.connect(); 
			is = huc.getInputStream(); 
		} catch (IOException e) { System.err.println(e); return(null); }
		
		try {
		  BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
		  String jsonText = readAll(rd);
		  is.close();
		  return(jsonText);
		} catch (Exception e) { System.err.println(e); return(null); } 		
	}

	/** When run, the SA Thread will attempt to fetch a server annoucement as the URL pointed to 
	 * by the String annoucementURL. It will check the ID of the URL against
	 * what is saved in the database and set the new_server_announcement flag in GameInfo if 
	 * the annoucement is new (hasn't been saved into the database yet).
	 */
	public void run() {	
		//Don't run if there isn't a valid URL
		if(!url_is_valid) {
			Log.w(activitynametag, "No valid announcement_url specified in string resources. Check your strings.xml file. Ignore if you are not running the public build.");
			return;
		}
		
		String jsonString = jsonStringFromURLString(announcementURL);
		
		if(jsonString == null) {
			Log.e(activitynametag, "ServerAnnouncementThread couldn't get jsonString (jSFUS() returned null).");
			return;
		}
		
		//Convert the JSON formatted string to JSONArray type
		JSONArray jsonObjectArray = null;
		try { jsonObjectArray = new JSONArray(jsonString); }
		catch (JSONException e) { System.err.println(e); }
		
		if(jsonObjectArray == null) {
			Log.e(activitynametag, "ServerAnnouncementThread got null jsonObjectArray from the string");
			return;
		}
		
		if(jsonObjectArray.length() < 1) {
			Log.e(activitynametag, "ServerAnnouncementThread got too short length for jsonObjectArray");
			return;
		}
		
		Integer announcementId = -1;
		String announcement = "";
		
		for(int i = 0; i < jsonObjectArray.length(); i++){
			try {
				JSONObject announcementObject = jsonObjectArray.getJSONObject(i);
				Integer currAnnouncementId = Integer.valueOf(announcementObject.getInt("announcement_id"));
				
				if(currAnnouncementId > announcementId) {
					announcementId = Integer.valueOf(announcementObject.getInt("announcement_id"));
					announcement = new String(announcementObject.getString("announcement_text"));
				}
					
			} catch (JSONException e) { 
				Log.e(activitynametag, e.toString()); 
				return; 
			}
		}
		
		Log.d(activitynametag, "SAT got annoucement: (" + announcementId + ", \"" + announcement + "\")");
		
		if(announcementId == -1) {
			Log.w(activitynametag, "Wasn't able to get an announcement (announcementId was -1)");
			return;
		}
		
		announcementDatabase.open();
				
		if(!announcementDatabase.databaseContainsId(announcementId)) {
			ServerAnnouncement sa = new ServerAnnouncement(announcementId, announcement); 
			GameInfo.setAnnouncement(sa);
		} 
		
		announcementDatabase.close();
	}
}
