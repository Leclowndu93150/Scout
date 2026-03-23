package pm.c7.scout.client;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.inventory.InventoryMenu;

public class ScoutUtilClient {
	public static @Nullable InventoryMenu getPlayerScreenHandler() {
		var client = Minecraft.getInstance();
		if (client != null && client.player != null) {
			return client.player.inventoryMenu;
		}
		return null;
	}

	public static boolean isScreenAllowed(Screen screen) {
		return screen instanceof InventoryScreen
			|| screen instanceof CraftingScreen
			|| screen instanceof AbstractFurnaceScreen
			|| screen instanceof ContainerScreen;
	}
}
