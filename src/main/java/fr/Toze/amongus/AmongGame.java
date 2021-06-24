package fr.Toze.amongus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimerTask;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.Toze.amongus.enums.MessageList;
import fr.Toze.amongus.interfaces.Task;
import fr.Toze.amongus.throwable.GameException;
import fr.Toze.amongus.utils.ColorGetter;
import fr.Toze.amongus.utils.ColorGetter.Color;
import fr.Toze.amongus.utils.Converter;
import fr.Toze.amongus.utils.Items;
import fr.Toze.amongus.utils.NBTEntity;
import fr.Toze.amongus.utils.NBTEntity.Equipment;
import fr.Toze.amongus.utils.NBTItemStack;
import fr.Toze.amongus.utils.TimerManager;
import fr.Toze.amongus.utils.Titles;
import fr.Toze.amongus.utils.VisualEntity;

public class AmongGame {

	private Main main;
	private GameState state;
	private int timer, meetingTime, emergencyTime;
	private ColorGetter colors;
	private boolean meeting;
	private Inventory inventory;
	private Map<String, Inventory> meetingsMap;
	private Items item;
	private ItemStack sword;
	
	public AmongGame(Main main) {

		this.main = main;
		this.state = new GameState();
		this.timer = -1;
		this.emergencyTime = MessageList.EMERGENCY_MEETING_DELAY.toInt();
		this.meetingsMap = new HashMap<>();
		this.item = new Items();
		
		new TimerManager(new TimerTask(){

			@Override
			public void run() {

				for(Player player : getState().getImpostors()){
					ItemStack stack = player.getInventory().getItem(3);
					if(stack != null){
						int amount = stack.getAmount();
						if(amount == 2){
							stack.setType(Material.DIAMOND_SWORD);
							stack.setAmount(1);
						}else{
							stack.setAmount(amount-1);
						}
					}
				}

				if(emergencyTime > 0) emergencyTime--;
				if(timer == 0){
					timer = -1;
					try {
						start();
					} catch (GameException e) {
						e.printStackTrace();
					}
				}else if(timer > 0){
					timer--;
					if(timer <= 3 || timer % 5 == 0){
						Bukkit.broadcastMessage(MessageList.MESSAGE_START_IN.toString().replace("%timer%", String.valueOf(timer)));
					}
				}
			}
		}, 1000);

	}

	public void start() throws GameException{

		if(this.state.isProgress()){
			throw new GameException("Game is aleardy in progress!");
		}

		System.out.println("Attempt to start a AmongUS.");
		this.state.progress = true;
		this.colors = new ColorGetter(this.state.crewmates);
		this.colors.getMap().entrySet().stream().forEach(entry -> Bukkit.getPlayerExact(
				entry.getKey()).sendMessage(MessageList.MESSAGE_PLAYER_SAY_COLOR
						.toString().replace("%color%", entry.getValue().getName())));

		tpAll();

		this.sword = this.item.getItem(Material.IRON_SWORD, MessageList.IMPOSTORS_KILL_COOLDOWN.toInt(), 0, "§c§l" + MessageList.IMPOSTORS_NAME+" Sword", null);
		ItemMeta metaSword = sword.getItemMeta();
		metaSword.spigot().setUnbreakable(true);
		sword.setItemMeta(metaSword);

		new BukkitRunnable() {
			HashMap<String, Color> copy1 = new HashMap<>(colors.getMap());
			ItemStack map = item.getItem(Material.EMPTY_MAP, 1, 0, "§6§lMap !", null);

			@Override
			public void run() {
				if(copy1.isEmpty()){
					cancel();

				}else{
					String skin = copy1.keySet().stream().findFirst().get();
					Color color = copy1.get(skin);
					//Bukkit.dispatchCommand(console, "skin set "+skin+" "+color.getURL());

					Player player = Bukkit.getPlayer(skin);

					ItemStack coloredArmor = new ItemStack(Material.LEATHER_BOOTS);
					LeatherArmorMeta meta = (LeatherArmorMeta) coloredArmor.getItemMeta();
					int[] color2 = color.getRgb();
					meta.setColor(org.bukkit.Color.fromRGB(color2[0], color2[1], color2[2]));
					coloredArmor.setItemMeta(meta);

					player.getInventory().clear();
					PlayerInventory inv = player.getInventory();
					inv.setBoots(coloredArmor);
					coloredArmor.setType(Material.LEATHER_LEGGINGS);
					inv.setLeggings(coloredArmor);
					coloredArmor.setType(Material.LEATHER_CHESTPLATE);
					inv.setChestplate(coloredArmor);
					coloredArmor.setType(Material.LEATHER_HELMET);
					inv.setHelmet(coloredArmor);

					if(state.getImpostors().contains(player)){
						inv.setItem(3, sword);
						inv.setItem(5, map);
						inv.setHeldItemSlot(5);
					}else{
						inv.setItem(4, map);
						inv.setHeldItemSlot(4);
					}

					player.updateInventory();

					copy1.remove(skin);
				}
			}
		}.runTaskTimer(this.main, 0, 3);



		List<Player> copy = new LinkedList<>(this.state.getCrewmates());
		Collections.shuffle(copy);
		List<Player> random = copy.subList(0, MessageList.IMPOSTOR_COUNT.toInt());

		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "effect @a jump_boost 0");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "effect @a jump_boost 30000 200 true");

		random.forEach(p -> {
			this.state.crewmates.remove(p);
			this.state.impostors.add(p);
		});

		Map<String, List<Task>> tasks = this.main.getTasks().initPlayerTask(this);
		for(Entry<String, List<Task>> entry : tasks.entrySet()){
			//Bukkit.broadcastMessage(entry.getKey()+"=");
			//Bukkit.broadcastMessage("   "+String.join(", ", entry.getValue().stream().map(i -> i.getName()).collect(Collectors.toList())));
			String name = entry.getKey();
			Player player = Bukkit.getPlayer(name);
			for(Task task : entry.getValue()){
				Location location = task.getLocation();
				Chunk chunk = location.getChunk();
				if(!chunk.isLoaded()) chunk.load();

				location.subtract(0, 0.6, 0);
				location.setYaw(40);
				location.setPitch(20);
				VisualEntity entity = new VisualEntity(player, location, EntityType.ARMOR_STAND);
				ArmorStand armor = (ArmorStand) entity.getEntity();
				armor.setSmall(true);
				armor.setGravity(false);
				armor.setCustomName("§7"+MessageList.TASK_NAME.toString()+" "+task.getDuration().getToLanguage());
				armor.setCustomNameVisible(true);
				armor.setVisible(false);
				armor.setHelmet(new ItemStack(Material.GOLD_BLOCK));
				NBTEntity nbt = new NBTEntity(entity.getEntity());
				nbt.put(Equipment.BOOTS, "task-entity", "true");
				nbt.put(Equipment.BOOTS, "task-id", task.getName());
				nbt.put(Equipment.BOOTS, "task-player", name);
				//	new EntityAnimation(armor, TYPE.CIRCLE, ParticleEffect.FLAME);

			}
		}

		new Titles().title("§c"+MessageList.IMPOSTORS_NAME.toString()).subtitle(
				"§c"+MessageList.IMPOSTORS_GOAL.toString()).times(10, 60, 10).send(random);
		random.forEach(p -> p.sendMessage("§c"+MessageList.IMPOSTORS_NAME.toString()+" §c! "+MessageList.IMPOSTORS_GOAL.toString()));
		new Titles().title("§b"+MessageList.CREWMATES_NAME.toString()).subtitle(
				"§b"+MessageList.CREWMATES_GOAL.toString()).times(10, 60, 10).send(this.state.crewmates);
		this.state.crewmates.forEach(p -> p.sendMessage("§b"+MessageList.CREWMATES_NAME.toString()+" §b! "+MessageList.CREWMATES_GOAL.toString()));

		this.inventory = Bukkit.createInventory(null, 36, "§4MEETING!");
		ItemStack[] contents = this.item.getOutline(4, 14);

		List<Integer> slots = new ArrayList<>();

		for(int i = 10; i <= 23; i++){
			if(i == 17) i+=4;
			slots.add(i);
		}

		float f = Float.valueOf(String.valueOf(Double.valueOf(MessageList.WALK_SPEED.toInt()/10.0)));
		for(Player players : getState().allPlayers){
			players.setWalkSpeed(f);
			Color color = colors.getColor(players);
			int[] color2 = color.getRgb();
			ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
			LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
			meta.setColor(org.bukkit.Color.fromRGB(color2[0], color2[1], color2[2]));
			meta.setDisplayName("§"+String.valueOf(color.getParagraph())+color.getName()+" §c("+players.getName()+")");
			helmet.setItemMeta(meta);
			contents[slots.remove(0)] = helmet;
		}

		this.meetingTime = MessageList.MEETING_TIME.toInt();
		contents[25] = this.item.getItem(Material.GOLD_HELMET, 1, 0, "§cSkip!", null);
		contents[31] = this.item.getItem(Material.SIGN, 1, 0, "§e>§a§l "+meetingTime+" seconds.", null); 
		inventory.setContents(contents);

		System.out.println("Successful start!");

	}

	public void meeting(){
		meeting = true;
		tpAll();
		this.meetingsMap.clear();
		this.state.allPlayers.forEach(p ->{
			if(state.isAlive(p)){
				p.closeInventory();
				Inventory inv = Bukkit.createInventory(null, inventory.getSize(), inventory.getTitle());
				inv.setContents(inventory.getContents());
				p.openInventory(inv);
				meetingsMap.put(p.getName(), inv);
			}
		});
		new BukkitRunnable() {
			int i = meetingTime;

			@Override
			public void run() {
				i--;
				ItemStack sign = item.getItem(Material.SIGN, 1, 0, "§e>§a§l "+meetingTime+" seconds.", null);
				if(i == -1){
					emergencyTime = MessageList.EMERGENCY_MEETING_DELAY.toInt();
					meeting = false;
					cancel();
					state.allPlayers.forEach(p -> p.closeInventory());

					Map<String, List<String>> map = new HashMap<>();

					for(Entry<String, Inventory> entry : meetingsMap.entrySet()){
						String value = (String) new NBTItemStack(entry.getValue().getItem(0)).get("a", String.class);
						if(!value.equalsIgnoreCase("")){
							List<String> list = map.containsKey(value) ? map.get(value) : new ArrayList<>();
							list.add(entry.getKey());
							map.put(value, list);
						}
					}

					String names = null;

					Bukkit.broadcastMessage("");
					int total = map.values().stream().mapToInt(i -> i.size()).sum();

					for(Player entry : state.allPlayers){
						String name = entry.getName();
						if(!state.isAlive(entry)) continue;
						if(vote(name, map, total)) names = name;
					}

					vote("kip", map, total);
					Bukkit.broadcastMessage("");

					if(names != null){
						Player player = Bukkit.getPlayerExact(names);
						if(player != null){
							quit(player);
							player.setGameMode(GameMode.CREATIVE);
						}
					}else{
						Bukkit.broadcastMessage(MessageList.MESSAGE_PLAYER_NO_ELIMINATED.toString());
					}

				}else{
					ItemMeta meta = sign.getItemMeta();
					meta.setDisplayName("§e>§a§l "+i+" seconds.");
					sign.setItemMeta(meta);
					meetingsMap.values().forEach(i -> i.setItem(31, sign));
				}
			}
		}.runTaskTimer(Main.getInstance(), 20, 20);
	}

	private boolean vote(String name, Map<String, List<String>> map, int total) {
		boolean b = false;
		List<String> voted = map.get(name);
		boolean vnull = voted == null;
		int size = vnull ? 0 : voted.size();
		b = size*2 > total;

		Bukkit.broadcastMessage(MessageList.VOTE_RESULT.toString()
				.replace("%name%", name.equalsIgnoreCase("kip") ? "§e§lSkip" : name)
				.replace("%count%", String.valueOf(size))
				.replace("%voted%", vnull ? "*" : String.join(", ", voted)));
		return b;
	}

	private void tpAll() {

		int[][] locations = new int[][]{{0, -4}, {2, -3}, {4, -1}, {4, 1}, {2, 3}, {0, 4}, {-2, 3},
			{-4, 1}, {-4, -1}, {-2, -3}};

			Location mainLoc = new Converter().toLocation(MessageList.CENTER_LOCATION.toString()).clone().add(0.5, 0, 0.5);
			for(int i = 0; i < this.state.allPlayers.size(); i++){
				Player player = this.state.allPlayers.get(i);
				if(player.isOnline() && this.state.isAlive(player)){
					player.teleport(mainLoc.clone().add(locations[i][0], 0, locations[i][1]));
				}
			}
			Chunk c = mainLoc.getChunk();
			mainLoc.getWorld().refreshChunk(c.getX(), c.getZ());

	}

	public boolean isMeeting() {
		return meeting;
	}

	public boolean join(Player player) {
		int size = this.state.getCrewmates().size();
		if(size == 10 || this.state.isProgress()) return false;
		this.state.addPlayer(player);
		if(size+1 == MessageList.MIN_PLAYERS.toInt()){
			timer = 1;
		}
		return true;
	}

	public boolean quit(Player player) {
		if(this.state.isProgress()){
			boolean impo = this.state.getImpostors().contains(player);
			Bukkit.broadcastMessage(MessageList.MESSAGE_PLAYER_ELIMINATED.toString()
					.replace("%name%", player.getName())
					.replace("%color%", "§"+(impo ? "c" : "b"))
					.replace("%rank%", impo ? MessageList.IMPOSTORS_NAME.toString() : MessageList.CREWMATES_NAME.toString()));
			this.state.removePlayer(player);
			int impostorsCount = this.state.getImpostors().size(),
					crewmatesCount = this.state.getCrewmates().size();
			if(impostorsCount == 0){
				win(true);
			}else if(crewmatesCount <= impostorsCount){
				win(false);
			}else{

			}
		}else{
			this.state.removePlayer(player);
			if(this.state.getCrewmates().size() == MessageList.MIN_PLAYERS.toInt()-1 && timer != -1){
				timer = -1;
				Bukkit.broadcastMessage(MessageList.TIMER_CANCELLED.toString());
			}
		}
		return false;
	}

	public void attack(Player impostor, Player crewmates) {
		if(state.getImpostors().contains(impostor) && state.getCrewmates().contains(crewmates)){
			ItemStack item = impostor.getItemInHand();
			if(item != null && item.getType().equals(Material.DIAMOND_SWORD)){
				crewmates.setGameMode(GameMode.SPECTATOR);
				impostor.setItemInHand(this.sword);
			}
		}
	}

	public void win(boolean isCrewmate) {
		this.state.progress = false;
		new BukkitRunnable(){
			PotionEffect blindness = new PotionEffect(PotionEffectType.BLINDNESS, 60, 0);

			@Override
			public void run() {
				Bukkit.broadcastMessage(MessageList.MESSAGE_WON.toString().replace("%rank%", isCrewmate ? "crewmates" : "impostors"));
				for(Player pla : Bukkit.getOnlinePlayers()){
					pla.addPotionEffect(blindness);
					pla.setGameMode(GameMode.SPECTATOR);
				}

				new BukkitRunnable() {

					@Override
					public void run() {
						//Bukkit.getOnlinePlayers().forEach(p -> p.kickPlayer(MessageList.KICK_GAME_FINISH.toString()));
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "plugman reload AmongUS");
					}
				}.runTaskLater(main, 60);

			}

		}.runTaskLater(this.main, 40);

	}

	public int getEmergencyTimer() {
		return this.emergencyTime;
	}

	public GameState getState() {
		return state;
	}

	public ColorGetter getColors() {
		return colors;
	}

	public class GameState{

		private boolean progress;
		private List<Player> crewmates, impostors, allPlayers;

		public GameState() {
			progress = false;
			crewmates = new ArrayList<>();
			impostors = new ArrayList<>();
			this.allPlayers = new ArrayList<>();
		}

		public boolean isProgress() {
			return progress;
		}

		/*
		 * If game is not in progress -> Return all players.
		 */
		public List<Player> getCrewmates() {
			return crewmates;
		}

		/*
		 * If game is not in progress -> Return a empty list.
		 */
		public List<Player> getImpostors() {
			return impostors;
		}

		public boolean isAlive(Player player){
			return crewmates.contains(player) || impostors.contains(player);
		}

		private void addPlayer(Player player) {
			this.crewmates.add(player);
			this.allPlayers.add(player);
		}

		private void removePlayer(Player player) {
			this.crewmates.remove(player);
			this.impostors.remove(player);
			if(!isProgress()) this.allPlayers.remove(player);
		}

	}

}
