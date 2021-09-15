package net.thibmorozier.guiclock.config.option;

import com.terraformersmc.modmenu.config.option.OptionConvertable;

import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.option.CyclingOption;
import net.minecraft.client.option.Option;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.thibmorozier.guiclock.config.enums.ClockPosEnum;
import net.thibmorozier.guiclock.util.ClockTranslationUtil;

public class ClockPosEnumConfigOption implements OptionConvertable {
	private final String key, translationKey;
	private final Text toolTip;
	private final ClockPosEnum defaultValue;

	public ClockPosEnumConfigOption(String key, ClockPosEnum defaultValue) {
		ClockConfigOptionStorage.setClockPosEnum(key, defaultValue);
		this.key = key;
		this.translationKey = ClockTranslationUtil.translationKeyOf("option", key);
		this.toolTip = new TranslatableText(translationKey + ".tooltip");
		this.defaultValue = defaultValue;
	}

	public String getKey() {
		return key;
	}

	public ClockPosEnum getValue() {
		return ClockConfigOptionStorage.getClockPosEnum(key);
	}

	public void setValue(ClockPosEnum value) {
		ClockConfigOptionStorage.setClockPosEnum(key, value);
	}

	public void cycleValue() {
		ClockConfigOptionStorage.cycleClockPosEnum(key);
	}

	public void cycleValue(int amount) {
		ClockConfigOptionStorage.cycleClockPosEnum(key, amount);
	}

	public ClockPosEnum getDefaultValue() {
		return defaultValue;
	}

	private static Text getValueText(ClockPosEnumConfigOption option, ClockPosEnum value) {
		return new TranslatableText(option.translationKey + "." + value.name().toLowerCase(Locale.ROOT));
	}

	public Text getButtonText() {
		return ScreenTexts.composeGenericOptionText(new TranslatableText(translationKey), getValueText(this, getValue()));
	}

	@Override
	public Option asOption() {
		return CyclingOption.create(translationKey, ClockPosEnum.values(),
            value -> getValueText(this, value),
            ignored -> ClockConfigOptionStorage.getClockPosEnum(key),
            (ignored, option, value) -> ClockConfigOptionStorage.setClockPosEnum(key, value)
        ).tooltip((client) -> {
            List<OrderedText> list = client.textRenderer.wrapLines(toolTip, 200);
            return (value) -> { return list; };
        });
	}
}

