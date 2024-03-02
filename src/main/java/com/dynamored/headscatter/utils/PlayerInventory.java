package com.dynamored.headscatter.utils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PlayerInventory {
	public static int getEmptyInventorySlots(Inventory inventory) {
        int emptySlots = 0;
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null)
                emptySlots++;
        }
        return emptySlots;
    }
}
