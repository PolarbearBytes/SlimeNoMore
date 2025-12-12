package games.polarbearbytes.slimenomore.config;

import net.minecraft.client.gui.screen.Screen;

public interface ConfigProvider {

    SlimeNoMoreClientConfig getConfig();

    Screen createConfigScreen(Screen parent);
}