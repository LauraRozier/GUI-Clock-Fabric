package net.thibmorozier.guiclock.config.option;

import java.util.HashMap;
import java.util.Map;

import com.terraformersmc.modmenu.config.option.ConfigOptionStorage;

public class ThibConfigOptionStorage extends ConfigOptionStorage {
    private static final Map<String, Integer> INTEGER_OPTIONS = new HashMap<>();

	public static void setInteger(String key, Integer value) {
		INTEGER_OPTIONS.put(key, value);
	}

	public static Integer getInteger(String key) {
		return INTEGER_OPTIONS.get(key);
	}
}
