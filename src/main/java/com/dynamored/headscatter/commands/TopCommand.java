package com.dynamored.headscatter.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import com.dynamored.headscatter.HeadScatter;
import com.dynamored.headscatter.utils.PlayerScore;

public class TopCommand implements TabExecutor {

	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 1) {
			List<String> counters = new ArrayList<>();

			for (int i = 2; i < 10; i++)
				counters.add(String.valueOf(i));

			return counters;
		}

		return Collections.emptyList();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		LinkedHashMap<UUID, PlayerScore> sortedScores = new LinkedHashMap<>();
		HashMap<UUID, PlayerScore> playersScores = HeadScatter.getInstance().playersScores;
		ArrayList<PlayerScore> scores = new ArrayList<>();

		for (Map.Entry<UUID, PlayerScore> entry : playersScores.entrySet())
            scores.add(entry.getValue());

        Collections.sort(scores, new Comparator<PlayerScore>() {
            public int compare(PlayerScore ps1, PlayerScore ps2) {
                return ps1.score - ps2.score;
            }
        });

        for (PlayerScore ps : scores)
            for (Entry<UUID, PlayerScore> entry : playersScores.entrySet())
                if (entry.getValue().equals(ps))
                    sortedScores.put(entry.getKey(), ps);

		int page = 1;
		int perPage = 10;
		if (args.length == 1) {
			try {
				page = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				sender.sendMessage(HeadScatter.getInstance().prefix + "§7[§c!§7] Invalid page number");
				return false;
			}
		}

		sender.sendMessage("§7§l✦------------❁------------§7§l✦", "", "§7 |  §9HeadScatter™ §7Top §7(Page " + page + ")", "", "§7§l✦------------❁------------§7§l✦", "");

		ArrayList<Map.Entry<UUID, PlayerScore>> list = new ArrayList<>(sortedScores.entrySet());

		int total = 0;
		for (int i = perPage*(page-1); i < perPage*page; i++) {
			Map.Entry<UUID, PlayerScore> entryAtIndex;

			try {
				entryAtIndex = list.get(i);

				PlayerScore ps = entryAtIndex.getValue();

				if (i == 0) sender.sendMessage("§7 ▸ §f" + ps.player.getDisplayName() + "§7: §c" + ps.score);
				else if (i == 1) sender.sendMessage("§7 ▸ §f" + ps.player.getDisplayName() + "§7: §6" + ps.score);
				else if (i == 2) sender.sendMessage("§7 ▸ §f" + ps.player.getDisplayName() + "§7: §b" + ps.score);
				else sender.sendMessage("§7 ▸ §f" + ps.player.getDisplayName() + "§7: " + ps.score);

				total++;
			} catch (IndexOutOfBoundsException e) {}
		}

		if (total == 0) sender.sendMessage("§7§o   No scores...");

		sender.sendMessage("");

		return true;
	}
}
