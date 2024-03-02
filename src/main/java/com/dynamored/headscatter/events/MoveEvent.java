package com.dynamored.headscatter.events;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.dynamored.headscatter.HeadScatter;
import com.dynamored.headscatter.utils.ExcludedZone;
import com.dynamored.headscatter.utils.Head;
import com.dynamored.headscatter.utils.HeadCompass;
import com.dynamored.headscatter.utils.HeadPossibility;
import com.dynamored.headscatter.utils.Maths;

public class MoveEvent implements Listener {

	public HashMap<UUID, Location> playerLatestPositions = new HashMap<>();

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Location actual = player.getLocation().getBlock().getLocation();

		if (!player.isValid() || player.isDead() || !actual.getWorld().getName().equalsIgnoreCase(HeadScatter.getInstance().config.world)) return;

		if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) return;

		Location last = this.playerLatestPositions.get(player.getUniqueId());

		Head nearest = this._getNearest(actual);
		if (nearest != null) {
			HeadScatter.getInstance().playersNearestHeads.put(player.getUniqueId(), nearest.location);
			HeadCompass.setTarget(player, nearest.location);
		}

		if (last == null) this.playerLatestPositions.put(player.getUniqueId(), actual);
		else if (!ExcludedZone.isInOneZone(HeadScatter.getInstance().config.excludedZones, actual) && last.distance(actual) >= HeadScatter.getInstance().config.step) {
			this.playerLatestPositions.put(player.getUniqueId(), actual);

			int chance = Maths.random(0, HeadScatter.getInstance().config.oneOver, true);
			if (chance != 0 || this._headArounds(actual) > HeadScatter.getInstance().config.maximumPerZone) return;

			Location headSpawn = this._randomLocation(actual);
			while (headSpawn.getBlock().getType() == Material.WATER || ExcludedZone.isInOneZone(HeadScatter.getInstance().config.excludedZones, headSpawn)) headSpawn = this._randomLocation(actual);
			while (headSpawn.getBlock().getType() != Material.AIR && headSpawn.getBlock().getType() != Material.GRASS) headSpawn.add(0, 1, 0);

			HeadPossibility possibility = this._randomHead();
			Head head = new Head(possibility.texture, headSpawn, possibility.name, possibility.lore, possibility.rarity, possibility.owner);

			head.spawn();
			head.save();

			player.playSound(player, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, .2f, 1f);
			player.sendMessage(HeadScatter.getInstance().prefix + "§7Un personnage ou objet d'une autre dimension vient d'être téléporté à proximité de vous...");
		}
	}

	private Location _randomLocation(Location reference) {
		int radius = HeadScatter.getInstance().config.radius;
		int randomX = Maths.random(radius*-1, radius) + reference.getBlockX();
		int randomZ = Maths.random(radius*-1, radius) + reference.getBlockZ();

		return new Location(reference.getWorld(), randomX, reference.getWorld().getHighestBlockYAt(randomX, randomZ), randomZ);
	}

	private HeadPossibility _randomHead() {
		List<HeadPossibility> list = HeadScatter.getInstance().headPossibilities;

		double totalWeight = 0.0;
		for (HeadPossibility possibility : list)
			totalWeight += possibility.rarity;

		int idx = 0;
		for (double r = Math.random() * totalWeight; idx < list.size() - 1; ++idx) {
			r -= list.get(idx).rarity;
			if (r <= 0.0) break;
		}
		return list.get(idx);
    }

	private int _headArounds(Location center) {
		int counter = 0;

		for (Head head : HeadScatter.getInstance().heads.values()) {
			if (head.location.distance(center) <= HeadScatter.getInstance().config.radius) counter++;
		}

		return counter;
	}

	private Head _getNearest(Location center) {
		Head nearest = null;
		Collection<Head> heads = HeadScatter.getInstance().heads.values();
		double minDistance = -1;

		for (Head head : heads) {
			if (minDistance == -1 || minDistance > center.distance(head.location)) {
				minDistance = center.distance(head.location);
				nearest = head;
			}
		}

		return nearest;
	}
}
