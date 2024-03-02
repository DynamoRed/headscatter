package com.dynamored.headscatter.events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.dynamored.headscatter.HeadScatter;
import com.dynamored.headscatter.utils.Fireworks;
import com.dynamored.headscatter.utils.Head;
import com.dynamored.headscatter.utils.PlayerScore;

public class BreakEvent implements Listener {

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Location location = event.getBlock().getLocation();

		if (event.getBlock().getType() == Material.PLAYER_HEAD) {
			Head head = Head.getHeadByLocation(location);
			if (head != null) {
				event.setDropItems(false);
				event.setExpToDrop(50);
				Fireworks.summon(location);
				head.drop();
				PlayerScore score = HeadScatter.getInstance().playersScores.get(event.getPlayer().getUniqueId());
				if (score != null) score.increment();
				else new PlayerScore(event.getPlayer(), 1).save();
			}
		}
	}
}
