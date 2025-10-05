package games.polarbearbytes.slimenomore.networking;

import games.polarbearbytes.slimenomore.SlimeNoMore;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import java.util.List;
import java.util.ArrayList;

/**
 * Our Server To Client packet telling it the seed
 * @param slimeChunkStates states for the currently set slime chunks
 */
public record SyncPacket(List<ChunkPos> slimeChunkStates) implements CustomPayload {
    public static final Id<SyncPacket> ID = new Id<>(Identifier.of(SlimeNoMore.MOD_ID, "sync_packet"));
    public static final CustomPayload.Id<SyncPacket> PAYLOAD_ID = new CustomPayload.Id<>(Identifier.of(SlimeNoMore.MOD_ID, "sync_packet"));

    public static final PacketCodec<RegistryByteBuf, SyncPacket> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.collection(
                ArrayList::new,
                ChunkPos.PACKET_CODEC
            ), SyncPacket::slimeChunkStates,
            SyncPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}