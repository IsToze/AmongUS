package fr.Toze.amongus.event;

import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.Toze.amongus.AmongGame;
import fr.Toze.amongus.Main;
import fr.Toze.amongus.enums.MessageList;
import fr.Toze.amongus.interfaces.Task;
import fr.Toze.amongus.interfaces.Task.TaskState;
import fr.Toze.amongus.manager.TaskManager;
import fr.Toze.amongus.throwable.TaskException;
import fr.Toze.amongus.utils.Converter;
import fr.Toze.amongus.utils.EntitySearcher;
import fr.Toze.amongus.utils.NBTEntity;
import fr.Toze.amongus.utils.NBTEntity.Equipment;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PlayerConnection;

public class GameEvent implements Listener {

	private TaskManager manager;
	private AmongGame game;
	private Location emergencyLocation;

	public GameEvent(Main main) {
		this.manager = main.getTasks();
		this.game = main.getGame();
		this.emergencyLocation = new Converter().toLocation(MessageList.EMERGENCY_MEETING_LOCATION.toString());
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event){
		Player player = event.getPlayer();
		if(!ignore(player)){
			event.setCancelled(true);
			if(event.getBlock().getLocation().distance(this.emergencyLocation) == 0){
				int time = game.getEmergencyTimer();
				if(time > 0){
					player.sendMessage(MessageList.EMERGENCY_MEETING_TIMER_MESSAGE.toString()
							.replace("%timer%", String.valueOf(time)));
				}else{
					game.meeting();
				}
			}
		}
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event){
		if(!ignore(event.getPlayer())) event.setCancelled(true);
	}

	@EventHandler
	public void onFood(FoodLevelChangeEvent event){
		event.setFoodLevel(6);
	}

	@EventHandler
	public void onInventory(InventoryClickEvent event){
		if(!ignore((Player) event.getWhoClicked())) event.setCancelled(true);
	}
	@EventHandler
	public void onAtEntityInteract(PlayerInteractAtEntityEvent event){
		Player player = event.getPlayer();
		if(player.getItemInHand().getType() == Material.AIR){
			event.setCancelled(true);
			Entity entity = event.getRightClicked();
			if(entity.getType().equals(EntityType.ARMOR_STAND)){
				NBTEntity nbt = new NBTEntity(entity);
				if(((String) nbt.get(Equipment.BOOTS, "task-entity", String.class)).equalsIgnoreCase("true")
						&& ((String) nbt.get(Equipment.BOOTS, "task-player", String.class)).equalsIgnoreCase(player.getName())){
					try {
						Task task = this.manager.getTask((String) nbt.get(Equipment.BOOTS, "task-id", String.class)).get();
						CompletableFuture<TaskState> future = this.manager.execute(player, task);
						future.thenAccept(states -> {
							if(states.equals(TaskState.CANCELED)){
								player.sendMessage(MessageList.MESSAGE_PLAYER_CANCELLED_MISSION.toString());
							}else{
								player.closeInventory();
								entity.remove();
								player.sendMessage(MessageList.MESSAGE_PLAYER_COMPLETED_MISSION.toString());
								this.manager.complete(player, task);

								if(manager.getTaskRemaining() == 0){
									game.win(true);
								}

							}
						});
					} catch (TaskException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event){
		if(!ignore(event.getPlayer())) event.setCancelled(true);
	}

	private boolean ignore(Player player){
		return player.getGameMode().equals(GameMode.CREATIVE);
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event){
		//if(!game.getState().isProgress()) return;
		event.setCancelled(true);	

		Entity entity = event.getEntity();
		if(entity.getType().equals(EntityType.ARMOR_STAND)){
			String data = (String) new NBTEntity(entity).get(Equipment.BOOTS, "CameraViewerFor", String.class);
			if(data != null){
				Player damaging = Bukkit.getPlayerExact(data);
				if(damaging != null){
					Bukkit.getPluginManager().callEvent(new EntityDamageByEntityEvent(event.getDamager(), damaging, DamageCause.ENTITY_ATTACK, 1.0));
				}
			}
		}else{
			if(entity instanceof Player){
				Entity damagerEntity = event.getDamager();
				if(damagerEntity instanceof Player){
					game.attack((Player) damagerEntity, (Player) entity);
				}
			}
		}

	}

	@EventHandler
	public void onChangeChunk(PlayerMoveEvent event){
		Chunk newC = event.getTo().getChunk();
		if(!event.getFrom().getChunk().equals(newC)){
			Player player = event.getPlayer();
			String name = player.getName();
			PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
			new EntitySearcher(EntityType.ARMOR_STAND, Equipment.BOOTS, "task-entity", "true").getAll().stream().filter(a -> {
				return !((String)new NBTEntity(a).get(Equipment.BOOTS, "task-player", String.class)).equalsIgnoreCase(name);
			}).forEach(ad -> connection.sendPacket(new PacketPlayOutEntityDestroy(ad.getEntityId())));
		}
	}

}
