package net.thibmorozier.guiclock.config;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Locale;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import com.google.gson.*;
import com.terraformersmc.modmenu.config.option.EnumConfigOption;
import com.terraformersmc.modmenu.config.option.StringSetConfigOption;

import net.fabricmc.loader.api.FabricLoader;
import net.thibmorozier.guiclock.GuiClock;
import net.thibmorozier.guiclock.config.option.BooleanConfigOption;
import net.thibmorozier.guiclock.config.option.IntegerConfigOption;
import net.thibmorozier.guiclock.config.option.ThibConfigOptionStorage;

public class ConfigManager {
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

				for (Field field : Config.class.getDeclaredFields()) {
					if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
						if (StringSetConfigOption.class.isAssignableFrom(field.getType())) {
							JsonArray jsonArray = json.getAsJsonArray(field.getName().toLowerCase(Locale.ROOT));

							if (jsonArray != null) {
								StringSetConfigOption option = (StringSetConfigOption)field.get(null);
								ThibConfigOptionStorage.setStringSet(option.getKey(), Sets.newHashSet(jsonArray).stream().map(JsonElement::getAsString).collect(Collectors.toSet()));
							}
						} else if (IntegerConfigOption.class.isAssignableFrom(field.getType())) {
							JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive(field.getName().toLowerCase(Locale.ROOT));

							if (jsonPrimitive != null && jsonPrimitive.isNumber()) {
								IntegerConfigOption option = (IntegerConfigOption)field.get(null);
								ThibConfigOptionStorage.setInteger(option.getKey(), jsonPrimitive.getAsInt());
							}
						} else if (BooleanConfigOption.class.isAssignableFrom(field.getType())) {
							JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive(field.getName().toLowerCase(Locale.ROOT));

							if (jsonPrimitive != null && jsonPrimitive.isBoolean()) {
								BooleanConfigOption option = (BooleanConfigOption)field.get(null);
								ThibConfigOptionStorage.setBoolean(option.getKey(), jsonPrimitive.getAsBoolean());
							}
						} else if (EnumConfigOption.class.isAssignableFrom(field.getType()) && field.getGenericType() instanceof ParameterizedType) {
							JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive(field.getName().toLowerCase(Locale.ROOT));

							if (jsonPrimitive != null && jsonPrimitive.isString()) {
								Type generic = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];

								if (generic instanceof Class<?>) {
									EnumConfigOption<?> option = (EnumConfigOption<?>)field.get(null);
									Enum<?> found = null;

									for (Enum<?> value : ((Class<Enum<?>>) generic).getEnumConstants()) {
										if (value.name().toLowerCase(Locale.ROOT).equals(jsonPrimitive.getAsString())) {
											found = value;
											break;
										}
									}

									if (found != null)
										ThibConfigOptionStorage.setEnumTypeless(option.getKey(), found);
								}
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
			for (Field field : Config.class.getDeclaredFields()) {
				if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
					if (BooleanConfigOption.class.isAssignableFrom(field.getType())) {
						BooleanConfigOption option = (BooleanConfigOption)field.get(null);
						config.addProperty(field.getName().toLowerCase(Locale.ROOT), ThibConfigOptionStorage.getBoolean(option.getKey()));
					} else if (IntegerConfigOption.class.isAssignableFrom(field.getType())) {
						IntegerConfigOption option = (IntegerConfigOption)field.get(null);
						config.addProperty(field.getName().toLowerCase(Locale.ROOT), ThibConfigOptionStorage.getInteger(option.getKey()));
					} else if (StringSetConfigOption.class.isAssignableFrom(field.getType())) {
						StringSetConfigOption option = (StringSetConfigOption)field.get(null);
						JsonArray array = new JsonArray();
						ThibConfigOptionStorage.getStringSet(option.getKey()).forEach(array::add);
						config.add(field.getName().toLowerCase(Locale.ROOT), array);
					} else if (EnumConfigOption.class.isAssignableFrom(field.getType()) && field.getGenericType() instanceof ParameterizedType) {
						Type generic = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];

						if (generic instanceof Class<?>) {
							EnumConfigOption<?> option = (EnumConfigOption<?>)field.get(null);
							config.addProperty(field.getName().toLowerCase(Locale.ROOT), ThibConfigOptionStorage.getEnumTypeless(option.getKey(), (Class<Enum<?>>)generic).name().toLowerCase(Locale.ROOT));
						}
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
