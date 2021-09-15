package net.thibmorozier.guiclock.config.modmenu;

import java.util.List;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.TranslatableText;
import net.thibmorozier.guiclock.config.ClockConfig;
import net.thibmorozier.guiclock.config.ClockConfigManager;

@Environment(EnvType.CLIENT)
public class ClockScreenWidget extends GameOptionsScreen  {
    private ButtonListWidget buttonList;

    public ClockScreenWidget(Screen previous) {
        super(previous, MinecraftClient.getInstance().options, new TranslatableText("guiclock.options"));
    }

    protected void init() {
        this.buttonList = new ButtonListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);
        this.buttonList.addAll(ClockConfig.asOptions());
        addSelectableChild(this.buttonList);

        // Draw DONE button
        addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, ScreenTexts.DONE, button -> this.client.setScreen(this.parent)));
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        this.buttonList.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 5, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
        List<OrderedText> list = getHoveredButtonTooltip(this.buttonList, mouseX, mouseY);

        if (list != null)
            renderOrderedTooltip(matrices, list, mouseX, mouseY);
    }

	public void removed() {
		ClockConfigManager.save();
	}
}
