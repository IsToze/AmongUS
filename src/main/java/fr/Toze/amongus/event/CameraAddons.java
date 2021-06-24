package fr.Toze.amongus.event;
import java.util.Optional;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import fr.Toze.amongus.AmongGame.GameState;
import fr.Toze.amongus.Main;
import fr.Toze.amongus.enums.MessageList;
import fr.Toze.amongus.manager.CameraManager;
import fr.Toze.amongus.manager.CameraManager.Camera;
import fr.Toze.amongus.utils.Converter;
import fr.Toze.amongus.utils.NBTEntity;
import fr.Toze.amongus.utils.NBTEntity.Equipment;

public class CameraAddons implements Listener {

	private Main main;
	private CameraManager manager;
	private Location location;

	public CameraAddons(Main main) {
		this.main = main;
		this.manager = main.getCameraManager();
		this.location = new Converter().toLocation(MessageList.GROUP_CAMERA_BUTTON.toString());
	}

	@EventHandler
	public void onButton(PlayerInteractEvent event){
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
			if(event.getClickedBlock().getLocation().equals(location)){
				Player player = event.getPlayer();
				ArmorStand armor = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
				armor.setGravity(false);
				armor.setCustomName("§b"+player.getName());
				armor.setCustomNameVisible(true);

				ItemStack coloredArmor = new ItemStack(Material.LEATHER_BOOTS);
				LeatherArmorMeta meta = (LeatherArmorMeta) coloredArmor.getItemMeta();
				int[] color = this.main.getGame().getColors().getColor(player).getRgb();
				meta.setColor(Color.fromRGB(color[0], color[1], color[2]));
				coloredArmor.setItemMeta(meta);

				armor.setBoots(coloredArmor);
				coloredArmor.setType(Material.LEATHER_LEGGINGS);
				armor.setLeggings(coloredArmor);
				coloredArmor.setType(Material.LEATHER_CHESTPLATE);
				armor.setChestplate(coloredArmor);
				coloredArmor.setType(Material.LEATHER_HELMET);
				armor.setHelmet(coloredArmor);

				NBTEntity entity = new NBTEntity(armor);
				entity.put(Equipment.BOOTS, "CameraViewerFor", player.getName());

				this.manager.see(player, this.manager.getCameraList(MessageList.GROUP_CAMERA.toInt()).get(0));
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractAtEntityEvent event){
		Entity entity = event.getRightClicked();
		if(!entity.getType().equals(EntityType.ARMOR_STAND)) return;
		String name = entity.getCustomName();
		Player player = event.getPlayer();
		if(name != null && name.startsWith("CameraClicker") && canUse(player)){
			this.manager.see(player, this.manager.getCamera(name.split(" ")[1]).get());
		}
	}


	@EventHandler
	public void onTrapInteract(PlayerInteractEvent event){
		if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
		Block block = event.getClickedBlock();
		if(!block.getType().equals(Material.IRON_TRAPDOOR)) return;
		Location location = block.getLocation();
		Optional<Camera> cameras = this.manager.getCameras().stream().filter(
				cam -> cam.getLocation().distance(location) <= 1).findFirst();
		if(cameras.isPresent()){
			Player player = event.getPlayer();
			if(canUse(player)){
				this.manager.see(player, cameras.get());
			}

		}
	}

	private boolean canUse(Player player) {
		GameState state = this.main.getGame().getState();
		return state.getImpostors().contains(player) || !state.isProgress();
	}


}
