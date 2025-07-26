package games.polarbearbytes.slimenomore.events;

import games.polarbearbytes.slimenomore.networking.SlimeChunkTogglePacket;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * Our keybind handler class
 */
public class KeyInputHandler {
    public static final String KEY_CATEGORY_KEYS = "key.category.slimenomore.keys";
    public static final String KEY_TOGGLE_SLIMECHUNK = "key.slimenomore.toggleslimechunk";

    public static KeyBinding toggleSlimeChunkKey;

    public static void registerKeyInputs(){
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(toggleSlimeChunkKey.wasPressed()){
                if(client.player == null) return;
                ClientPlayNetworking.send(new SlimeChunkTogglePacket(client.player.getChunkPos()));
            }
        });
    }

    /**
     * Register our keybinds
     */
    public static void register() {
        toggleSlimeChunkKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            KEY_TOGGLE_SLIMECHUNK,
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_DELETE,
            KEY_CATEGORY_KEYS
        ));

        registerKeyInputs();
    }
}
