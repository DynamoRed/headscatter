package com.dynamored.headscatter.utils;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class Fireworks {
	private static List<List<Color>> fireworksCombinaisons = Arrays.asList(
		Arrays.asList(Color.RED, Color.ORANGE, Color.YELLOW),
		Arrays.asList(Color.BLUE, Color.AQUA, Color.NAVY, Color.TEAL),
		Arrays.asList(Color.FUCHSIA, Color.PURPLE),
		Arrays.asList(Color.GREEN, Color.LIME)
	);

	public static void summon(Location location) {
        Firework firework = location.getWorld().spawn(location.add(0.5, 0.5, 0.5), Firework.class);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();

        FireworkEffect effect = FireworkEffect.builder()
                .with(FireworkEffect.Type.BALL_LARGE)
                .withColor(fireworksCombinaisons.get(Maths.random(0, fireworksCombinaisons.size(), true)))
                .withFade(Color.WHITE, Color.BLACK, Color.SILVER)
                .withFlicker()
                .withTrail()
                .build();

        fireworkMeta.addEffect(effect);
        fireworkMeta.setPower(2);

        firework.setFireworkMeta(fireworkMeta);
    }
}
