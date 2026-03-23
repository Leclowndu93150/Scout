package pm.c7.scout.fabric.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.GameRules;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pm.c7.scout.ScoutNetworking;
import pm.c7.scout.ScoutScreenHandler;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.item.BaseBagItem;
import pm.c7.scout.item.BaseBagItem.BagType;
import pm.c7.scout.item.IBagItem;
import pm.c7.scout.screen.BagSlot;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
	@Inject(method = "die", at = @At("HEAD"))
	private void scout$attemptFixGraveMods(DamageSource source, CallbackInfo callbackInfo) {
		ServerPlayer player = (ServerPlayer) (Object) this;
		ScoutScreenHandler handler = (ScoutScreenHandler) player.inventoryMenu;

		if (!player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
			ItemStack backStack = ScoutUtil.findBagItem(player, BagType.SATCHEL, false);
			if (!backStack.isEmpty()) {
				IBagItem bagItem = (IBagItem) backStack.getItem();
				int slots = bagItem.getSlotCount();

				NonNullList<BagSlot> bagSlots = handler.scout$getSatchelSlots();

				for (int i = 0; i < slots; i++) {
					BagSlot slot = bagSlots.get(i);
					slot.setInventory(null);
					slot.setEnabled(false);
				}
			}

			ItemStack leftPouchStack = ScoutUtil.findBagItem(player, BagType.POUCH, false);
			if (!leftPouchStack.isEmpty()) {
				IBagItem bagItem = (IBagItem) leftPouchStack.getItem();
				int slots = bagItem.getSlotCount();

				NonNullList<BagSlot> bagSlots = handler.scout$getLeftPouchSlots();

				for (int i = 0; i < slots; i++) {
					BagSlot slot = bagSlots.get(i);
					slot.setInventory(null);
					slot.setEnabled(false);
				}
			}

			ItemStack rightPouchStack = ScoutUtil.findBagItem(player, BagType.POUCH, true);
			if (!rightPouchStack.isEmpty()) {
				IBagItem bagItem = (IBagItem) rightPouchStack.getItem();
				int slots = bagItem.getSlotCount();

				NonNullList<BagSlot> bagSlots = handler.scout$getRightPouchSlots();

				for (int i = 0; i < slots; i++) {
					BagSlot slot = bagSlots.get(i);
					slot.setInventory(null);
					slot.setEnabled(false);
				}
			}

			ServerPlayNetworking.send(player, new ScoutNetworking.EnableSlotsPayload());
		}
	}
}
