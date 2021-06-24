package fr.Toze.amongus.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;

public class VisualEntity{

	private LivingEntity living;

	public VisualEntity(Player player, Location location, EntityType type) {
		living = (LivingEntity) location.getWorld().spawnEntity(location, type);
		Bukkit.getOnlinePlayers().stream().filter(
				p -> !p.equals(player)).forEach(
						p -> ((CraftPlayer) p).getHandle().playerConnection.sendPacket(
								new PacketPlayOutEntityDestroy(living.getEntityId())));
	}

	public LivingEntity getEntity() {
		return living;
	}
	
}
