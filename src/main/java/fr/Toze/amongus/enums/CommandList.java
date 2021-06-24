package fr.Toze.amongus.enums;

import fr.Toze.amongus.commands.CamerasCommand;
import fr.Toze.amongus.commands.HologramCommand;
import fr.Toze.amongus.commands.MeetingCommand;
import fr.Toze.amongus.commands.RanksCommand;
import fr.Toze.amongus.commands.TaskTestCommand;
import fr.Toze.amongus.interfaces.CompletedCommand;

public enum CommandList {

	CAMERAS(new CamerasCommand(), "Manage all Cameras."),
	HOLOGRAM(new HologramCommand(), "Create a Hologram."),
	TASKTEST(new TaskTestCommand(), "Test a Task."),
	MEETING(new MeetingCommand(), "Start a Meeting."),
	RANKS(new RanksCommand(), "Send a message with the rank of the players");
	
	private CompletedCommand command;
	private String utils;

	CommandList(CompletedCommand command, String utils){
		this.command = command;
		this.utils = utils;
	}

	public CompletedCommand getCommand() {
		return command;
	}

	public String getUtils() {
		return utils;
	}

}
