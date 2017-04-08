package lolAPI;


import java.util.LinkedList;


public class Team {
	

	private int id;
	private LinkedList<Player> players= new LinkedList<Player>();
	
	
	public Team() {
		super();
	}

	

	

	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}




	public void addPlayer(Player e) {
		players.add(e);
	}
	
	
	public LinkedList<Player> getPlayers(){
		return players;
	}

	
	public Player getPlayer(int idx){
		return players.get(idx);
	}


	@Override
	public String toString() {
		String result = "Team [id=" + this.getId() + "  players:  ";
			for(Player i : players){
				result += i.getPlayerNickname();
				result += "  ";
					
			}
		return result;
	}


}
