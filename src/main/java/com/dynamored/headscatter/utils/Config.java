package com.dynamored.headscatter.utils;

import java.util.ArrayList;
import java.util.List;

public class Config {
	public String world = "world";
	public int radius = 128;
	public int maximumPerZone = 5;
	public List<ExcludedZone> excludedZones = new ArrayList<>();
	public int oneOver = 5;
	public int step = 48;
}
