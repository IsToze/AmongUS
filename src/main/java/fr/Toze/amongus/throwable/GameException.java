package fr.Toze.amongus.throwable;

public class GameException extends Exception {

	private static final long serialVersionUID = -7703182392675630049L;

	private String reason;
	
	public GameException(String reason) {
		this.reason = reason;
	}
	
	public String getMessage(){
		return reason;
	}
	
}
