package lolAPI;
public class Player{
	int playerID;
	String playerNickname;
	int playerLevel;
	
	public Player(){};
	
	
	
	
	public Player(int playerID, String playerNickname, int playerLevel) {
		super();
		this.playerID = playerID;
		this.playerNickname = playerNickname;
		this.playerLevel = playerLevel;
	}




	public void setPlayerID(int playerID){this.playerID = playerID;}
	public void setPlayerNickname(String playerNickname){this.playerNickname = playerNickname;}
	public void setLevel(int level){this.playerLevel = level;}
	
	public int getPlayerID(){return this.playerID;}
	public int getPlayerLevel(){return this.playerLevel;}
	public String getPlayerNickname(){return this.playerNickname;}
}