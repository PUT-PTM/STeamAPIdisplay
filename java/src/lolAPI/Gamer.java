package lolAPI;



public class Gamer {

	private String nickname;
	private String steamid;
	private boolean online;
	
	
	public Gamer(String nickname, String steamid, boolean online) {
		super();
		this.steamid = steamid;
		this.nickname = "";
		this.online = false;
	}
	
	public Gamer(){
		super();
		this.steamid = "";
		this.nickname = "";
		this.online = false;
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	public String getSteamid() {
		return steamid;
	}
	
	public void setSteamid(String steamid) {
		this.steamid = steamid;
	}
	
	public boolean getOnline() {
		return online;
	}
	
	public void setOnline(boolean online) {
		this.online = online;
	}

}