package lolAPI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

public static void main(String[] args) throws Exception   {

		
		boolean friendActivity = true;	// ONLINE <=> TRUE,  OFFLINE <=> FALSE
	
		Player player;
		ArrayList<Team> teams = new ArrayList<Team>();
		
		String key1 = "";
		String key2 = "";		
		
		

		
		File conf1 = new File("data/key1.conf");
		File conf2 = new File("data/key2.conf");
		
		
		File team1 = new File("data/team1.in");
		File team2 = new File("data/team2.in");
		
		
		Scanner in = new Scanner(new FileReader(conf1));
		while (in.hasNext()) key1 = in.nextLine();
		
		in = new Scanner(new FileReader(conf2));
		while (in.hasNext()) key2 = in.nextLine();
 
		
			while(true){
			

				try {
					new JavaMicrocontrollerCommunicator().connect("COM5"); 
					
					
					
					//  <===> STEAM API
					
					
					ArrayList<Gamer> friendlist = new ArrayList<Gamer>();	
					
					String steamKEY = "A874C5F48D6FBE8A2AFD35513589D4D3";
					String ID = "76561198013643275";
					
					STEAMApiManager.GetFriendList(steamKEY, ID, friendlist);
					for (Gamer g : friendlist){
						if ( g.getOnline() == friendActivity ){
							System.out.println(g.getNickname());
							
						    CommPortSender.send(new ProtocolImpl().getMessage(g.getNickname()));  

						}
					}

					
					
					// 		<===>  RIOT GAMES API
					
					
					
					try {
						player = RiotAPIManager.findUserID(key1);							
						String res = RiotAPIManager.findUserDetailsByID(player.getPlayerID(), player.getPlayerNickname(), key2);
						
						try {
							teams = RiotAPIManager.findCurrentGameDetails(player.getPlayerID(),key1);
						
							RiotAPIManager.findPlayersInGameDetails(teams,key1,key2);
		
						
						
									BufferedReader br = new BufferedReader(new FileReader(team1));
									
									try {
									    StringBuilder sb = new StringBuilder();
									    String line = br.readLine();
									    CommPortSender.send(new ProtocolImpl().getMessage(line));  
									    
									   for (int i = 0; i <=3; i++) {
									        sb.append(line);
									        sb.append(System.lineSeparator());
									        line = br.readLine();
									        CommPortSender.send(new ProtocolImpl().getMessage(line));  
									    }
									    String everything = sb.toString();
									} finally {
									    br.close();
									}			
								}
						catch (Exception e){
						    CommPortSender.send(new ProtocolImpl().getMessage(res + "\nGracz offline."));  

						}
						
					}
				
					catch (Exception e){
					    CommPortSender.send(new ProtocolImpl().getMessage("Gracz nie znaleziony."));  

						}
					
					
					
					
				}
				
																															
							
					
				catch (Exception e){
					System.out.println("Brak dostepu");
				}					
					}
		

		}

}
