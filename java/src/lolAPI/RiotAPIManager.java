package lolAPI;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.io.*;
import java.util.*;
import org.json.*;


 



public class RiotAPIManager {


	/*
	 * funkcja znajduje ID gracza na podstawie nicku
	 */
	
	public static Player findUserID(String APIkey) throws MalformedURLException, IOException, JSONException{
		
						String serverLink = "https://eune.api.pvp.net/api/lol/eune/v1.4/summoner/by-name/"; 
						String playerNickname;
						String playerNicknameQuery;
						
						String charset = "UTF-8";  
								
							
						System.out.println("Podaj nick gracza: ");
						Scanner input = new Scanner(System.in);
						playerNickname = input.nextLine();
						playerNickname = playerNickname.replaceAll("\\s","");
						playerNicknameQuery = playerNickname.replaceAll("\\s","%20");
						playerNickname = playerNickname.toLowerCase();
				
						
				
				
						
						URLConnection connection = new URL(serverLink + playerNicknameQuery + "?" + APIkey).openConnection();
						connection.setRequestProperty("Accept-Charset", charset);
						InputStream response = connection.getInputStream();					
				
				
						try (Scanner scanner = new Scanner(response)) {
						    String responseBody = scanner.useDelimiter("\\A").next();
						  /*  System.out.println(responseBody);		*/
						    JSONObject obj = new JSONObject(responseBody);
						    Player player = new Player();
						    player.setPlayerNickname(obj.getJSONObject(playerNickname).getString("name"));
						    player.setPlayerID(obj.getJSONObject(playerNickname).getInt("id"));
						    player.setLevel( obj.getJSONObject(playerNickname).getInt("summonerLevel") ); 
						   		
						    return player;
						}
	

	}

	
	
	/*
	 * zapytanie do API o czÄ™?›Ä‡, kt??ra ma siÄ™ pojawiÄ‡ na wy?›wietlaczu -
	 * tworzy dane [ nick gracza - dywizja ]
	 */
	
	public static String findUserDetailsByID(int ID, String nick, String APIkey) throws MalformedURLException, IOException{
		
		
		String serverLink = "https://eune.api.pvp.net/api/lol/eune/v2.5/league/by-summoner/"; 
		int playerID = ID;
		String charset = "UTF-8";  
				
		String tier="-";
		String division="-";
		
		URLConnection connection = new URL(serverLink + playerID+ "/entry" + "?" + APIkey).openConnection();
		connection.setRequestProperty("Accept-Charset", charset);
		InputStream response = connection.getInputStream();	
		
		try (Scanner scanner = new Scanner(response)) {
		    String responseBody = scanner.useDelimiter("\\A").next();
		    
		    
			JSONObject obj = new JSONObject(responseBody);						  					    						    			
			JSONArray arr = obj.getJSONArray((Integer.toString(ID)));
			
			try{
			
			   for (int i = 0; i < arr.length(); i++)
			   {
				   if( arr.getJSONObject(i).getString("queue").equals("RANKED_SOLO_5x5")){
				       tier = arr.getJSONObject(i).getString("tier");							     
				     
				      JSONObject jsonObject1 = (JSONObject) arr.get(i);
				      JSONArray jsonarray1 = (JSONArray) jsonObject1.get("entries");
				   
			
				    
				       for (int j = 0; j < jsonarray1.length(); j++) {
				   division = jsonarray1.getJSONObject(j).getString("division");
				     } 				   
				   }				   				   					   								   
			   }
						
					} catch (Exception e){
							e.printStackTrace();
				}
		
		}catch (JSONException e1) {
		
		tier = "UNRANKED";
		division = "";
		
		return tier +" "+ division;	
			}
		
return tier +" "+ division;			

}
	

	
	
	/*
	 * zapytanie, kt??re znajduje inforamacje o wszystkich graczach, kt??rzy sÄ… aktualnie w danej grze
	 * tworzy listÄ™ Team: 2 dru??yny 5-osobowe
	 */
	
	
	public static ArrayList<Team> findCurrentGameDetails(int playerID, String APIkey) throws MalformedURLException, IOException, JSONException{
		String server = "EUN1";
		String serverLink = "https://eune.api.pvp.net/observer-mode/rest/consumer/getSpectatorGameInfo/"; 
		String charset = "UTF-8";  
		 String responseBody = "";
		String link = serverLink + server + "/" + playerID + "?" + APIkey;
		
		int defaultLevel = 30;
		
		Player player = new Player();
		
		Team team1 = new Team();
		Team team2 = new Team();

		
		team1.setId(100);
		team2.setId(200);
		
		URLConnection connection = new URL(link).openConnection();
		connection.setRequestProperty("Accept-Charset", charset);
		InputStream response = connection.getInputStream();	
		
		try (Scanner scanner = new Scanner(response)) {
		     responseBody = scanner.useDelimiter("\\A").next();
		}
	
		JSONObject obj = new JSONObject(responseBody);						  					    						    	
		JSONArray arr = obj.getJSONArray("participants");

		

		
		 for (int i = 0; i < arr.length(); i++)
		   {
			 
			 JSONObject jobj = arr.getJSONObject(i); 
			
			 int teamID = jobj.getInt("teamId");
			 int playerNR = jobj.getInt("summonerId");
			 String playerName = jobj.getString("summonerName");


			 
			 if (teamID == 100){
			
				 team1.addPlayer(new Player(playerNR, playerName, defaultLevel ));	 
				 
			 }
			 
			 else if (teamID == 200){
			
				 team2.addPlayer(new Player(playerNR, playerName, defaultLevel ));	 
				 
			 }
			 
				
					
		
				 
		   }
		 
		 ArrayList<Team> team = new ArrayList<Team>();
		 team.add(team1);
		 team.add(team2);
		 System.out.println(team.toString());

			 return team;
	}
	
	
	
	/*
	 * Funkcja, kt??ra przetwarza dane uzyskane poprzez findCurrentGameDetails().
	 * Ka??dy gracz na li?›cie jest sprawdzany przez funkcjÄ™ findUserDetailsByID().
	 * Uzyskujemy dane [ nick gracza - dywizja ] dla wszystkich 10 graczy w grze.
	 * Dane wyprowadzamy do pliku. 
	 */
	
	public static void findPlayersInGameDetails(ArrayList<Team> teams, String key1, String key2) throws MalformedURLException, IOException, JSONException{
	
		int iterator = 0;
		String key = key1;
		

		/* TODO: 
		 * zmiana ?›cie??ek - dopasowanie do katalog??w projektu, a nie lokalnego komputera
		 *  
		 */
		
		File team1 = new File("data/team1.in");
		File team2 = new File("data/team2.in");
		team1.delete();
		team2.delete();
		File team = team1;
		for (Team t : teams){
			if (iterator == 1) {
			key = key2;
			team = team2;
			}
			
			for (Player p : t.getPlayers()){
			Player temp = p;
			
			try {
			    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(team, true)));
			    out.println(p.getPlayerNickname());
			    out.println(findUserDetailsByID(temp.getPlayerID(), temp.getPlayerNickname(), key));
			    if (t.getId() == 100) out.println("TEAM I");
			    else out.println("TEAM II");
			    out.close();
			} catch (IOException e) {
			    //exception handling left as an exercise for the reader
			}
			
			}
			iterator++;
			
		}
		
	
		
	}
	
	
	
	
	
	
	
	
	


}

 
		
		/* 		id -  49950320  - do test??w		 	*/	