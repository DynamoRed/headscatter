package com.dynamored.headscatter.utils;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.configuration.file.YamlConfiguration;

import com.dynamored.headscatter.HeadScatter;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import java.io.IOException;
import java.lang.reflect.Field;

public class Head extends HeadPossibility {

	public UUID uuid;
	public Location location;

	public Head(String texture, Location location, String name, String lore, double rarity, UUID owner) {
		super(texture, name, lore, rarity, owner);
		this.uuid = UUID.randomUUID();
		this.location = location;
	}

	public Head(UUID uuid, String texture, Location location, String name, String lore, double rarity, UUID owner) {
		super(texture, name, lore, rarity, owner);
		this.uuid = uuid;
		this.location = location;
	}

	public void spawn() {
		this.setHeadTexture(this.location, this.uuid, this.texture);
	}

	public void drop() {
		this.location.getWorld().dropItemNaturally(this.location, this.getHead());
		this.remove();
	}

	public void save() {
		YamlConfiguration posConfig = HeadScatter.getInstance().positionsConfig;
		posConfig.set("positions." + this.uuid + ".x", this.location.getBlockX());
		posConfig.set("positions." + this.uuid + ".y", this.location.getBlockY());
		posConfig.set("positions." + this.uuid + ".z", this.location.getBlockZ());
		posConfig.set("positions." + this.uuid + ".world", this.location.getWorld().getName());
		posConfig.set("positions." + this.uuid + ".name", this.name);
		posConfig.set("positions." + this.uuid + ".lore", this.lore);
		posConfig.set("positions." + this.uuid + ".texture", this.texture);

		try {
			posConfig.save(HeadScatter.getInstance().positionsFile);
			HeadScatter.getInstance().heads.put(this.uuid, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void remove() {
		HeadScatter.getInstance().positionsConfig.set("positions." + this.uuid, null);

		try {
			HeadScatter.getInstance().positionsConfig.save(HeadScatter.getInstance().positionsFile);
			HeadScatter.getInstance().heads.remove(this.uuid);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Head getHeadByLocation(Location location) {
		for (Head head : HeadScatter.getInstance().heads.values()) {
			if (head.location.getBlockX() == location.getBlockX() && head.location.getBlockY() == location.getBlockY() && head.location.getBlockZ() == location.getBlockZ())
				return head;
		}
		return null;
	}

	public void setHeadTexture(Location location, UUID uuid, String base64) {
        try {
            Block block = location.getBlock();
            block.setType(Material.PLAYER_HEAD);

            org.bukkit.block.Skull skullBlock = (Skull) block.getState();

			if (this.owner != null) skullBlock.setOwningPlayer(Bukkit.getOfflinePlayer(this.owner));
			else {
				GameProfile profile = new GameProfile(uuid, null);
				profile.getProperties().put("textures", new Property("textures", base64));

				Field profileField = skullBlock.getClass().getDeclaredField("profile");
				profileField.setAccessible(true);
				profileField.set(skullBlock, profile);
			}

            skullBlock.update();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
