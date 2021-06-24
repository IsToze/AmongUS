package fr.Toze.amongus.throwable;

import fr.Toze.amongus.interfaces.Task;

public class TaskException extends Exception {

	private static final long serialVersionUID = 8760954391012724785L;
	private String reason;
	private Task task;
	
	public TaskException(String reason, Task task) {
		this.reason = reason;
		this.task = task;
	}
	
	public String getMessage(){
		return reason;
	}
	
	public Task getTask() {
		return task;
	}
	
}
