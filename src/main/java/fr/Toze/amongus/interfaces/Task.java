package fr.Toze.amongus.interfaces;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.Toze.amongus.enums.MessageList;
import fr.Toze.amongus.manager.TaskManager.TaskPlayerState;
import fr.Toze.amongus.utils.Converter;
import fr.Toze.amongus.utils.Items;

public abstract class Task {

	public abstract TaskDuration getDuration();
	public abstract ItemStack[] getContents(Items items);
	public abstract void use(Player player, Inventory inventory, TaskPlayerState state);
	public abstract void click(Inventory inventory, Player player, int slot, ItemStack current, ClickType type, TaskPlayerState state);
	
	public String getRules() {
		return MessageList.valueOf("TASK_"+getName()+"_RULES").toString();
	}
	
	public Location getLocation() {
		return new Converter().toLocation(MessageList.valueOf("TASK_"+getName()+"_LOCATION").toString());
	}
	
	public String getName() {
		return this.getClass().asSubclass(this.getClass()).getSimpleName();
	}

	public enum TaskDuration{

		SHORT(),
		MEDIUM(),
		LONG();

		public String getToLanguage(){ return MessageList.valueOf(name()).toString();}

	}
	
	public enum TaskState{
		CANCELED(),
		FAILED(),
		SUCCEEDED();
	}

}
