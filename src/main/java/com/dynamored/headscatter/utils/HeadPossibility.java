package com.dynamored.headscatter.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import com.dynamored.headscatter.HeadScatter;

public class HeadPossibility {

	public String texture;
	public String name;
	public String lore;
	public double rarity;
	public UUID owner;

	public HeadPossibility(String texture, String name, String lore, double rarity) {
		this.texture = texture;
		this.name = name;
		this.lore = lore;
		this.rarity = rarity;
	}

	public HeadPossibility(String texture, String name, String lore, double rarity, UUID owner) {
		this.texture = texture;
		this.name = name;
		this.lore = lore;
		this.rarity = rarity;
		this.owner = owner;
	}

	public ItemStack getHead() {
		final ItemStack head = new ItemStack(Material.PLAYER_HEAD);
		final SkullMeta meta = (SkullMeta) head.getItemMeta();

		if (this.owner != null) meta.setOwningPlayer(Bukkit.getOfflinePlayer(this.owner));
		else _setBase64ToSkullMeta(this.texture, meta);

		meta.setDisplayName(this.name);
		if (this.name.startsWith("§b#HS")) meta.setLore(this.splitLore(this.lore, "§c"));
		else meta.setLore(this.splitLore(this.lore, "§e"));
		meta.getPersistentDataContainer().set(HeadScatter.getInstance().nbt, PersistentDataType.BOOLEAN, true);

		head.setItemMeta(meta);
		return head;
	}

	private PlayerProfile _getProfileBase64(String base64) {
		PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
		PlayerTextures textures = profile.getTextures();
		URL urlObject;
		try {
			urlObject = _getUrlFromBase64(base64);
		} catch (MalformedURLException exception) {
			throw new RuntimeException("Invalid URL", exception);
		}
		textures.setSkin(urlObject);
		profile.setTextures(textures);
		return profile;
	}

	private URL _getUrlFromBase64(String base64) throws MalformedURLException {
		String decoded = new String(Base64.getDecoder().decode(base64));
		return new URL(decoded.substring("{\"textures\":{\"SKIN\":{\"url\":\"".length(), decoded.length() - "\"}}}".length()));
	}

	private void _setBase64ToSkullMeta(String base64, SkullMeta meta) {
		PlayerProfile profile = _getProfileBase64(base64);
		meta.setOwnerProfile(profile);
	}

	private List<String> splitLore(String lore, String lineColor) {
		int lineLimit = 50;
        List<String> segments = new ArrayList<>();
        segments.add("");
        String[] lines = lore.split("<line>");

        for (String line : lines) {
            int start = 0;
            while (start < line.length()) {
                int end = Math.min(line.length(), start + lineLimit);
                if (end < line.length())
                    while (end > start && Character.isLetterOrDigit(line.charAt(end - 1)))
                        end--;

                if (end > start) {
                    segments.add(lineColor + line.substring(start, end));
                    start = end;
                } else
                    start++;
            }
        }

		if (segments.size() > 1) {
			segments.set(1, "§8“" + segments.get(1));
			int lastIndex = segments.size() - 1;
			String lastSegment = segments.get(lastIndex);
			segments.set(lastIndex, lastSegment + "§8„");
		}

        return segments;
    }
}
