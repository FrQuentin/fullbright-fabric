package fr.quentin.fullbright;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main mod class for the Fullbright modification.
 * Handles initialization of the mod's core systems.
 */
public class Fullbright implements ModInitializer {
	/**
	 * The mod identifier used for resources and registration.
	 * This ID is used to uniquely identify the mod and its resources within Minecraft.
	 */
	public static final String MOD_ID = "fullbright";

	/**
	 * Logger instance for mod-specific logging.
	 * Used to log information, warnings, and errors related to the mod.
	 */
	public static final Logger LOGGER = LoggerFactory.getLogger("Fullbright");

	/**
	 * Called when the mod is initialized.
	 * This method is part of the Fabric mod lifecycle and is where the mod's core systems should be set up.
	 */
	@Override
	public void onInitialize() {

	}
}
