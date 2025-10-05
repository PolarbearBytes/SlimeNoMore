package games.polarbearbytes.slimenomore;

import games.polarbearbytes.slimenomore.data.SlimeChunksState;
import games.polarbearbytes.slimenomore.networking.SyncPacket;
import games.polarbearbytes.slimenomore.networking.SlimeChunkTogglePacket;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
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

		ServerPlayerEvents.JOIN.register(player -> {
			MinecraftServer server = player.getEntityWorld().getServer();
			List<ChunkPos> list = SlimeChunksState.get(server).getList();
			ServerPlayNetworking.send(player,new SyncPacket(list));
		});

		//Register the handler for when server receives our packet
		ServerPlayNetworking.registerGlobalReceiver(SlimeChunkTogglePacket.ID, (payload, context) -> {
			MinecraftServer server = context.server();
			ServerPlayerEntity player = context.player();

			boolean singlePlayerHost = server.isSingleplayer() || server.getPlayerManager().isOperator(player.getPlayerConfigEntry());

			if (!singlePlayerHost && !Permissions.check(context.player(), SlimeNoMore.MOD_ID+".toggleSlimeSpawning")) {
				context.player().sendMessage(Text.of(
					"You do not have permission to toggle slime spawning"
				));
			} else {
				Boolean result = SlimeChunksState.get(server).toggleChunkState(payload.chunkPosition());
				context.player().sendMessage(Text.of(
						String.format(
								"%s Slime Spawning in Chunk",
								Boolean.FALSE.equals(result) ? "Disabled" : "Allowed"
						)
				));
			}

			List<ChunkPos> list = SlimeChunksState.get(server).getList();
			ServerPlayNetworking.send(context.player(),new SyncPacket(list));
		});
	}
}