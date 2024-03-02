package com.dynamored.headscatter.utils;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.dynamored.headscatter.HeadScatter;

public class HeadCompass {
	public static ItemStack get() {
		final ItemStack compass = new ItemStack(Material.COMPASS);
		final ItemMeta meta = compass.getItemMeta();

		meta.setDisplayName("§9Radar à Têtes§8™");
		meta.setLore(Arrays.asList("", "§7§l✦------------❁------------§7§l✦", "", "  §eTraçage GPS des têtes en cours...", "  §8§oPropriété de DynamoIndustries™", "", "§7§l✦------------❁------------§7§l✦", ""));
		meta.getPersistentDataContainer().set(HeadScatter.getInstance().nbt, PersistentDataType.BOOLEAN, true);

		compass.setItemMeta(meta);
		return compass;
	}

	public static void setTarget(Player player, Location nearest) {
		if (!player.isValid() || player.isDead()) return;

		Inventory inventory = player.getInventory();
		ItemMeta exampleMeta = HeadCompass.get().getItemMeta();

		for (int i = 0; i < inventory.getSize(); i++) {
			ItemStack item = inventory.getItem(i);
			if (item != null) {
				ItemMeta meta = item.getItemMeta();
				PersistentDataContainer container = meta.getPersistentDataContainer();

				if (meta.getDisplayName().equals(exampleMeta.getDisplayName()) && container.has(HeadScatter.getInstance().nbt, PersistentDataType.BOOLEAN) && container.get(HeadScatter.getInstance().nbt, PersistentDataType.BOOLEAN) && item.getType() == Material.COMPASS) {
					CompassMeta compassMeta = (CompassMeta) meta;
					compassMeta.setLodestoneTracked(false);
					compassMeta.setLodestone(nearest);
					item.setItemMeta(compassMeta);

					inventory.setItem(i, item);
				}
			}
		}
	}
}
