package pm.c7.scout.neoforge.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.core.NonNullList;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.server.ScoutUtilServer;

@OnlyIn(Dist.DEDICATED_SERVER)
@Mixin(NonNullList.class)
public class NonNullListMixin {
	@Inject(method = "get", at = @At("HEAD"), cancellable = true)
	public void scout$fixIndexingSlots(int index, CallbackInfoReturnable<Object> cir) {
		var currentPlayer = ScoutUtilServer.getCurrentPlayer();
		if (ScoutUtil.isBagSlot(index)) {
			if (currentPlayer != null) {
				cir.setReturnValue(ScoutUtil.getBagSlot(index, currentPlayer.inventoryMenu));
			} else {
				cir.setReturnValue(null);
			}
		}
	}
}
