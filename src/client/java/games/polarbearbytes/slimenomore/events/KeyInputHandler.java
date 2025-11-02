package games.polarbearbytes.slimenomore.events;

import games.polarbearbytes.slimenomore.SlimeNoMoreClient;
import games.polarbearbytes.slimenomore.config.SlimeNoMoreClientConfig;
import games.polarbearbytes.slimenomore.networking.SlimeChunkTogglePacket;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

/**
 * Our keybind handler class
 */
public class KeyInputHandler {
    public static final KeyBinding.Category KEY_CATEGORY_KEYS = KeyBinding.Category.create(Identifier.of("slime-no-more","keys"));
    public static final String KEY_TOGGLE_SLIMECHUNK = "key.slimenomore.toggleslimechunk";
    public static final String KEY_TOGGLE_DISABLED_DISPLAY = "key.slimenomore.togglesdisableddisplay";
    public static final String KEY_TOGGLE_BOX_DISPLAY = "key.slimenomore.togglesboxdisplay";
    public static final String KEY_TOGGLE_BILLBOARD_DISPLAY = "key.slimenomore.togglesbillboarddisplay";

    public static KeyBinding toggleSlimeChunkKey;
    public static KeyBinding toggleDisabledDisplayKey;
    public static KeyBinding toggleBoxDisplayKey;
    public static KeyBinding toggleBillboardDisplayKey;

    public static void registerKeyInputs(){
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            SlimeNoMoreClientConfig config = AutoConfig.getConfigHolder(SlimeNoMoreClientConfig.class).getConfig();
            if(toggleSlimeChunkKey.wasPressed()){
                if(client.player == null) return;
                ClientPlayNetworking.send(new SlimeChunkTogglePacket(client.player.getChunkPos()));
            }
            if(toggleDisabledDisplayKey.wasPressed()){
                SlimeNoMoreClient.renderDisabledDisplay = !SlimeNoMoreClient.renderDisabledDisplay;
            }
            if(toggleBoxDisplayKey.wasPressed()){
                config.renderBox = !config.renderBox;
            }
            if(toggleBillboardDisplayKey.wasPressed()){
                config.renderBillboard = !config.renderBillboard;
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
        toggleDisabledDisplayKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_TOGGLE_DISABLED_DISPLAY,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_INSERT,
                KEY_CATEGORY_KEYS
        ));
        toggleBoxDisplayKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_TOGGLE_BOX_DISPLAY,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_PAGE_DOWN,
                KEY_CATEGORY_KEYS
        ));
        toggleBillboardDisplayKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_TOGGLE_BILLBOARD_DISPLAY,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_PAGE_UP,
                KEY_CATEGORY_KEYS
        ));

        registerKeyInputs();
    }
}
