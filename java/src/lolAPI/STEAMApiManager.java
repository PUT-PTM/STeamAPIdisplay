package lolAPI;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class STEAMApiManager {



public static void GetFriendList(String apiKey, String steamID, ArrayList<Gamer> friendlist) throws MalformedURLException, IOException, JSONException{
String serverLink = "http://api.steampowered.com/ISteamUser/GetFriendList/v0001/?key=";
String charset = "UTF-8";  

URLConnection connection = new URL(serverLink + apiKey +  "&steamid=" + steamID+"&relationship=friend").openConnection();
connection.setRequestProperty("Accept-Charset", charset);
InputStream response = connection.getInputStream();	
try (Scanner scanner = new Scanner(response)) {
    String responseBody = scanner.useDelimiter("\\A").next();
   
    
    
    JSONObject friendslist = new JSONObject(responseBody);
    JSONObject friends = friendslist.getJSONObject("friendslist");
    JSONArray arr = friends.getJSONArray("friends");

    try {
    	for (int i = 0; i < arr.length(); i++){
    		String a = arr.getJSONObject(i).get("steamid").toString();
    	//	System.out.println(a);
    		Gamer gm = new Gamer();
    		gm.setSteamid(a);
    		friendlist.add(gm);
    	}
    }
    
    catch(Exception e){
    	
    }
    
	    for (Gamer g : friendlist){    	
	    	GetPlayerSummaries(apiKey, g.getSteamid(), friendlist);
	    }
    
	}
}



	/* zapytanie pobiera informacje o graczu: nickname i stan (ONLINE/OFFLINE)
	* funkcja modyfikuje liste znajomych - przyporzadkowuje numerom ID nickname i stan 
 	*/

public static void GetPlayerSummaries(String apiKey, String steamID, ArrayList<Gamer> friendlist) throws MalformedURLException, IOException{
	String serverLink = "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=";
	String charset = "UTF-8";  

	URLConnection connection = new URL(serverLink + apiKey +  "&steamids=" + steamID).openConnection();
	connection.setRequestProperty("Accept-Charset", charset);
	InputStream response = connection.getInputStream();		
	try (Scanner scanner = new Scanner(response)) {
	    String responseBody = scanner.useDelimiter("\\A").next();
	    
		/* przetwarzanie JSONa --> pobieranie nickname'u i stanu aktywnoœci dla ka¿dego gracza */

	    
	    JSONObject obj = new JSONObject(responseBody);
	    JSONObject obj2 = obj.getJSONObject("response");
	    JSONArray array = obj2.getJSONArray("players");

	    for (int i = 0; i < array.length(); i++){
    		String steamid = array.getJSONObject(i).get("steamid").toString();
    		String personaname = array.getJSONObject(i).get("personaname").toString();
    		String personastate = array.getJSONObject(i).get("personastate").toString();
    		boolean online = false;
    		if (personastate.equalsIgnoreCase("0")) {
    			 online = false;
    		}
    		if (personastate.equalsIgnoreCase("1")) {
    			 online = false;
    		}
    		

    		for (Gamer g : friendlist)
    		{

    			if (g.getSteamid().equals(steamid)){
    				g.setNickname(personaname);
    				g.setOnline(online);
    			}
    		}

	    }
	
	}
	catch (Exception e){
		
	}
}


}