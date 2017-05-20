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
 
		new JavaMicrocontrollerCommunicator().connect("COM5"); 
		 CommPortSender.send(new ProtocolImpl().getMessage("WELCOME"));  
	     CommPortSender.send(new ProtocolImpl().getMessage("TO")); 
	     CommPortSender.send(new ProtocolImpl().getMessage("LOL API")); 
		
		
		
			while(true){
			

				try {
					// new JavaMicrocontrollerCommunicator().connect("COM5"); 
			        
		//	        CommPortSender.send(new ProtocolImpl().getMessage("TEKSTTEKST222xxxxx")); 
			//        CommPortSender.send(new ProtocolImpl().getMessage("TEKSTTEKST22x")); 

					
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
						
					
					//	CommPortSender.send(new ProtocolImpl().getMessage(player.getPlayerNickname()));
					//	CommPortSender.send(new ProtocolImpl().getMessage(res));  
					//	System.out.println("HERE  "+res);
						
						try {
							teams = RiotAPIManager.findCurrentGameDetails(player.getPlayerID(),key1);
						
							RiotAPIManager.findPlayersInGameDetails(teams,key1,key2);
		
						
						
									BufferedReader br = new BufferedReader(new FileReader(team1));
									BufferedReader br2 = new BufferedReader(new FileReader(team2));

									try {
									    StringBuilder sb = new StringBuilder();
									    String line = br.readLine();
									    CommPortSender.send(new ProtocolImpl().getMessage(line));  
									    
									   for (int i = 0; i <=13; i++) {
									        sb.append(line);
									        sb.append(System.lineSeparator());
									        line = br.readLine();
									        CommPortSender.send(new ProtocolImpl().getMessage(line));  
									    }
									   
									   for (int i = 0; i <=13; i++) {
									        sb.append(line);
									        sb.append(System.lineSeparator());
									        line = br2.readLine();
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
