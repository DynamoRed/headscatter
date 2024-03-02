package com.dynamored.headscatter.utils;

import java.util.List;

import org.bukkit.Location;

public class ExcludedZone {
	public Location corner1;
	public Location corner2;

	public ExcludedZone(Location corner1, Location corner2) {
		this.corner1 = corner1;
		this.corner2 = corner2;
	}

	public boolean has(Location location) {
		Location minLocation = new Location(
			this.corner1.getWorld(),
			Math.min(this.corner1.getBlockX(), this.corner2.getBlockX()),
			Math.min(this.corner1.getBlockY(), this.corner2.getBlockY()),
			Math.min(this.corner1.getBlockZ(), this.corner2.getBlockZ())
		);
		Location maxLocation = new Location(
			this.corner1.getWorld(),
			Math.max(this.corner1.getBlockX(), this.corner2.getBlockX()),
			Math.max(this.corner1.getBlockY(), this.corner2.getBlockY()),
			Math.max(this.corner1.getBlockZ(), this.corner2.getBlockZ())
		);

		return (
			minLocation.getX() <= location.getX()
			&& minLocation.getZ() <= location.getZ()
			&& maxLocation.getX() >= location.getX()
			&& maxLocation.getZ() >= location.getZ()
		);
	}

	public static boolean isInOneZone(List<ExcludedZone> zones, Location location) {
		for (ExcludedZone zone : zones) {
			if (zone.has(location)) return true;
		}
		return false;
	}
}
