package lolAPI;

import java.net.*;
import java.io.*;
import java.util.*;
import org.json.*;


 



public class lolServerConnector {


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
	 * zapytanie do API o część, która ma się pojawić na wyświetlaczu -
	 * tworzy dane [ nick gracza - dywizja ]
	 */
	
	public static String findUserDetailsByID(int ID, String nick, String APIkey) throws MalformedURLException, IOException, JSONException{
		
		
		String serverLink = "https://eune.api.pvp.net/api/lol/eune/v2.5/league/by-summoner/"; 
		int playerID = ID;
		String charset = "UTF-8";  
				
		String tier="";
		String division="";
		
		URLConnection connection = new URL(serverLink + playerID+ "/entry" + "?" + APIkey).openConnection();
		connection.setRequestProperty("Accept-Charset", charset);
		InputStream response = connection.getInputStream();	
		
		try (Scanner scanner = new Scanner(response)) {
		    String responseBody = scanner.useDelimiter("\\A").next();
		    
		    
			JSONObject obj = new JSONObject(responseBody);						  					    						    
	
			
			   JSONArray arr = obj.getJSONArray((Integer.toString(ID)));
			  
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
		
		}
		
		// NICK RETURN ADDED JUST FOR TESTS !
		
		  return nick +" " + tier +" "+ division;			
	}
						
	

	
	
	/*
	 * zapytanie, które znajduje inforamacje o wszystkich graczach, którzy są aktualnie w danej grze
	 * tworzy listę Team: 2 drużyny 5-osobowe
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
	 * Funkcja, która przetwarza dane uzyskane poprzez findCurrentGameDetails().
	 * Każdy gracz na liście jest sprawdzany przez funkcję findUserDetailsByID().
	 * Uzyskujemy dane [ nick gracza - dywizja ] dla wszystkich 10 graczy w grze.
	 * Dane wyprowadzamy do pliku. 
	 * TODO : STM - czytanie z utworzonych plików
	 */
	
	public static void findPlayersInGameDetails(ArrayList<Team> teams, String key1, String key2) throws MalformedURLException, IOException, JSONException{
	
		int iterator = 0;
		String key = key1;
		

		/* TODO: 
		 * zmiana ścieżek - dopasowanie do katalogów projektu, a nie lokalnego komputera
		 *  
		 */
		
		File team1 = new File("C:/Users/Erwin/Desktop/webAPI/data/team1.in");
		File team2 = new File("C:/Users/Erwin/Desktop/webAPI/data/team2.in");
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
			System.out.println( findUserDetailsByID(temp.getPlayerID(), temp.getPlayerNickname(), key));
			
			try {
			    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(team, true)));
			    out.println(findUserDetailsByID(temp.getPlayerID(), temp.getPlayerNickname(), key));
			    out.close();
			} catch (IOException e) {
			    //exception handling left as an exercise for the reader
			}
			
			}
			iterator++;
			
		}
		
	
		
	}
	
	
	
	
	
	
	
	
	public static void main(String[] args) throws MalformedURLException, IOException, JSONException   {

		Player player;
		ArrayList<Team> teams = new ArrayList<Team>();
		
		String key1 = "";
		String key2 = "";		
		
		

		
		File conf1 = new File("C:/Users/Erwin/Desktop/webAPI/data/key1.conf");
		File conf2 = new File("C:/Users/Erwin/Desktop/webAPI/data/key2.conf");
		
		Scanner in = new Scanner(new FileReader(conf1));
		while (in.hasNext()) key1 = in.nextLine();
		
		in = new Scanner(new FileReader(conf2));
		while (in.hasNext()) key2 = in.nextLine();
		
		
		
		
			while(true){
			
					
				//	GraphicalInterface gui = new GraphicalInterface();
						
							player = findUserID(key1);
							
						//	System.out.println(player.getPlayerNickname()+"  lvl: " +player.getPlayerLevel()+"  ID: "+player.getPlayerID());
							
						//	System.out.println(findUserDetailsByID(player.getPlayerID(),player.getPlayerNickname(), key2));
							
							teams = findCurrentGameDetails(player.getPlayerID(),key1);
							
							findPlayersInGameDetails(teams,key1,key2);
			
							
							
							
							
							/* 		id -  49950320  - do testów		 	*/						
							
					
							
			}
			
			
		}


}
