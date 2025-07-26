package games.polarbearbytes.slimenomore.networking;

import games.polarbearbytes.slimenomore.SlimeNoMore;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;

/**
 * Our Client To Server packet telling it to toggle the chunk state at the provided chunk position
 * @param chunkPosition X,Z position of the chunk in the world.
 */
public record SlimeChunkTogglePacket(ChunkPos chunkPosition) implements CustomPayload {
    public static final CustomPayload.Id<SlimeChunkTogglePacket> ID = new CustomPayload.Id<>(Identifier.of(SlimeNoMore.MOD_ID, "slime_chunk_toggle_packet"));

    public static final PacketCodec<RegistryByteBuf, SlimeChunkTogglePacket> PACKET_CODEC = PacketCodec.tuple(
            ChunkPos.PACKET_CODEC, SlimeChunkTogglePacket::chunkPosition,
            SlimeChunkTogglePacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}