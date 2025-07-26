package games.polarbearbytes.slimenomore.mixin;


import com.llamalad7.mixinextras.sugar.Local;
import games.polarbearbytes.slimenomore.data.SlimeChunksState;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Mixin class for the SlimeEntity class, we need modify its canSpawn method
 */
@Mixin(SlimeEntity.class)
public class SlimeEntityMixin {

    /**
     * Our injected method tha will change the return value based on wither or not
     * we have disabled the chunk. Disabled we do not spawn, enabled we defer to the
     * normal canSpawn's return value
     *
     * @return True if spawning is allowed at chunk, false otherwise.
     */
    @ModifyVariable(method = "canSpawn(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/WorldAccess;Lnet/minecraft/entity/SpawnReason;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/random/Random;)Z", at = @At("STORE"))
    private static boolean canSpawnMixin(boolean original, @Local(ordinal = 0)ChunkPos chunkPos, @Local(argsOnly = true) WorldAccess world) {
        MinecraftServer server = world.getServer();
        if(server == null) return original;
        return (SlimeChunksState.get(server).getChunkState(chunkPos)) && original;
    }
}
