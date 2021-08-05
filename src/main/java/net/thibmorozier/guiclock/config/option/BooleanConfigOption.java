package net.thibmorozier.guiclock.config.option;

import net.thibmorozier.guiclock.util.TranslationUtil;

import com.terraformersmc.modmenu.config.option.OptionConvertable;

import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.option.CyclingOption;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class BooleanConfigOption implements OptionConvertable {
    private final String key, translationKey;
	private final boolean defaultValue;
	private final Text enabledText;
	private final Text disabledText;

	public BooleanConfigOption(String key, boolean defaultValue, String enabledKey, String disabledKey) {
		ThibConfigOptionStorage.setBoolean(key, defaultValue);
		this.key = key;
		this.translationKey = TranslationUtil.translationKeyOf("option", key);
		this.defaultValue = defaultValue;
		this.enabledText = new TranslatableText(translationKey + "." + enabledKey);
		this.disabledText = new TranslatableText(translationKey + "." + disabledKey);
	}

	public BooleanConfigOption(String key, boolean defaultValue) {
		this(key, defaultValue, "true", "false");
	}

	public String getKey() {
		return key;
	}

	public boolean getValue() {
		return ThibConfigOptionStorage.getBoolean(key);
	}

	public void setValue(boolean value) {
		ThibConfigOptionStorage.setBoolean(key, value);
	}

	public void toggleValue() {
		ThibConfigOptionStorage.toggleBoolean(key);
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
			return CyclingOption.create(translationKey, enabledText, disabledText, ignored -> ThibConfigOptionStorage.getBoolean(key), (ignored, option, value) -> ThibConfigOptionStorage.setBoolean(key, value));

		return CyclingOption.create(translationKey, ignored -> ThibConfigOptionStorage.getBoolean(key), (ignored, option, value) -> ThibConfigOptionStorage.setBoolean(key, value));
	}
}
