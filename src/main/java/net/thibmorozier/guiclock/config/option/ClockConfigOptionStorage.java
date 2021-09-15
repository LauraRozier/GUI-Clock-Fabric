package net.thibmorozier.guiclock.config.option;

import java.util.HashMap;
import java.util.Map;

import com.terraformersmc.modmenu.config.option.ConfigOptionStorage;

import net.thibmorozier.guiclock.config.enums.ClockPosEnum;

public class ClockConfigOptionStorage extends ConfigOptionStorage {
    private static final Map<String, Integer> INTEGER_OPTIONS = new HashMap<>();
	private static final Map<String, ClockPosEnum> CLOCK_POS_ENUM_OPTIONS = new HashMap<>();

	public static void setInteger(String key, Integer value) {
		INTEGER_OPTIONS.put(key, value);
	}

	public static Integer getInteger(String key) {
		return INTEGER_OPTIONS.get(key);
	}

	public static void setClockPosEnum(String key, ClockPosEnum value) {
		CLOCK_POS_ENUM_OPTIONS.put(key, value);
	}

	public static ClockPosEnum getClockPosEnum(String key) {
		return CLOCK_POS_ENUM_OPTIONS.get(key);
	}

	public static ClockPosEnum cycleClockPosEnum(String key) {
		return cycleClockPosEnum(key, 1);
	}

	public static ClockPosEnum cycleClockPosEnum(String key, int amount) {
		ClockPosEnum[] values = ClockPosEnum.values();
		ClockPosEnum currentValue = getClockPosEnum(key);
		ClockPosEnum newValue = values[(currentValue.ordinal() + amount) % values.length];
		setClockPosEnum(key, newValue);
		return newValue;
	}
}
