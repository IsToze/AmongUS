package fr.Toze.amongus.event;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;
import org.bukkit.scheduler.BukkitRunnable;

import fr.Toze.amongus.Main;
import fr.Toze.amongus.enums.MessageList;
import fr.Toze.amongus.interfaces.Task;
import fr.Toze.amongus.manager.TaskManager;

public class CustomMap implements Listener {

	private Image image;
	private Map<Player, MapCanvas> views;
	private int minX, minZ;
	private double diffX, diffZ;
	
	public CustomMap(Main main) {
		
		this.minX = MessageList.MAP_MIN_X.toInt();
		this.minZ = MessageList.MAP_MIN_Z.toInt();
		this.diffX = 256.0/(MessageList.MAP_MAX_X.toInt()-minX)-1.9;
		this.diffZ = 256.0/(MessageList.MAP_MAX_Z.toInt()-minZ);
		
		try {
			this.image = ImageIO.read(new URL("https://i.imgur.com/9yTpgSQ.jpg"));
			this.views = new HashMap<>();
			TaskManager tasks = main.getTasks();
			
			new BukkitRunnable() {
				@Override
				public void run() {
					Map<String, List<Task>> allTask = tasks.getPlayerTask();
					for(Entry<Player, MapCanvas> entry : views.entrySet()){
						MapCursorCollection cursor = new MapCursorCollection();
						
						Player player = entry.getKey();
						Location location = player.getLocation();
						
						int[] cos = cos(location.getX(), location.getZ());
						cursor.addCursor(cos[0], cos[1], (byte) getDirection(entry.getKey()), (byte) 1, true);
						
						for(Task task : allTask.get(player.getName())){
							Location locationTask = task.getLocation();
							int[] cosTask = cos(
									locationTask.getX(), 
									locationTask.getZ());
							cursor.addCursor(cosTask[0], cosTask[1], (byte) 0, (byte) 4, true);
						}
						
						entry.getValue().setCursors(cursor);
					}
				}
			}.runTaskTimer(Main.getInstance(), 10, 10);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected int[] cos(double x, double z) {
		return new int[]{(int) ((z-minZ)*-(diffZ)-138), (int) ((x-minX)*(diffX)-66)};
	}

	private int getDirection(Player player) {
        double rotation = (player.getLocation().getYaw() - 90.0F) % 360.0F;
        if (rotation < 0.0D) {
            rotation += 360.0D;
        }
        if ((0.0D <= rotation) && (rotation < 22.5D)) {
            return 8;
        }
        if ((22.5D <= rotation) && (rotation < 67.5D)) {
            return 10;
        }
        if ((67.5D <= rotation) && (rotation < 112.5D)) {
            return 12;
        }
        if ((112.5D <= rotation) && (rotation < 157.5D)) {
            return 14;
        }
        if ((157.5D <= rotation) && (rotation < 202.5D)) {
            return 0;
        }
        if ((202.5D <= rotation) && (rotation < 247.5D)) {
            return 2;
        }
        if ((247.5D <= rotation) && (rotation < 292.5D)) {
            return 4;
        }
        if ((292.5D <= rotation) && (rotation < 337.5D)) {
            return 6;
        }
        if ((337.5D <= rotation) && (rotation < 360.0D)) {
            return 8;
        }
        return -1;
    }
	
	@EventHandler
	public void onMapInitialize(MapInitializeEvent event){
		MapView view = event.getMap();
		view.setScale(Scale.CLOSEST);
		view.getRenderers().clear();
		view.addRenderer(new Renderer());
		view.setCenterX(0);
		view.setCenterZ(0);
		//view.setCenterX(1072);
		//view.setCenterZ(1099);
		view.setWorld(Bukkit.getWorld("world"));
	}
	
	public class Renderer extends MapRenderer{

		@Override
		public void render(MapView view, MapCanvas mapCanvas, Player player) {
			mapCanvas.drawImage(0, 0, image);
			views.put(player, mapCanvas);
		}
		
	}
	
}
