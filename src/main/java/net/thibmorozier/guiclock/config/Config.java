package net.thibmorozier.guiclock.config;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import com.terraformersmc.modmenu.config.option.OptionConvertable;

import net.minecraft.client.option.Option;
import net.thibmorozier.guiclock.config.enums.PositionEnum;
import net.thibmorozier.guiclock.config.option.BooleanConfigOption;
import net.thibmorozier.guiclock.config.option.EnumConfigOption;
import net.thibmorozier.guiclock.config.option.IntegerConfigOption;

public class Config {
    public static final BooleanConfigOption MUST_HAVE_CLOCK_IN_INVENTORY = new BooleanConfigOption("must_have_clock_in_inventory", true);
    public static final BooleanConfigOption SHOW_GAME_TIME = new BooleanConfigOption("show_game_time", true);
    public static final BooleanConfigOption SHOW_REAL_TIME = new BooleanConfigOption("show_real_time", false);
    public static final BooleanConfigOption SHOW_24H_FORMAT = new BooleanConfigOption("show_24h_format", false);
    public static final BooleanConfigOption SHOW_TIME_SECONDS = new BooleanConfigOption("show_time_seconds", false);
    public static final BooleanConfigOption SHOW_WORLD_DAYS = new BooleanConfigOption("show_world_days", false);
    public static final IntegerConfigOption RGB_R = new IntegerConfigOption("rgb_r", 224, 0, 255);
    public static final IntegerConfigOption RGB_G = new IntegerConfigOption("rgb_g", 224, 0, 255);
    public static final IntegerConfigOption RGB_B = new IntegerConfigOption("rgb_b", 224, 0, 255);
    public static final EnumConfigOption<PositionEnum> POSITION = new EnumConfigOption<PositionEnum>("position", PositionEnum.BOTTOM_LEFT);

    public static Option[] asOptions() {
		ArrayList<Option> options = new ArrayList<>();

		for (Field field : Config.class.getDeclaredFields()) {
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
