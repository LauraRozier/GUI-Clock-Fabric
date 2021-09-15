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
import net.thibmorozier.guiclock.util.TranslationUtil;

public class EnumConfigOption<E extends Enum<E>> implements OptionConvertable {
	private final String key, translationKey;
	private final Text toolTip;
	private final Class<E> enumClass;
	private final E defaultValue;

	public EnumConfigOption(String key, E defaultValue) {
		ThibConfigOptionStorage.setEnum(key, defaultValue);
		this.key = key;
		this.translationKey = TranslationUtil.translationKeyOf("option", key);
		this.toolTip = new TranslatableText(translationKey + ".tooltip");
		this.enumClass = defaultValue.getDeclaringClass();
		this.defaultValue = defaultValue;
	}

	public String getKey() {
		return key;
	}

	public E getValue() {
		return ThibConfigOptionStorage.getEnum(key, enumClass);
	}

	public void setValue(E value) {
		ThibConfigOptionStorage.setEnum(key, value);
	}

	public void cycleValue() {
		ThibConfigOptionStorage.cycleEnum(key, enumClass);
	}

	public void cycleValue(int amount) {
		ThibConfigOptionStorage.cycleEnum(key, enumClass, amount);
	}

	public E getDefaultValue() {
		return defaultValue;
	}

	private static <E extends Enum<E>> Text getValueText(EnumConfigOption<E> option, E value) {
		return new TranslatableText(option.translationKey + "." + value.name().toLowerCase(Locale.ROOT));
	}

	public Text getButtonText() {
		return ScreenTexts.composeGenericOptionText(new TranslatableText(translationKey), getValueText(this, getValue()));
	}

	@Override
	public Option asOption() {
		return CyclingOption.create(translationKey, enumClass.getEnumConstants(),
            value -> getValueText(this, value),
            ignored -> ThibConfigOptionStorage.getEnum(key, enumClass),
            (ignored, option, value) -> ThibConfigOptionStorage.setEnum(key, value)
        ).tooltip((client) -> {
            List<OrderedText> list = client.textRenderer.wrapLines(toolTip, 200);
            return (value) -> { return list; };
        });
	}
}

