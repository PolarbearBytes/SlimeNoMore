package games.polarbearbytes.slimenomore;

import games.polarbearbytes.slimenomore.events.KeyInputHandler;
import games.polarbearbytes.slimenomore.networking.SyncPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SlimeNoMoreClient implements ClientModInitializer {
	public static Long seed = null;
	public static List<ChunkPos> slimeChunkStates = new ArrayList<>();
	public static final HashMap<ChunkPos, Box> slimeChunks = new HashMap<>();
	@Override
	public void onInitializeClient() {
		//Make sure we register the keybinds
		KeyInputHandler.register();
		ClientPlayNetworking.registerGlobalReceiver(SyncPacket.PAYLOAD_ID, (packet, context) -> context.client().execute(() -> {
			seed = packet.seed();
			slimeChunkStates = packet.slimeChunkStates();
		}));
	}
}