package com.dynamored.headscatter.utils;

import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.dynamored.headscatter.HeadScatter;

public class PlayerScore {
	public int score = 0;
	public Player player;

	public PlayerScore(Player player) {
		this.player = player;
	}

	public PlayerScore(Player player, int score) {
		this.player = player;
		this.score = score;
	}

	public void increment() {
		this.score++;
		this.save();
	}

	public void save() {
		YamlConfiguration scoresConfig = HeadScatter.getInstance().scoresConfig;
		scoresConfig.set("scores." + this.player.getUniqueId() + ".score", this.score);

		try {
			scoresConfig.save(HeadScatter.getInstance().scoresFile);
			HeadScatter.getInstance().playersScores.put(this.player.getUniqueId(), this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
