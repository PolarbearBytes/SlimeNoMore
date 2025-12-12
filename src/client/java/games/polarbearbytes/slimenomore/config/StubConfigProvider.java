package games.polarbearbytes.slimenomore.config;

import net.minecraft.client.gui.screen.Screen;

public class StubConfigProvider implements ConfigProvider {

    private static final SlimeNoMoreClientConfig CONFIG = new SlimeNoMoreClientConfig();

    @Override
    public SlimeNoMoreClientConfig getConfig() {
        return CONFIG;
    }

    @Override
    public Screen createConfigScreen(Screen parent) {
        return parent; // No config GUI
    }
}