package fr.Toze.amongus.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TimerManager {

	private static List<Timer> timers;
	
	public static void init() {
		timers = new ArrayList<>();
	}

	public static void stopAll(){
		timers.forEach(timer -> timer.cancel());
	}
	
	public static int getSize(){
		return timers.size();
	}
	
	private Timer timer;
	private TimerTask timertask;
	private int delay;
	
	public TimerManager() {}
	
	public TimerManager(TimerTask task, int delay) {
		this.timer = new Timer();
		TimerManager.timers.add(timer);
		this.timertask = task;
		this.delay = delay;
	}
	
	public void start(){
		timer.schedule(timertask, delay, delay);
	}
	
	public void stop(){
		timer.cancel();
		TimerManager.timers.remove(timer);
		this.timer = new Timer();
		TimerManager.timers.add(timer);
	}
	
}
