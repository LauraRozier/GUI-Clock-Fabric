package net.thibmorozier.guiclock.config;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Locale;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import com.google.gson.*;
import com.terraformersmc.modmenu.config.option.StringSetConfigOption;

import net.fabricmc.loader.api.FabricLoader;
import net.thibmorozier.guiclock.GuiClock;
import net.thibmorozier.guiclock.config.enums.ClockPosEnum;
import net.thibmorozier.guiclock.config.option.ClockBooleanConfigOption;
import net.thibmorozier.guiclock.config.option.ClockPosEnumConfigOption;
import net.thibmorozier.guiclock.config.option.ClockIntegerConfigOption;
import net.thibmorozier.guiclock.config.option.ClockConfigOptionStorage;

public class ClockConfigManager {
    public static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setPrettyPrinting().create();
    private static File file;

    private static void prepareConfigFile() {
		if (file != null)
			return;

		file = new File(FabricLoader.getInstance().getConfigDir().toFile(), GuiClock.MOD_ID + ".json");
	}

	public static void initializeConfig() {
		load();
	}

	private static void load() {
		prepareConfigFile();

		try {
			if (!file.exists())
				save();

			if (file.exists()) {
				BufferedReader br = new BufferedReader(new FileReader(file));
				JsonObject json = new JsonParser().parse(br).getAsJsonObject();

				for (Field field : ClockConfig.class.getDeclaredFields()) {
					if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
						if (StringSetConfigOption.class.isAssignableFrom(field.getType())) {
							JsonArray jsonArray = json.getAsJsonArray(field.getName().toLowerCase(Locale.ROOT));

							if (jsonArray != null) {
								StringSetConfigOption option = (StringSetConfigOption)field.get(null);
								ClockConfigOptionStorage.setStringSet(option.getKey(), Sets.newHashSet(jsonArray).stream().map(JsonElement::getAsString).collect(Collectors.toSet()));
							}
						} else if (ClockIntegerConfigOption.class.isAssignableFrom(field.getType())) {
							JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive(field.getName().toLowerCase(Locale.ROOT));

							if (jsonPrimitive != null && jsonPrimitive.isNumber()) {
								ClockIntegerConfigOption option = (ClockIntegerConfigOption)field.get(null);
								ClockConfigOptionStorage.setInteger(option.getKey(), jsonPrimitive.getAsInt());
							}
						} else if (ClockBooleanConfigOption.class.isAssignableFrom(field.getType())) {
							JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive(field.getName().toLowerCase(Locale.ROOT));

							if (jsonPrimitive != null && jsonPrimitive.isBoolean()) {
								ClockBooleanConfigOption option = (ClockBooleanConfigOption)field.get(null);
								ClockConfigOptionStorage.setBoolean(option.getKey(), jsonPrimitive.getAsBoolean());
							}
						} else if (ClockPosEnumConfigOption.class.isAssignableFrom(field.getType())) {
							JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive(field.getName().toLowerCase(Locale.ROOT));

							if (jsonPrimitive != null && jsonPrimitive.isString()) {
								ClockPosEnumConfigOption option = (ClockPosEnumConfigOption) field.get(null);
								ClockPosEnum found = null;

								for (ClockPosEnum value : ClockPosEnum.values()) {
									if (value.name().toLowerCase(Locale.ROOT).equals(jsonPrimitive.getAsString())) {
										found = value;
										break;
									}
								}

								if (found != null)
									ClockConfigOptionStorage.setClockPosEnum(option.getKey(), found);
							}
						}
					}
				}
			}
		} catch (FileNotFoundException | IllegalAccessException e) {
			System.err.println("Couldn't load Mod Menu configuration file; reverting to defaults");
			e.printStackTrace();
		}
	}

	public static void save() {
		prepareConfigFile();

		JsonObject config = new JsonObject();

		try {
			for (Field field : ClockConfig.class.getDeclaredFields()) {
				if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
					if (ClockBooleanConfigOption.class.isAssignableFrom(field.getType())) {
						ClockBooleanConfigOption option = (ClockBooleanConfigOption)field.get(null);
						config.addProperty(field.getName().toLowerCase(Locale.ROOT), ClockConfigOptionStorage.getBoolean(option.getKey()));
					} else if (ClockIntegerConfigOption.class.isAssignableFrom(field.getType())) {
						ClockIntegerConfigOption option = (ClockIntegerConfigOption)field.get(null);
						config.addProperty(field.getName().toLowerCase(Locale.ROOT), ClockConfigOptionStorage.getInteger(option.getKey()));
					} else if (StringSetConfigOption.class.isAssignableFrom(field.getType())) {
						StringSetConfigOption option = (StringSetConfigOption)field.get(null);
						JsonArray array = new JsonArray();
						ClockConfigOptionStorage.getStringSet(option.getKey()).forEach(array::add);
						config.add(field.getName().toLowerCase(Locale.ROOT), array);
					} else if (ClockPosEnumConfigOption.class.isAssignableFrom(field.getType())) {
						ClockPosEnumConfigOption option = (ClockPosEnumConfigOption) field.get(null);
						config.addProperty(field.getName().toLowerCase(Locale.ROOT), ClockConfigOptionStorage.getClockPosEnum(option.getKey()).name().toLowerCase(Locale.ROOT));
					}
				}
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		String jsonString = GSON.toJson(config);

		try (FileWriter fileWriter = new FileWriter(file)) {
			fileWriter.write(jsonString);
		} catch (IOException e) {
			System.err.println("Couldn't save Mod Menu configuration file");
			e.printStackTrace();
		}
	}
}
