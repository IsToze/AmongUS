package fr.Toze.amongus.enums;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public enum MessageList {

	PLUGIN_PREFIX("&6[&eAmongUS&6]"),
	MESSAGE_HELP_FOR("&a>     &eAide pour la commande '&6%command%&e' :"),
	MESSAGE_COMMAND_ONLY_PLAYERS("&cSeul les joueurs peuvent utiliser cette commande."),
	MESSAGE_COMMAND_NO_PERMISSION("&cVous n'avez pas la permission d'utiliser cette commande."),
	MESSAGE_SUB_COMMAND_NOT_FOUND("&eLa sous-commande &6'%subname%' &en'existe pas."),
	
	MESSAGE_PLAYER_ELIMINATED("&eLe joueur &a%name% &ea �t� �limin�(e). %color%Il �tait %rank%%color%."),
	MESSAGE_PLAYER_NO_ELIMINATED("&cAucun joueur n'a �t� �limin� !"),
	
	PERMISSION_CAMERAS_COMMAND("cameras.use"), 
	MESSAGE_COMMAND_CAMERA_LIST("Affiche la liste des cam�ras disponibles"), 
	MESSAGE_COMMAND_CAMERA_CREATE("Cr�er une nouvelle camera"), 
	MESSAGE_COMMAND_CAMERA_CREATE_NO_SEE("&cVous ne regardez actuellement aucun bloc."),
	MESSAGE_COMMAND_CAMERA_CREATE_ALEARDY_EXIST("&cIl y a d�j� une cam�ra nomm�e &e%name%&c."),
	MESSAGE_COMMAND_CAMERA_CREATE_SUCCESS("&aVous venez de cr�er une nouvelle cam�ra. &e(Nom : &a%name%&e, Groupe : &a%group%&e)."),
	ARROW_CHANGE_CAMERA_NAME("&eCam�ra Suivante"), 
	MESSAGE_RIGHT_CLICK("&7Fa�tes un clique droit pour changer de cam�ra !"),
	VOTE_RESULT("&e> &b%name% : &c%count% vote(s) : &7%voted%."),
	
	PERMISSION_TASKTEST_COMMAND("tasktest.use"),
	MESSAGE_COMMAND_TASKTEST("&cUtilise -> /tasktest <Name> &7(Utilise TAB)."),
	MESSAGE_COMMAND_TASKTEST_NO_EXIST("&cLa t�che '%id%' n'existe pas."),
	
	PERMISSION_HOLOGRAM_COMMAND("hologram.use"),
	
	PERMISSION_MEETING_COMMAND("meeting.use"),
	
	PERMISSION_RANKS_COMMAND("ranks.use"),
	
	KICK_SERVER_FULL("&4La partie est d�j� compl�te ou une partie est d�j� en cours !"),
	
	MIN_PLAYERS(3),
	IMPOSTOR_COUNT(1),
	MESSAGE_WON("&eLes &c%rank% &eont gagn�s !"),
	KICK_GAME_FINISH("&7Partie termin�e ! Veuillez � nouveau rejoindre le jeu !"),
	MESSAGE_START_IN("&eLa partie d�marre dans &a%timer% &eseconde(s)."),
	TIMER_CANCELLED("&cD�marrage annul� !"),
	WALK_SPEED(2),
	
	MEETING_TIME(20),
	MEETING_CAN_CHANGE_VOTE(false),
	EMERGENCY_MEETING_TIMER_MESSAGE("&cVous devez attendre %timer% seconde(s) avant de pouvoir utiliser le bouton d'urgence !"),
	
	GROUP_CAMERA(1), //UNIQUE GROUPE OU CA RETP
	GROUP_CAMERA_BUTTON("world 0 0 0"),
	CENTER_LOCATION("world 0 0 0"),
	LOBBY_LOCATION("world 0 0 0"),
	EMERGENCY_MEETING_LOCATION("world 0 0 0"),
	EMERGENCY_MEETING_DELAY(15),
	
	MAP_MIN_X(0),
	MAP_MIN_Z(0),
	MAP_MAX_X(0),
	MAP_MAX_Z(0),
	
	IMPOSTORS_NAME("Imposteur"), 
	IMPOSTORS_GOAL("Tue tout le monde et sabote tout !"),
	IMPOSTORS_KILL_COOLDOWN(10),
	CREWMATES_NAME("�quipier"), 
	CREWMATES_GOAL("Accompli tes missions et trouve les imposteurs !"),
	MESSAGE_PLAYER_SAY_COLOR("&eVous �tes le joueur &6%color%&e."), 
	
	MESSAGE_PLAYER_COMPLETED_MISSION("&eVous avez fini(e) une mission !"),
	MESSAGE_PLAYER_CANCELLED_MISSION("&cVous avez quitt�(e) une mission !"),
	SHORT("Petite"),
	MEDIUM("Moyenne"),
	LONG("Longue"), 
	RULES_LANGUAGE("&c&lR�gles :"),
	
	TASK_S1_RULES("Relie les laines de la m�me couleur entre-elle"),
	TASK_S1_LOCATION("world 0 0 0"),
	
	TASK_S2_RULES(""),
	TASK_S2_LOCATION("world 0 0 0"),
	
	TASK_S3_RULES("Clique sur le bouton plusieurs fois pour remplir l'inventaire"),
	TASK_S3_LOCATION("world 0 0 0"),
	
	TASK_M1_RULES("Attends que le t�l�chargement se fasse.."),
	TASK_M1_LOCATION("world 0 0 0"),
	
	TASK_M2_RULES("Clique sur les nombres de 1 � 10"),
	TASK_M2_LOCATION("world 0 0 0"),
	
	TASK_L1_RULES("Reproduire � 5 reprises la forme de gauche (Chaque tour rajoute 1 case de plus � retenir)"),
	TASK_L1_SPEED(10),
	TASK_L1_LOCATION("world 0 0 0"),
	
	TASK_L2_RULES("Clique sur les laines rapidement 20 fois"),
	TASK_L2_SPEED(13), 
	TASK_L2_LOCATION("world 0 0 0"),
	
	TASK_SHORT_COUNT(2),
	TASK_MEDIUM_COUNT(1),
	TASK_LONG_COUNT(1), 
	TASK_NAME("&7T�che"),
	;
	
	private static Map<String, Object> values;
	
	public static void init(Plugin plugin) {
		MessageList.values = new HashMap<>();

		plugin.reloadConfig();
		
		FileConfiguration config = plugin.getConfig();
		for(MessageList list : MessageList.values()){
			String key = list.getKey();
			
			if(!config.contains(key)){
				Object object = list.getDefaultObject();
				config.set(list.getKey(), object);
				values.put(key, object);
			}else{
				values.put(key, config.get(key));
			}
			
		}
		
		plugin.saveConfig();

	}
	
	private Object value;
	private String key;
	
	private MessageList(Object object) {
		this.value = object;
		this.key = name().toLowerCase();
	}

	public String getKey() {
		return key;
	}
	
	public Object to() {
		return values.get(key);
	}
	
	public String toString() {
		return ChatColor.translateAlternateColorCodes('&', (String) values.get(key));
	}
	
	public int toInt() {
		return (int) values.get(key);
	}
	
	public boolean toBoolean() {
		return (boolean) values.get(key);
	}
	
	public Location toLocation(){
		String[] arrays = toString().split(" ");
		return new Location(Bukkit.getWorld(arrays[0]), Integer.parseInt(arrays[1]), 
				Integer.parseInt(arrays[2]), Integer.parseInt(arrays[3]),
				Integer.parseInt(arrays[4]), Integer.parseInt(arrays[5]));
	}
	
	public Object getDefaultObject() {
		return value;
	}
}
