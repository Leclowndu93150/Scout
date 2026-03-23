package pm.c7.scout.mixin;

import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.config.ScoutConfig;
import pm.c7.scout.item.BaseBagItem;

@Mixin(BowItem.class)
public class BowItemMixin {
	@Inject(method = "releaseUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void scout$arrowsFromBags(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks, CallbackInfo ci, Player playerEntity, ItemStack itemStack, int i, float f) {
		if (ScoutConfig.useArrows) {
			boolean infinity = itemStack.is(Items.ARROW) && playerEntity.getAbilities().instabuild;
			boolean hasRan = false;

			if (!infinity && !playerEntity.getAbilities().instabuild) {
				var leftPouch = ScoutUtil.findBagItem(playerEntity, BaseBagItem.BagType.POUCH, false);
				var rightPouch = ScoutUtil.findBagItem(playerEntity, BaseBagItem.BagType.POUCH, true);
				var satchel = ScoutUtil.findBagItem(playerEntity, BaseBagItem.BagType.SATCHEL, false);

				if (!leftPouch.isEmpty()) {
					BaseBagItem item = (BaseBagItem) leftPouch.getItem();
					var inv = item.getInventory(leftPouch);

					for(int s = 0; s < inv.getContainerSize(); ++s) {
						ItemStack invStack = inv.getItem(s);
						if (ItemStack.matches(invStack, itemStack)) {
							invStack.shrink(1);
							if (invStack.isEmpty()) {
								inv.setItem(s, ItemStack.EMPTY);
							}
							inv.setChanged();
							hasRan = true;
							break;
						}
					}
				}
				if (!rightPouch.isEmpty() && !hasRan) {
					BaseBagItem item = (BaseBagItem) rightPouch.getItem();
					var inv = item.getInventory(rightPouch);

					for(int s = 0; s < inv.getContainerSize(); ++s) {
						ItemStack invStack = inv.getItem(s);
						if (ItemStack.matches(invStack, itemStack)) {
							invStack.shrink(1);
							if (invStack.isEmpty()) {
								inv.setItem(s, ItemStack.EMPTY);
							}
							inv.setChanged();
							hasRan = true;
							break;
						}
					}
				}
				if (!satchel.isEmpty() && !hasRan) {
					BaseBagItem item = (BaseBagItem) satchel.getItem();
					var inv = item.getInventory(satchel);

					for(int s = 0; s < inv.getContainerSize(); ++s) {
						ItemStack invStack = inv.getItem(s);
						if (ItemStack.matches(invStack, itemStack)) {
							invStack.shrink(1);
							if (invStack.isEmpty()) {
								inv.setItem(s, ItemStack.EMPTY);
							}
							inv.setChanged();
							hasRan = true;
							break;
						}
					}
				}
			}
		}
	}
}
