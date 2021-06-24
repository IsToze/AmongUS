package fr.Toze.amongus.throwable;

public class CameraException extends Exception {

	private static final long serialVersionUID = -400490662699849359L;
	private String reason, cameraName;
	
	public CameraException(String reason, String cameraName) {
		this.reason = reason;
		this.cameraName = cameraName;
	}
	
	public String getMessage(){
		return reason;
	}
	
	public String getCameraName() {
		return cameraName;
	}
	
}
