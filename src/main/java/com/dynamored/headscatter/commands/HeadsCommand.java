package com.dynamored.headscatter.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import com.dynamored.headscatter.HeadScatter;
import com.dynamored.headscatter.utils.HeadPossibility;
import com.dynamored.headscatter.utils.PlayerInventory;

public class HeadsCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		List<HeadPossibility> possibilities = HeadScatter.getInstance().headPossibilities;
		List<ItemStack> shulkers = new ArrayList<>();

		int shulkerSize = 27;
		int shulkersCount = possibilities.size()/shulkerSize;
		if (possibilities.size()%shulkerSize != 0) shulkersCount++;

		for (int i = 0; i < shulkersCount; i++) {
			ItemStack shulkerBox = new ItemStack(Material.WHITE_SHULKER_BOX, 1);
			BlockStateMeta meta = (BlockStateMeta) shulkerBox.getItemMeta();

			ShulkerBox shulker = (ShulkerBox) meta.getBlockState();

			for (int y = shulkerSize*i; y < shulkerSize*(i+1); y++) {
				if (y >= possibilities.size()) break;
				shulker.getInventory().addItem(possibilities.get(y).getHead());
			}

			meta.setDisplayName("§9Têtes #" + (i+1));
			meta.setBlockState(shulker);
			shulkerBox.setItemMeta(meta);
			shulkers.add(shulkerBox);
		}

		if (sender instanceof Player){
			Player player = (Player) sender;

			if (PlayerInventory.getEmptyInventorySlots(player.getInventory()) >= shulkersCount)
				for (ItemStack shulker : shulkers)
					player.getInventory().addItem(shulker);
			else {
				sender.sendMessage(HeadScatter.getInstance().prefix + "§7[§c!§7] Not enought space in inventory");
				return false;
			}
		} else {
			sender.sendMessage(HeadScatter.getInstance().prefix + "§7[§c!§7] Reserved to players");
			return false;
		}

		return true;
	}
}
