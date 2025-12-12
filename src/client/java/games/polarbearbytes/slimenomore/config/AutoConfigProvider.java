package games.polarbearbytes.slimenomore.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.AutoConfigClient;
import net.minecraft.client.gui.screen.Screen;

public class AutoConfigProvider implements ConfigProvider {

    @Override
    public SlimeNoMoreClientConfig getConfig() {
        return AutoConfig.getConfigHolder(SlimeNoMoreClientConfig.class).getConfig();
    }

    @Override
    public Screen createConfigScreen(Screen parent) {
        return AutoConfigClient.getConfigScreen(SlimeNoMoreClientConfig.class, parent).get();
    }
}