package games.polarbearbytes.slimenomore.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import games.polarbearbytes.slimenomore.config.Config;
import games.polarbearbytes.slimenomore.config.SlimeNoMoreClientConfig;
import me.shedaniel.autoconfig.AutoConfig;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> Config.get().createConfigScreen(parent);
        //return parent -> AutoConfig.getConfigScreen(SlimeNoMoreClientConfig.class, parent).get();
    }
}
