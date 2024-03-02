package com.dynamored.headscatter.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.dynamored.headscatter.HeadScatter;
import com.dynamored.headscatter.utils.HeadCompass;

public class HeadCompassCommand implements TabExecutor {

	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 1) {
			List<String> playerNames = new ArrayList<>();
			Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().size()];

			Bukkit.getServer().getOnlinePlayers().toArray(players);

			for (int i = 0; i < players.length; i++)
				playerNames.add(players[i].getName());

			return playerNames;
		}

		return Collections.emptyList();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 1) {
			Player target = Bukkit.getPlayer(args[0]);

			if (target != null && target.isValid() && !target.isDead())
				target.getInventory().addItem(HeadCompass.get());
			else {
				sender.sendMessage(HeadScatter.getInstance().prefix + "§7[§c!§7] Cannot find this player");
				return false;
			}
		} else {
			if (sender instanceof Player){
				Player player = (Player) sender;
				player.getInventory().addItem(HeadCompass.get());
			} else {
				sender.sendMessage(HeadScatter.getInstance().prefix + "§7[§c!§7] Reserved to players");
				return false;
			}
		}
		return true;
	}
}
