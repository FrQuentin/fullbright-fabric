package fr.quentin.fullbright.option;

import fr.quentin.fullbright.Fullbright;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * Manages key bindings for the Fullbright mod.
 * Provides functionality to register key bindings for mod-specific actions.
 */
public class KeyBindings {
    /**
     * Key binding for toggling the configuration.
     * This key binding allows users to quickly access the configuration options.
     */
    public static KeyBinding configurationKey;

    /**
     * Key binding for opening the text editor.
     * This key binding allows users to quickly access the text editor.
     */
    public static KeyBinding textEditorKey;

    /**
     * Registers the key bindings for the mod.
     * This method should be called during the mod's initialization to ensure key bindings are available.
     */
    public static void register() {
        // Register a new key binding for the configuration action
        configurationKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        Fullbright.MOD_ID + ".configuration_keybinding", // Unique identifier for the key binding
                        InputUtil.Type.KEYSYM, // Type of input (keyboard key)
                        GLFW.GLFW_KEY_G, // Default key (G key)
                        "key.category.fullbright" // Category for the key binding in the controls menu
                )
        );

        // Register a new key binding for the text editor action
        textEditorKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        Fullbright.MOD_ID + ".text_editor_keybinding", // Unique identifier for the key binding
                        InputUtil.Type.KEYSYM, // Type of input (keyboard key)
                        GLFW.GLFW_KEY_N, // Default key (N key)
                        "key.category.fullbright" // Category for the key binding in the controls menu
                )
        );
    }
}
