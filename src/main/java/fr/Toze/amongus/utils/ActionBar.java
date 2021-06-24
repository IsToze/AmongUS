package fr.Toze.amongus.utils;

import java.util.Arrays;
import java.util.List;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;

public class ActionBar {

	public void send(Player player, String message){
       send(Arrays.asList(player), message);
    }
	
	public void send(List<Player> player, String message){
        IChatBaseComponent cbc = ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
        player.forEach(p -> ((CraftPlayer) p).getHandle().playerConnection.sendPacket(ppoc));
    }
	
	
	public void remove(Player player){
        CraftPlayer p = (CraftPlayer) player;
        IChatBaseComponent cbc = ChatSerializer.a("");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc,(byte) 2);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(ppoc);
    }
	
	
}
