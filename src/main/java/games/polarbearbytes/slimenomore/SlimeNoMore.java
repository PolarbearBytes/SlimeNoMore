package games.polarbearbytes.slimenomore;

import games.polarbearbytes.slimenomore.data.SlimeChunksState;
import games.polarbearbytes.slimenomore.networking.SlimeChunkTogglePacket;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlimeNoMore implements ModInitializer {
	public static final String MOD_ID = "slime-no-more";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		//Register the Client To Server packet
		PayloadTypeRegistry.playC2S().register(SlimeChunkTogglePacket.ID, SlimeChunkTogglePacket.PACKET_CODEC);

		//Register the handler for when server receives our packet
		ServerPlayNetworking.registerGlobalReceiver(SlimeChunkTogglePacket.ID, (payload, context) -> {
			Boolean result = SlimeChunksState.get(context.server()).toggleChunkState(payload.chunkPosition());
			context.player().sendMessage(Text.of(
				String.format(
					"⚠ Slime Chunk: (%d,%d), Spawning Disabled: %s ⚠",
						payload.chunkPosition().x,
						payload.chunkPosition().z,
						Boolean.FALSE.equals(result)
				)
			));
		});
	}
}