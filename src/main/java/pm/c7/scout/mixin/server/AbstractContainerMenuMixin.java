package pm.c7.scout.mixin.server;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import pm.c7.scout.server.ScoutUtilServer;

@Environment(EnvType.SERVER)
@Mixin(value = AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {
	@Redirect(method = "doClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;quickMoveStack(Lnet/minecraft/world/entity/player/Player;I)Lnet/minecraft/world/item/ItemStack;"))
	public ItemStack scout$fixQuickMove(AbstractContainerMenu self, Player player, int index, int slotIndex, int button, ClickType actionType, Player playerAgain) {
		ScoutUtilServer.setCurrentPlayer(player);
		ItemStack ret = self.quickMoveStack(player, index);
		ScoutUtilServer.clearCurrentPlayer();

		return ret;
	}
}
