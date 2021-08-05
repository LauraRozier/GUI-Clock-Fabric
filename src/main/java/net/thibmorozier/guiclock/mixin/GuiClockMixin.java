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
import net.thibmorozier.guiclock.config.Config;

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

		if (Config.MUST_HAVE_CLOCK_IN_INVENTORY.getValue())
			if (!client.player.getInventory().contains(Items.CLOCK.getDefaultStack()))
				return;

		if (textRenderer == null)
			textRenderer = client.textRenderer;

		String timeStr = "";

		if (Config.SHOW_GAME_TIME.getValue())
			timeStr = getGameTime(client);

		if (Config.SHOW_REAL_TIME.getValue()) {
			if (!timeStr.isBlank())
				timeStr += " | ";

			timeStr += getPCLocalTime(Config.SHOW_24H_FORMAT.getValue(), Config.SHOW_TIME_SECONDS.getValue());
		}

		if (timeStr.isBlank())
			return;

        int coordX   = 3;
        int coordY   = client.getWindow().getScaledHeight() - 25;

		Color foreColor = new Color(Config.RGB_R.getValue(), Config.RGB_G.getValue(), Config.RGB_B.getValue(), 255);

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		DrawableHelper.fill(matrices, coordX - 2, coordY - 2, coordX + textRenderer.getWidth(timeStr) + 2, coordY + 11, client.options.getTextBackgroundColor(0));
		textRenderer.drawWithShadow(matrices, timeStr, (float)coordX, (float)coordY, foreColor.getRGB());

		if (!daystring.isBlank()) {
			int dayCoordY = coordY + 10;
			DrawableHelper.fill(matrices, coordX - 2, dayCoordY - 2, coordX + textRenderer.getWidth(daystring) + 2, dayCoordY + 11, client.options.getTextBackgroundColor(0));
			textRenderer.drawWithShadow(matrices, daystring, (float)coordX, (float)dayCoordY, foreColor.getRGB());
		}

		RenderSystem.disableBlend();
	}

	private String getGameTime(MinecraftClient client) {
		long gameTime  = client.world.getLunarTime();
		int daysplayed = 0;

		while (gameTime >= 24000) {
			gameTime -= 24000;
			daysplayed++;
		}

		daystring = Config.SHOW_WORLD_DAYS.getValue() ? "Day " + daysplayed : "";
		int time = (int)(gameTime >= 18000 ? gameTime - 18000 : gameTime + 6000);
		String suffix = "";

		if (!Config.SHOW_24H_FORMAT.getValue()) {
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

		timeStr = ((!Config.SHOW_24H_FORMAT.getValue()) && strSplit[0].equals("0") ? strSplit[1] : strSplit[0] + strSplit[1]) + ":" + sm;
		return timeStr + suffix;
	}

	private String getPCLocalTime(boolean twentyfour, boolean showseconds) {
		LocalDateTime now = LocalDateTime.now();

		if (showseconds) {
			return now.format(DateTimeFormatter.ofPattern(twentyfour ? "HH:mm:ss" : "hh:mm:ss a"));
		} else {
			return now.format(DateTimeFormatter.ofPattern(twentyfour ? "HH:mm" : "hh:mm a"));
		}
	}
}
