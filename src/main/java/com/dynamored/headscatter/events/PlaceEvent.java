package com.dynamored.headscatter.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.dynamored.headscatter.HeadScatter;

public class PlaceEvent implements Listener {

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		ItemStack item = event.getItemInHand();
		PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();

		if (container.has(HeadScatter.getInstance().nbt, PersistentDataType.BOOLEAN) && container.get(HeadScatter.getInstance().nbt, PersistentDataType.BOOLEAN)) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(HeadScatter.getInstance().prefix + "§7[§c!§7] You cannot place this");
		}
	}
}
