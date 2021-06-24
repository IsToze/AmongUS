package fr.Toze.amongus.event;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.scheduler.BukkitRunnable;

import fr.Toze.amongus.Main;
import fr.Toze.amongus.enums.MessageList;
import fr.Toze.amongus.manager.CameraManager;
import fr.Toze.amongus.utils.ActionBar;
import fr.Toze.amongus.utils.ParticleEffect;
import fr.Toze.amongus.utils.ParticleEffect.ParticleColor;

public class CameraEvent implements Listener {

	private Map<String, Entity> map;
	private CameraManager cameraManager;

	public CameraEvent(Main main) {
		map = new HashMap<>();
		this.cameraManager = main.getCameraManager();
		ActionBar actionbar = new ActionBar();
		ParticleColor color = new ParticleEffect.OrdinaryColor(Color.RED);
	
		new BukkitRunnable() {
			int count = 0;
			
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()){
					String name = player.getName();
					Entity vehicle = player.getVehicle();
					if(vehicle == null && map.containsKey(name)){
						map.remove(name);
						main.getCameraManager().dismount(player);
					}
				}
				
				if(count == 8){
					count = 0;
					
					List<Player> players = cameraManager.getUsers().stream().map(u -> Bukkit.getPlayer(u)).collect(Collectors.toList());
					actionbar.send(players, MessageList.MESSAGE_RIGHT_CLICK.toString());
					
					Set<Location> set = new HashSet<>(players.stream()
							.map(p -> cameraManager.getCamera(p))
							.filter(group -> group.getGroup() == 1).map(p -> p.getLocation()).collect(Collectors.toList()));
					
					for(Location locs : set){
						locs = locs.clone().add(0.5, 0.5, 0.5);
						for(double[] doubles : new double[][]{{0, 0, 0.5}, {0, 0, -0.5}, 
							{0.5, 0, 0}, {-0.5, 0, 0},
							{0, 0.5, 0}, {0, -0.5, 0}}){
							ParticleEffect.REDSTONE.display(color, locs.clone().add(doubles[0], doubles[1], doubles[2]),
									30);
						}
					}
					
				}
				count++;
				
			}
		}.runTaskTimer(main, 5, 5);

	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event){
		if(see((Player) event.getWhoClicked())) event.setCancelled(true);
	}
	
	@EventHandler
	public void onDropItem(PlayerDropItemEvent event){
		if(see(event.getPlayer())) event.setCancelled(true);
	}
	
	private boolean see(Player player) {
		return this.cameraManager.getCamera(player) != null;
	}
	
	@EventHandler
	public void onInteract(PlayerInteractAtEntityEvent event){
		Player player = event.getPlayer();
		if(this.cameraManager.getCamera(player) != null){
			this.cameraManager.nextCamera(player);
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent event){
		Entity entity = event.getEntity();
		if(entity.getType().equals(EntityType.PLAYER)){
			event.setCancelled(map.containsKey(entity.getName()));
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event){
		cameraManager.dismount(event.getPlayer());
	}

	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent event){
		event.setCancelled(true);
	}
	
	public void setUndamageable(Player player, ArmorStand armor) {
		map.put(player.getName(), armor);
	}

}
