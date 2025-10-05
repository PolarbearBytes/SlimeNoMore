package games.polarbearbytes.slimenomore.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "slimenomore")
public class SlimeNoMoreClientConfig implements ConfigData {
    public boolean renderBox = true;
    public boolean renderBillboard = true;
}
