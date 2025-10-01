package games.polarbearbytes.slimenomore;

import games.polarbearbytes.slimenomore.data.SlimeChunksState;
import games.polarbearbytes.slimenomore.networking.SyncPacket;
import games.polarbearbytes.slimenomore.networking.SlimeChunkTogglePacket;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.ChunkPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SlimeNoMore implements ModInitializer {
	public static final String MOD_ID = "slime-no-more";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	@Override
	public void onInitialize() {
		//Register the Client To Server packet
		PayloadTypeRegistry.playC2S().register(SlimeChunkTogglePacket.ID, SlimeChunkTogglePacket.PACKET_CODEC);
		PayloadTypeRegistry.playS2C().register(SyncPacket.PAYLOAD_ID, SyncPacket.PACKET_CODEC);
		//Register the handler for when server receives our packet
		ServerPlayNetworking.registerGlobalReceiver(SlimeChunkTogglePacket.ID, (payload, context) -> {
			MinecraftServer server = context.server();

			Boolean result = SlimeChunksState.get(server).toggleChunkState(payload.chunkPosition());
			List<ChunkPos> list = SlimeChunksState.get(server).getList();
			Long seed = server.getOverworld().getSeed();

			ServerPlayNetworking.send(context.player(),new SyncPacket(list,seed));

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