package fr.Toze.amongus.utils;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_8_R3.PlayerConnection;

public class Titles {

	PacketPlayOutTitle title = null;
	PacketPlayOutTitle subtitle = null;
	PacketPlayOutTitle times = null;

	public Titles title(String title) {
		IChatBaseComponent comp = ChatSerializer.a("{text:\"" + ChatColor.translateAlternateColorCodes('&', title) + "\"}");
		this.title = new PacketPlayOutTitle(EnumTitleAction.TITLE, comp);
		return this;
	}

	public Titles subtitle(String subtitle) {
		IChatBaseComponent comp = ChatSerializer.a("{text:\"" + ChatColor.translateAlternateColorCodes('&', subtitle) + "\"}");
		this.subtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, comp);
		return this;
	}

	public Titles times(int fadeIn, int displayTime, int fadeOut) {
		times = new PacketPlayOutTitle(fadeIn, displayTime, fadeOut);
		return this;
	}

	public void send(Player player) {
		if(title != null){
			PlayerConnection con = ((CraftPlayer) player).getHandle().playerConnection;
			con.sendPacket(title);
			if(subtitle != null) con.sendPacket(subtitle);
			if(times != null) con.sendPacket(subtitle);
		}
	}

	public void send(List<Player> player) {
		if(title != null){
			for(Player pla : player){
				PlayerConnection con = ((CraftPlayer) pla).getHandle().playerConnection;
				con.sendPacket(title);
				if(subtitle != null) con.sendPacket(subtitle);
				if(times != null) con.sendPacket(subtitle);
			}
		}
	}

}	