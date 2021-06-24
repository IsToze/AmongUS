package fr.Toze.amongus.manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import fr.Toze.amongus.Main;
import fr.Toze.amongus.throwable.CameraException;
import fr.Toze.amongus.utils.EntitySearcher;
import fr.Toze.amongus.utils.Items;
import fr.Toze.amongus.utils.NBTEntity.Equipment;

public class CameraManager {

	private File file;
	private List<Camera> cameras;
	private Inventory inventory;
	private Map<String, PlayerState> map;
	private Items item;
	
	public CameraManager(Main main) {
		this.file = main.getFiles().getFile("cameras");
		this.cameras = new ArrayList<>();
		this.inventory = Bukkit.createInventory(null, 54, "§6Camera List :");
		this.map = new HashMap<>();
		this.item = new Items();
		
		if(!this.file.exists()){
			try {
				this.file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			for(String key : config.getKeys(false)){
				System.out.println("Load camera : "+key);
				Camera camera = new Camera(
						key, 
						new Location(Bukkit.getWorld(config.getString(key+".world")),
								config.getInt(key+".x"),
								config.getInt(key+".y"),
								config.getInt(key+".z")),
						config.getInt(key+".group"));
				cameras.add(camera);
				addToInventory(camera);
			}
		}
	
	}

	public void createCamera(String name, Location location, int group){
		if(getCamera(name).isPresent()){
			new CameraException("Camera Aleardy exist!", name);
		}else{
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			
			config.set(name+".group", group);
			config.set(name+".x", location.getBlockX());
			config.set(name+".y", location.getBlockY());
			config.set(name+".z", location.getBlockZ());
			config.set(name+".world", location.getWorld().getName());
			
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			Camera camera = new Camera(name, location, group);
			cameras.add(camera);
			addToInventory(camera);
			System.out.println("Create new camera with name '"+name+"' to group number "+group+".");
		}
	}
	
	private void addToInventory(Camera camera) {
		String name = camera.getName();
		Location location = camera.getLocation();
		inventory.addItem(this.item.getSkull(1, "§6> §e§l"+name, 
				Arrays.asList("§dName > §a"+name, "§dGroup > §a"+camera.getGroup(),
						"§dX > §a"+location.getBlockX(),
						"§dY > §a"+location.getBlockY(),
						"§dZ > §a"+location.getBlockZ(),
						"§dWorld > §a"+location.getWorld().getName()), "SecurityCamera"));
	}

	public void see(Player player, Camera camera){
		if(getCamera(player) != null) return;
		Location location = camera.getLocation();
		//player.sendBlockChange(location, 0, (byte) 0);
		location = location.clone().add(0.5, -1.5, 0.5);

		Bukkit.getScoreboardManager().getMainScoreboard().getTeam("§cTraps").addPlayer(player);
		ArmorStand armor = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
		armor.setVisible(false);
		armor.setCustomName("CameraArmor "+camera.getName()+" "+camera.getGroup());
		armor.setSmall(true);
		armor.setGravity(false);
		armor.setPassenger(player);
		map.put(player.getName(), new PlayerState(player, armor));
	}
	
	public void nextCamera(Player player){
		PlayerState state = this.map.get(player.getName());
		ArmorStand armor = state.getArmor();
		String[] array = armor.getCustomName().split(" ");
		
		String name = array[1];
		int group = Integer.valueOf(array[2]);
		
		List<Camera> list = getCameraList(group);
		
		int index = -1;
		for(int i = 0; i < list.size(); i++){
			if(list.get(i).getName().equalsIgnoreCase(name)){
				index = i+1;
				break;
			}
		}
		
		if(index == list.size()) index = 0;
		Camera newcam = list.get(index);
		armor.setCustomName("CameraArmor "+newcam.getName()+" "+newcam.getGroup());
		Location location = newcam.getLocation().clone().add(0.5, -1.5, 0.5);
		((CraftArmorStand) armor).getHandle().setPosition(location.getX(), location.getY(), location.getZ());
		
	}
	
	public void dismount(Player player) {
		String name = player.getName();
		PlayerState state = map.remove(name);
		if(state != null){
			ArmorStand armor = state.getArmor();
			armor.remove();
			//Block block = armor.getWorld().getBlockAt(armor.getLocation().clone().subtract(0.5, -2, 0.5));
			//player.sendBlockChange(block.getLocation(), block.getTypeId(), block.getData());
			//"See camera : "
			List<LivingEntity> list = new EntitySearcher(EntityType.ARMOR_STAND, Equipment.BOOTS,
					"CameraViewerFor", player.getName()).getAll();
			boolean empty = list.isEmpty();
			state.reset(!empty);
			list.forEach(e -> e.remove());
			if(empty){
				player.teleport(player.getLocation().add(0, 1, 0));
				player.setVelocity(new Vector(0, 0.3, 0));
			}
			Bukkit.getScoreboardManager().getMainScoreboard().getTeam("§cPlayers").addPlayer(player);
		}
	}
	
	public void dismountAll() {
		new HashMap<>(this.map).keySet().forEach(p -> dismount(Bukkit.getPlayer(p)));
	}
	
	public List<Camera> getCameraList(int group){
		return cameras.stream().filter(camera -> camera.getGroup() == group).collect(Collectors.toList());
	}
	
	public List<Camera> getCameraListNotIn(int group){
		return cameras.stream().filter(camera -> camera.getGroup() != group).collect(Collectors.toList());
	}
	
	public List<Camera> getCameras(){
		return cameras;
	}
	
	public Optional<Camera> getCamera(String name){
		return cameras.stream().filter(camera -> camera.getName().equalsIgnoreCase(name)).findFirst();
	}
	
	public Camera getCamera(Player player){
		String name = player.getName();
		if(!this.map.containsKey(name)) return null;
		return getCamera(this.map.get(name).getArmor().getCustomName().split(" ")[1]).get();
	}
	
	public Set<String> getUsers(){
		return this.map.keySet();
	}
	
	public Inventory getInventory() {
		return inventory;
	}
	
	public class Camera{

		private String name;
		private Location location;
		private int group;
		
		public Camera(String name, Location location, int group) {
			this.name = name;
			this.location = location;
			this.group = group;
		}
		
		public String getName() {
			return name;
		}
		
		public Location getLocation() {
			return location;
		}
		
		public int getGroup() {
			return group;
		}
		
	}

	private class PlayerState{
		
		private double health, maxHealth;
		private GameMode gamemode;
		private Player player;
		private ItemStack[] inventoryContents, armorContents;
		private Location location;
		private ArmorStand armor;
		
		public PlayerState(Player player, ArmorStand armor) {
			this.player = player;
			this.gamemode = player.getGameMode();
			this.health = player.getHealth();
			this.maxHealth = player.getMaxHealth();
			this.location = player.getLocation();
			this.inventoryContents = player.getInventory().getContents();
			this.armorContents = player.getInventory().getArmorContents();
			this.armor = armor;
			
			player.setMaxHealth(1);
			PlayerInventory inventory = player.getInventory();
			inventory.clear();
			inventory.setHelmet(null);
			inventory.setChestplate(null);
			inventory.setLeggings(null);
			inventory.setBoots(null);
			player.updateInventory();
			player.setGameMode(GameMode.ADVENTURE);
			player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, true));
			
		}


		public void reset(boolean teleport){
			this.player.setGameMode(this.gamemode);
			this.player.setMaxHealth(this.maxHealth);
			this.player.setHealth(this.health);
			if(teleport) this.player.teleport(this.location);
			PlayerInventory inventory = player.getInventory();
			inventory.clear();
			inventory.setContents(this.inventoryContents);
			inventory.setArmorContents(this.armorContents);
			this.player.removePotionEffect(PotionEffectType.INVISIBILITY);
		}
		
		public ArmorStand getArmor() {
			return armor;
		}
		
	}
	
}
