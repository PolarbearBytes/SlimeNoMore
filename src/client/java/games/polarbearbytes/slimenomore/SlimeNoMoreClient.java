package games.polarbearbytes.slimenomore;

import games.polarbearbytes.slimenomore.events.KeyInputHandler;
import net.fabricmc.api.ClientModInitializer;

public class SlimeNoMoreClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		//Make sure we register the keybinds
		KeyInputHandler.register();
	}
}