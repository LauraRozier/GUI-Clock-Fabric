package net.thibmorozier.guiclock.config.option;

import net.thibmorozier.guiclock.util.TranslationUtil;

import com.terraformersmc.modmenu.config.option.OptionConvertable;

import net.minecraft.client.option.DoubleOption;
import net.minecraft.text.TranslatableText;

public class IntegerConfigOption implements OptionConvertable {
    private final String key, translationKey;
	private final Integer defaultValue;
	private final Integer minValue;
	private final Integer maxValue;

	public IntegerConfigOption(String key, Integer defaultValue, Integer minValue, Integer maxValue) {
		ThibConfigOptionStorage.setInteger(key, defaultValue);
		this.key = key;
		this.translationKey = TranslationUtil.translationKeyOf("option", key);
		this.defaultValue = defaultValue;
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	public IntegerConfigOption(String key, Integer defaultValue) {
		this(key, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	public String getKey() {
		return key;
	}

	public Integer getValue() {
		return ThibConfigOptionStorage.getInteger(key);
	}

	public void setValue(Integer value) {
		ThibConfigOptionStorage.setInteger(key, value);
	}

	public Integer getDefaultValue() {
		return defaultValue;
	}

	@Override
	public DoubleOption asOption() {
		return new DoubleOption(translationKey, minValue, maxValue, 1.0f,
			ignored -> (double) ThibConfigOptionStorage.getInteger(key),
			(option, value) -> ThibConfigOptionStorage.setInteger(key, value.intValue()),
			(ignored, option) -> new TranslatableText(translationKey, ThibConfigOptionStorage.getInteger(key))
		);
	}
}
