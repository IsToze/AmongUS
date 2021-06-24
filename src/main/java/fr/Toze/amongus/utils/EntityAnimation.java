package fr.Toze.amongus.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class EntityAnimation {
	
	private TimerManager manager;

	public EntityAnimation(LivingEntity entity, TYPE type) {
		this(entity, type, null);
	}
	
	public EntityAnimation(LivingEntity entity, TYPE type, ParticleEffect effect) {
		Location current = entity.getLocation();
		
		switch(type){
		
		case UP_ROTATION:
			manager = new TimerManager(new TimerTask(){

				int i = 0;
				boolean b = false;
				
				@Override
				public void run() {
					if(entity.isDead()) cancel();
					if(!b){
						i++;
						if(i == 60) b = true;
					}else{
						i--;
						if(i == -60) b = false;
					}
					current.setYaw(current.getYaw()+(b ? 2 : -2));
					entity.teleport(current.add(0, b ? (i < 30 ? 0.02 : 0.02-(0.0004*i)) : 
						(i > -30 ? -0.02 : -0.02-(0.0004*i)), 0));
				}
				
			}, 20);
			break;
		
		case CIRCLE:
			manager = new TimerManager(new TimerTask(){
				int i = 0;
				double needY = needY(entity);
				List<Location> circle = circleLoc(current, 1.5);
				@Override
				public void run() {
					if(entity.isDead()) cancel();
					i++;
					if(i == 40) i = 0;
					Location loc = circle.get(i);
					loc.setY(entity.getLocation().getY()+needY);
					effect.display(new Vector(0, 0, 0), 1, loc, 30);
				}
				
			}, 20);
			break;
			
		}
		
		manager.start();
		
	}

	protected double needY(LivingEntity entity) {
		return entity.getType().equals(EntityType.ARMOR_STAND) ? 2 : 0;
	}

	public static enum TYPE{
		UP_ROTATION,
		CIRCLE;
	}

	public static List<Location> circleLoc(Location loc, double radius){
		List<Location> locs = new ArrayList<>();
		
		for(double angle = 0; angle < 2*Math.PI; angle += Math.PI/20){
			final double x = radius * Math.cos(angle), z = radius * Math.sin(angle);
			loc.add(x, 0, z);
			locs.add(loc.clone());
			loc.subtract(x, 0, z);
		}
		return locs;
	}
	
}
