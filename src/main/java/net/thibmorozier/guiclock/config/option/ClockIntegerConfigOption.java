package net.thibmorozier.guiclock.config.option;

import net.thibmorozier.guiclock.util.ClockTranslationUtil;

import com.terraformersmc.modmenu.config.option.OptionConvertable;

import net.minecraft.client.option.DoubleOption;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ClockIntegerConfigOption implements OptionConvertable {
    private final String key, translationKey;
	private final Text toolTip;
	private final Integer defaultValue;
	private final Integer minValue;
	private final Integer maxValue;

	public ClockIntegerConfigOption(String key, Integer defaultValue, Integer minValue, Integer maxValue) {
		ClockConfigOptionStorage.setInteger(key, defaultValue);
		this.key = key;
		this.translationKey = ClockTranslationUtil.translationKeyOf("option", key);
		this.toolTip = new TranslatableText(translationKey + ".tooltip");
		this.defaultValue = defaultValue;
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	public ClockIntegerConfigOption(String key, Integer defaultValue) {
		this(key, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	public String getKey() {
		return key;
	}

	public Integer getValue() {
		return ClockConfigOptionStorage.getInteger(key);
	}

	public void setValue(Integer value) {
		ClockConfigOptionStorage.setInteger(key, value);
	}

	public Integer getDefaultValue() {
		return defaultValue;
	}

	@Override
	public DoubleOption asOption() {
		return new DoubleOption(translationKey, minValue, maxValue, 1.0f,
			ignored -> (double) ClockConfigOptionStorage.getInteger(key),
			(option, value) -> ClockConfigOptionStorage.setInteger(key, value.intValue()),
			(ignored, option) -> new TranslatableText(translationKey, ClockConfigOptionStorage.getInteger(key)),
			client -> client.textRenderer.wrapLines(toolTip, 200)
		);
	}
}
