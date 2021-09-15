package net.thibmorozier.guiclock;

import net.fabricmc.api.ClientModInitializer;
import net.thibmorozier.guiclock.config.ClockConfigManager;

public class GuiClock implements ClientModInitializer {
    public static final String MOD_ID = "guiclock";

	@Override
	public void onInitializeClient() {
        ClockConfigManager.initializeConfig();
    }
}
