package fr.Toze.amongus.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Converter {

	public Location toLocation(String loc) {
		String s[] = loc.split(" ");
		return new Location(Bukkit.getWorld(s[0]), 
				Integer.valueOf(s[1]), Integer.valueOf(s[2]), Integer.valueOf(s[3]));
	}
	
	public String toString(Location loc) {
		return loc.getWorld().getName()+" "+loc.getBlockX()+" "+loc.getBlockY()+" "+loc.getBlockZ();
	}
	
}
