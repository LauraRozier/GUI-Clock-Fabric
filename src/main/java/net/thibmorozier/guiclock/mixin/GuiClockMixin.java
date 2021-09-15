package net.thibmorozier.guiclock.mixin;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Items;
import net.thibmorozier.guiclock.config.ClockConfig;
import net.thibmorozier.guiclock.util.ClockVector2i;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class GuiClockMixin {
	private TextRenderer textRenderer = null;
	private String daystring          = null;

    @Accessor("client")
    protected abstract MinecraftClient getClient();

	@Inject(
		method = "render(Lnet/minecraft/client/util/math/MatrixStack;F)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/hud/InGameHud;renderStatusEffectOverlay(Lnet/minecraft/client/util/math/MatrixStack;)V",
			shift = At.Shift.AFTER
		),
		cancellable = true
	)
	private void renderClock(MatrixStack matrices, float tickDelta, CallbackInfo info) {
		MinecraftClient client = getClient();

		if (ClockConfig.MUST_HAVE_CLOCK_IN_INVENTORY.getValue())
			if (!client.player.getInventory().contains(Items.CLOCK.getDefaultStack()))
				return;

		if (textRenderer == null)
			textRenderer = client.textRenderer;

		String timeStr = "";

		if (ClockConfig.SHOW_GAME_TIME.getValue())
			timeStr = getClockGameTime(client);

		if (ClockConfig.SHOW_REAL_TIME.getValue()) {
			if (!timeStr.isBlank())
				timeStr += " | ";

			timeStr += getClockPCLocalTime(ClockConfig.SHOW_24H_FORMAT.getValue(), ClockConfig.SHOW_TIME_SECONDS.getValue());
		}

		if (timeStr.isBlank())
			return;

		int textWidth = textRenderer.getWidth(timeStr);
		ClockVector2i screenPos = getClockScreenPos(client, textWidth);

		Color foreColor = new Color(ClockConfig.RGB_R.getValue(), ClockConfig.RGB_G.getValue(), ClockConfig.RGB_B.getValue(), 255);

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		DrawableHelper.fill(matrices, screenPos.getX() - 2, screenPos.getY() - 2, screenPos.getX() + textWidth + 2, screenPos.getY() + 11, client.options.getTextBackgroundColor(0));
		textRenderer.drawWithShadow(matrices, timeStr, (float)screenPos.getX(), (float)screenPos.getY(), foreColor.getRGB());

		if (!daystring.isBlank()) {
			int dayCoordY = screenPos.getY() + 10;
			DrawableHelper.fill(matrices, screenPos.getX() - 2, dayCoordY - 2, screenPos.getX() + textWidth + 2, dayCoordY + 11, client.options.getTextBackgroundColor(0));
			textRenderer.drawWithShadow(matrices, daystring, (float)screenPos.getX(), (float)dayCoordY, foreColor.getRGB());
		}

		RenderSystem.disableBlend();
	}

	private String getClockGameTime(MinecraftClient client) {
		long gameTime  = client.world.getLunarTime();
		int daysplayed = 0;

		while (gameTime >= 24000) {
			gameTime -= 24000;
			daysplayed++;
		}

		daystring = ClockConfig.SHOW_WORLD_DAYS.getValue() ? "Day " + daysplayed : "";
		int time = (int)(gameTime >= 18000 ? gameTime - 18000 : gameTime + 6000);
		String suffix = "";

		if (!ClockConfig.SHOW_24H_FORMAT.getValue()) {
			if (time >= 12000) {
				suffix = " pm";

				if (time >= 13000)
					time -= 12000;
			} else {
				suffix = " am";

				if (time < 1000)
					time += 12000;
			}
		}

		String timeStr = time / 10 + "";

		for (int n = timeStr.length(); n < 4; n++)
			timeStr = "0" + timeStr;

		String[] strSplit = timeStr.split("");
		int minutes = (int)Math.floor((Double.parseDouble(strSplit[2] + strSplit[3]) / 100) * 60);
		String sm = (minutes < 10 ? "0" : "") + minutes;

		timeStr = ((!ClockConfig.SHOW_24H_FORMAT.getValue()) && strSplit[0].equals("0") ? strSplit[1] : strSplit[0] + strSplit[1]) + ":" + sm;
		return timeStr + suffix;
	}

	private String getClockPCLocalTime(boolean twentyfour, boolean showseconds) {
		LocalDateTime now = LocalDateTime.now();

		if (showseconds) {
			return now.format(DateTimeFormatter.ofPattern(twentyfour ? "HH:mm:ss" : "hh:mm:ss a"));
		} else {
			return now.format(DateTimeFormatter.ofPattern(twentyfour ? "HH:mm" : "hh:mm a"));
		}
	}

	private ClockVector2i getClockScreenPos(MinecraftClient client, int textWidth) {
		switch (ClockConfig.POSITION.getValue()) {
			case TOP_LEFT:      return new ClockVector2i(3, 12);
			case TOP_CENTER:    return new ClockVector2i((client.getWindow().getScaledWidth() / 2) - (textWidth / 2), 12);
			case TOP_RIGHT:     return new ClockVector2i(client.getWindow().getScaledWidth() - (textWidth + 3), 12);

			case CENTER_LEFT:   return new ClockVector2i(3, (client.getWindow().getScaledHeight() / 2) + 5);
			case CENTER_CENTER: return new ClockVector2i((client.getWindow().getScaledWidth() / 2) - (textWidth / 2), (client.getWindow().getScaledHeight() / 2) + 5);
			case CENTER_RIGHT:  return new ClockVector2i(client.getWindow().getScaledWidth() - (textWidth + 3), (client.getWindow().getScaledHeight() / 2) + 5);

			case BOTTOM_LEFT:   return new ClockVector2i(3, client.getWindow().getScaledHeight() - 25);
			case BOTTOM_CENTER: return new ClockVector2i((client.getWindow().getScaledWidth() / 2) - (textWidth / 2), client.getWindow().getScaledHeight() - 25);
			case BOTTOM_RIGHT:  return new ClockVector2i(client.getWindow().getScaledWidth() - (textWidth + 3), client.getWindow().getScaledHeight() - 25);

			default:            return new ClockVector2i(3, client.getWindow().getScaledHeight() - 25);
		}
	}
}
