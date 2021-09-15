package net.thibmorozier.guiclock.config.option;

import net.thibmorozier.guiclock.util.ClockTranslationUtil;

import java.util.List;

import com.terraformersmc.modmenu.config.option.OptionConvertable;

import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.option.CyclingOption;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ClockBooleanConfigOption implements OptionConvertable {
    private final String key, translationKey;
	private final Text toolTip;
	private final boolean defaultValue;
	private final Text enabledText;
	private final Text disabledText;

	public ClockBooleanConfigOption(String key, boolean defaultValue, String enabledKey, String disabledKey) {
		ClockConfigOptionStorage.setBoolean(key, defaultValue);
		this.key = key;
		this.translationKey = ClockTranslationUtil.translationKeyOf("option", key);
		this.toolTip = new TranslatableText(translationKey + ".tooltip");
		this.defaultValue = defaultValue;
		this.enabledText = new TranslatableText(translationKey + "." + enabledKey);
		this.disabledText = new TranslatableText(translationKey + "." + disabledKey);
	}

	public ClockBooleanConfigOption(String key, boolean defaultValue) {
		this(key, defaultValue, "true", "false");
	}

	public String getKey() {
		return key;
	}

	public boolean getValue() {
		return ClockConfigOptionStorage.getBoolean(key);
	}

	public void setValue(boolean value) {
		ClockConfigOptionStorage.setBoolean(key, value);
	}

	public void toggleValue() {
		ClockConfigOptionStorage.toggleBoolean(key);
	}

	public boolean getDefaultValue() {
		return defaultValue;
	}

	public Text getButtonText() {
		return ScreenTexts.composeGenericOptionText(new TranslatableText(translationKey), getValue() ? enabledText : disabledText);
	}

	@Override
	public CyclingOption<Boolean> asOption() {
		if (enabledText != null && disabledText != null)
			return CyclingOption.create(translationKey, enabledText, disabledText,
				ignored -> ClockConfigOptionStorage.getBoolean(key),
				(ignored, option, value) -> ClockConfigOptionStorage.setBoolean(key, value)
			).tooltip((client) -> {
				List<OrderedText> list = client.textRenderer.wrapLines(toolTip, 200);
				return (value) -> { return list; };
			});

		return CyclingOption.create(translationKey, toolTip,
			ignored -> ClockConfigOptionStorage.getBoolean(key),
			(ignored, option, value) -> ClockConfigOptionStorage.setBoolean(key, value)
		);
	}
}
