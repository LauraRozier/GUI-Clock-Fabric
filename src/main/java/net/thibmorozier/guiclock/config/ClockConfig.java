package net.thibmorozier.guiclock.config;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import com.terraformersmc.modmenu.config.option.OptionConvertable;

import net.minecraft.client.option.Option;
import net.thibmorozier.guiclock.config.enums.ClockPosEnum;
import net.thibmorozier.guiclock.config.option.ClockBooleanConfigOption;
import net.thibmorozier.guiclock.config.option.ClockPosEnumConfigOption;
import net.thibmorozier.guiclock.config.option.ClockIntegerConfigOption;

public class ClockConfig {
    public static final ClockBooleanConfigOption MUST_HAVE_CLOCK_IN_INVENTORY = new ClockBooleanConfigOption("must_have_clock_in_inventory", true);
    public static final ClockBooleanConfigOption SHOW_GAME_TIME = new ClockBooleanConfigOption("show_game_time", true);
    public static final ClockBooleanConfigOption SHOW_REAL_TIME = new ClockBooleanConfigOption("show_real_time", false);
    public static final ClockBooleanConfigOption SHOW_24H_FORMAT = new ClockBooleanConfigOption("show_24h_format", false);
    public static final ClockBooleanConfigOption SHOW_TIME_SECONDS = new ClockBooleanConfigOption("show_time_seconds", false);
    public static final ClockBooleanConfigOption SHOW_WORLD_DAYS = new ClockBooleanConfigOption("show_world_days", false);
    public static final ClockIntegerConfigOption RGB_R = new ClockIntegerConfigOption("rgb_r", 224, 0, 255);
    public static final ClockIntegerConfigOption RGB_G = new ClockIntegerConfigOption("rgb_g", 224, 0, 255);
    public static final ClockIntegerConfigOption RGB_B = new ClockIntegerConfigOption("rgb_b", 224, 0, 255);
    public static final ClockPosEnumConfigOption POSITION = new ClockPosEnumConfigOption("position", ClockPosEnum.BOTTOM_LEFT);

    public static Option[] asOptions() {
		ArrayList<Option> options = new ArrayList<>();

		for (Field field : ClockConfig.class.getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()) && OptionConvertable.class.isAssignableFrom(field.getType()) && !field.getName().equals("HIDE_CONFIG_BUTTONS") && !field.getName().equals("MODIFY_TITLE_SCREEN") && !field.getName().equals("MODIFY_GAME_MENU")) {
				try {
					options.add(((OptionConvertable) field.get(null)).asOption());
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}

		return options.stream().toArray(Option[]::new);
	}
}
