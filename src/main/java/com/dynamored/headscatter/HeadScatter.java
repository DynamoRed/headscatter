package com.dynamored.headscatter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.dynamored.headscatter.commands.HeadCompassCommand;
import com.dynamored.headscatter.commands.HeadsCommand;
import com.dynamored.headscatter.commands.TopCommand;
import com.dynamored.headscatter.events.BreakEvent;
import com.dynamored.headscatter.events.MoveEvent;
import com.dynamored.headscatter.events.PlaceEvent;
import com.dynamored.headscatter.utils.Config;
import com.dynamored.headscatter.utils.ExcludedZone;
import com.dynamored.headscatter.utils.Head;
import com.dynamored.headscatter.utils.HeadPossibility;
import com.dynamored.headscatter.utils.PlayerScore;

import net.md_5.bungee.api.ChatColor;

public class HeadScatter extends JavaPlugin {

	public List<File> files = new ArrayList<>();
	public final String prefix = "§7[§9HeadScatter™§7] ";
	public final String consolePrefix = "&9[&bdHeadScatter&9] ";
	public File headsFile;
	public YamlConfiguration headsConfig;
	public File positionsFile;
	public YamlConfiguration positionsConfig;
	public File scoresFile;
	public YamlConfiguration scoresConfig;

	public Config config = new Config();

	public HashMap<UUID, Head> heads = new HashMap<>();
	public List<HeadPossibility> headPossibilities = new ArrayList<>();
	public NamespacedKey nbt;
	public HashMap<UUID, Location> playersNearestHeads = new HashMap<>();
	public HashMap<UUID, PlayerScore> playersScores = new HashMap<>();

	private static HeadScatter instance;
	public static HeadScatter getInstance() {
        return instance;
    }

	public void colorStr(String string) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', this.consolePrefix + string));
    }

    public void onEnable() {
		colorStr("&aStarting up...");
		instance = this;
		this.nbt = new NamespacedKey(instance, "dheadscatter");

		_setupFiles();
		_setupEvents();
		_setupCommands();
		colorStr("&eFiles, commands and event setup successfully!");

		_setupLists();
		colorStr("&ePositions restored successfully!");
    }

    public void onDisable() {
		colorStr("&cThe plugin is no longer operational.");
		Bukkit.getScheduler().cancelTasks(this);
    }

	private void _setupLists() {
		if (this.headsConfig != null) {
			ConfigurationSection section = this.headsConfig.getConfigurationSection("heads");
			if (section != null) {
				for(String id : section.getKeys(false)) {
					String name = section.getConfigurationSection(id).getString("name");
					String lore = section.getConfigurationSection(id).getString("lore");
					String texture = section.getConfigurationSection(id).getString("texture");
					double rarity = section.getConfigurationSection(id).getDouble("rarity");
					String owner = section.getConfigurationSection(id).getString("owner");

					if (owner != null) this.headPossibilities.add(new HeadPossibility(texture, name, lore, rarity, UUID.fromString(owner)));
					else this.headPossibilities.add(new HeadPossibility(texture, name, lore, rarity));
				}
			}
		}

		if (this.positionsConfig != null) {
			ConfigurationSection section = this.positionsConfig.getConfigurationSection("positions");
			if (section != null) {
				for(String uuid : section.getKeys(false)) {
					String world = section.getConfigurationSection(uuid).getString("world");
					int x = section.getConfigurationSection(uuid).getInt("x");
					int y = section.getConfigurationSection(uuid).getInt("y");
					int z = section.getConfigurationSection(uuid).getInt("z");

					String name = section.getConfigurationSection(uuid).getString("name");
					String lore = section.getConfigurationSection(uuid).getString("lore");
					String texture = section.getConfigurationSection(uuid).getString("texture");
					double rarity = section.getConfigurationSection(uuid).getDouble("rarity");
					String owner = section.getConfigurationSection(uuid).getString("owner");

					if (owner != null) this.heads.put(UUID.fromString(uuid), new Head(UUID.fromString(uuid), texture, new Location(Bukkit.getWorld(world), x, y, z), name, lore, rarity, UUID.fromString(owner)));
					else this.heads.put(UUID.fromString(uuid), new Head(UUID.fromString(uuid), texture, new Location(Bukkit.getWorld(world), x, y, z), name, lore, rarity, null));
				}
			}
		}

		if (this.scoresConfig != null) {
			ConfigurationSection section = this.scoresConfig.getConfigurationSection("scores");
			if (section != null) {
				for(String uuid : section.getKeys(false)) {
					int score = section.getConfigurationSection(uuid).getInt("score");
					Player player = Bukkit.getPlayer(UUID.fromString(uuid));

					if (player != null) this.playersScores.put(UUID.fromString(uuid), new PlayerScore(player, score));
				}
			}
		}

		ConfigurationSection section = getConfig().getConfigurationSection("scatter");
		if (section != null) {
			String world = section.getString("world");
			int radius = section.getInt("radius");
			int maximumPerZone = section.getInt("maximumPerZone");
			int oneOver = section.getInt("oneOver");
			int step = section.getInt("step");

			this.config.world = world;
			this.config.radius = radius;
			this.config.maximumPerZone = maximumPerZone;
			this.config.oneOver = oneOver;
			this.config.step = step;

			ConfigurationSection excludedZonesSection = section.getConfigurationSection("excludedZones");

			for(String excludedZoneId : excludedZonesSection.getKeys(false)) {
				double x1 = excludedZonesSection.getConfigurationSection(excludedZoneId).getInt("x1");
				double z1 = excludedZonesSection.getConfigurationSection(excludedZoneId).getInt("z1");
				double x2 = excludedZonesSection.getConfigurationSection(excludedZoneId).getInt("x2");
				double z2 = excludedZonesSection.getConfigurationSection(excludedZoneId).getInt("z2");

				this.config.excludedZones.add(new ExcludedZone(new Location(Bukkit.getWorld(world), x1, 0d, z1), new Location(Bukkit.getWorld(world), x2, 0d, z2)));
			}
		}
	}

	private void _setupFiles() {
		saveDefaultConfig();
		saveResource("heads.yml", false);

		this.headsFile = new File(this.getDataFolder(), "heads.yml");
		files.add(this.headsFile);
		this.headsConfig = YamlConfiguration.loadConfiguration(headsFile);

		this.positionsFile = new File(this.getDataFolder(), "DO-NOT-TOUCH-Positions.yml");
		files.add(this.positionsFile);
		this.positionsConfig = YamlConfiguration.loadConfiguration(positionsFile);

		this.scoresFile = new File(this.getDataFolder(), "DO-NOT-TOUCH-Scores.yml");
		files.add(this.scoresFile);
		this.scoresConfig = YamlConfiguration.loadConfiguration(scoresFile);
	}

	private void _setupEvents() {
		getServer().getPluginManager().registerEvents(new MoveEvent(), this);
		getServer().getPluginManager().registerEvents(new BreakEvent(), this);
		getServer().getPluginManager().registerEvents(new PlaceEvent(), this);
	}

	private void _setupCommands() {
		getCommand("headscattercompass").setExecutor(new HeadCompassCommand());
		getCommand("headscattertop").setExecutor(new TopCommand());
		getCommand("headscattergetall").setExecutor(new HeadsCommand());
	}
}
