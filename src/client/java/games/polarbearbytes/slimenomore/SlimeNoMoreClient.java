package games.polarbearbytes.slimenomore;

import games.polarbearbytes.slimenomore.config.SlimeNoMoreClientConfig;
import games.polarbearbytes.slimenomore.events.KeyInputHandler;
import games.polarbearbytes.slimenomore.networking.SyncPacket;
import games.polarbearbytes.slimenomore.render.DisabledSlimeChunkRenderer;
import games.polarbearbytes.slimenomore.render.RendererHandler;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.math.ChunkPos;

import java.util.ArrayList;
import java.util.List;

public class SlimeNoMoreClient implements ClientModInitializer {
	public static List<ChunkPos> slimeChunkStates = new ArrayList<>();
	public static boolean renderDisabledDisplay = false;

	@SuppressWarnings("resource")
	@Override
	public void onInitializeClient() {
		KeyInputHandler.register();

		AutoConfig.register(SlimeNoMoreClientConfig.class, GsonConfigSerializer::new);

		RendererHandler.getInstance().register(DisabledSlimeChunkRenderer.INSTANCE);

		ClientPlayNetworking.registerGlobalReceiver(SyncPacket.PAYLOAD_ID, (packet, context) -> context.client().execute(() -> {
			slimeChunkStates = packet.slimeChunkStates();
			DisabledSlimeChunkRenderer.INSTANCE.setDisabledChunks(slimeChunkStates);
		}));
	}
}