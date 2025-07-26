package games.polarbearbytes.slimenomore.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

import java.util.*;

/**
 * PersistentState class for storing our chunk states
 * We use the chunk position as a key to store wither or not we disable the spawning for that chunk
 * If the key exists then the spawning is disabled otherwise we defer back to the normal spawning rules
 *
 */
public class SlimeChunksState extends PersistentState {
    private HashMap<ChunkPos,Boolean> slimeChunkStates = new HashMap<>();

    /*
    Custom string codec as ChunkPos does not have proper JSON-style serializable
    key like unboundedMap requires
     */
    public static final Codec<ChunkPos> CHUNK_POS_CODEC = Codec.STRING.xmap(
            str -> {
                String[] parts = str.split(",", 2);
                if (parts.length != 2) throw new IllegalArgumentException("Invalid ChunkPos: " + str);
                return new ChunkPos(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            },
            chunkPos -> chunkPos.x + "," + chunkPos.z
    );
    public static final Codec<SlimeChunksState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.unboundedMap(
            CHUNK_POS_CODEC,
            Codec.BOOL
        ).fieldOf("chunkstates").forGetter(SlimeChunksState::getRawMap)
    ).apply(instance, SlimeChunksState::new));

    //TODO: put all custom Identifiers statically in a central class
    public static final PersistentStateType<SlimeChunksState> TYPE = new PersistentStateType<>("slime_no_more_state",SlimeChunksState::new, CODEC, DataFixTypes.PLAYER);

    public SlimeChunksState(){}

    public SlimeChunksState(Map<ChunkPos,Boolean> slimeChunkStates){
        this.slimeChunkStates = new HashMap<>();
        this.slimeChunkStates.putAll(slimeChunkStates);
    }

    public static SlimeChunksState get(MinecraftServer server){
        return server.getOverworld().getPersistentStateManager().getOrCreate(TYPE);
    }

    private Map<ChunkPos, Boolean> getRawMap(){
        return this.slimeChunkStates;
    }

    /**
     * Toggles the chunk state data in our hashmap.
     *
     * @param chunkPosition The X,Z position of the chunk in the world
     * @return A boolean indicating value that was stored or null if removed
     */
    public Boolean toggleChunkState(ChunkPos chunkPosition){
        Boolean result = this.slimeChunkStates.compute(chunkPosition,(position,enabled)->{
           if(enabled == null){
               return false;
           }
           return null;
        });
        markDirty();
        return result;
    }

    /**
     * Get the state of a chunk
     *
     * @param chunkPosition The X,Z position of the chunk in the world
     * @return boolean indicating wither or not spawning is enabled
     */
    public boolean getChunkState(ChunkPos chunkPosition){
        return this.slimeChunkStates.getOrDefault(chunkPosition,true);
    }
}
