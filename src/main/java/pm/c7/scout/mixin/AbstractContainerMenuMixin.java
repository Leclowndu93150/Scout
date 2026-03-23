package pm.c7.scout.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.core.NonNullList;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import pm.c7.scout.ScoutMixin.Transformer;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.screen.BagSlot;

@Mixin(value = AbstractContainerMenu.class, priority = 950)
@Transformer(ScreenHandlerTransformer.class)
public abstract class AbstractContainerMenuMixin {
	@Inject(method = "clicked", at = @At("HEAD"), cancellable = true)
	private void scout$handleBagSlotClick(int slotIndex, int button, ClickType actionType, Player player, CallbackInfo ci) {
		if (!ScoutUtil.isBagSlot(slotIndex)) return;

		Slot slot = ScoutUtil.getBagSlot(slotIndex, player.inventoryMenu);
		if (slot == null) {
			ci.cancel();
			return;
		}

		switch (actionType) {
			case PICKUP -> {
				ItemStack cursorStack = this.getCarried();
				ItemStack slotStack = slot.getItem();

				if (cursorStack.isEmpty()) {
					if (!slotStack.isEmpty() && slot.mayPickup(player)) {
						int takeCount = button == 0 ? slotStack.getCount() : (slotStack.getCount() + 1) / 2;
						ItemStack taken = slot.remove(takeCount);
						this.setCarried(taken);
						slot.setChanged();
					}
				} else {
					if (slotStack.isEmpty()) {
						if (slot.mayPlace(cursorStack)) {
							int placeCount = button == 0 ? cursorStack.getCount() : 1;
							placeCount = Math.min(placeCount, slot.getMaxStackSize());
							slot.set(cursorStack.split(placeCount));
						}
					} else if (slot.mayPlace(cursorStack) && ItemStack.isSameItemSameComponents(slotStack, cursorStack)) {
						int placeCount = button == 0 ? cursorStack.getCount() : 1;
						int maxCount = Math.min(slot.getMaxStackSize(), cursorStack.getMaxStackSize());
						int room = maxCount - slotStack.getCount();
						placeCount = Math.min(placeCount, room);
						cursorStack.shrink(placeCount);
						slotStack.grow(placeCount);
						slot.set(slotStack);
					} else if (slot.mayPickup(player) && slot.mayPlace(cursorStack)) {
						if (cursorStack.getCount() <= slot.getMaxStackSize()) {
							slot.set(cursorStack);
							this.setCarried(slotStack);
						}
					}
				}
				ci.cancel();
			}
			case THROW -> {
				if (!this.getCarried().isEmpty()) {
					ci.cancel();
					return;
				}
				ItemStack slotStack = slot.getItem();
				if (!slotStack.isEmpty() && slot.mayPickup(player)) {
					int dropCount = button == 0 ? 1 : slotStack.getCount();
					ItemStack dropped = slot.remove(dropCount);
					player.drop(dropped, true);
					slot.setChanged();
				}
				ci.cancel();
			}
			case SWAP -> {
				Inventory inv = player.getInventory();
				ItemStack hotbarStack = button == 40 ? inv.getItem(40) : inv.getItem(button);
				ItemStack slotStack = slot.getItem();

				if (!hotbarStack.isEmpty() || !slotStack.isEmpty()) {
					if (hotbarStack.isEmpty()) {
						if (slot.mayPickup(player)) {
							if (button == 40) inv.setItem(40, slotStack);
							else inv.setItem(button, slotStack);
							slot.set(ItemStack.EMPTY);
						}
					} else if (slotStack.isEmpty()) {
						if (slot.mayPlace(hotbarStack)) {
							int maxCount = slot.getMaxStackSize();
							if (hotbarStack.getCount() > maxCount) {
								slot.set(hotbarStack.split(maxCount));
							} else {
								if (button == 40) inv.setItem(40, ItemStack.EMPTY);
								else inv.setItem(button, ItemStack.EMPTY);
								slot.set(hotbarStack);
							}
						}
					} else if (slot.mayPickup(player) && slot.mayPlace(hotbarStack)) {
						int maxCount = slot.getMaxStackSize();
						if (hotbarStack.getCount() > maxCount) {
							slot.set(hotbarStack.split(maxCount));
							if (!inv.add(slotStack)) {
								player.drop(slotStack, true);
							}
						} else {
							if (button == 40) inv.setItem(40, slotStack);
							else inv.setItem(button, slotStack);
							slot.set(hotbarStack);
						}
					}
				}
				ci.cancel();
			}
			case QUICK_MOVE -> {
				ItemStack slotStack = slot.getItem();
				if (!slotStack.isEmpty() && slot.mayPickup(player)) {
					ItemStack taken = slot.remove(slotStack.getCount());
					ItemStack remainder = this.insertItem(taken, player);
					if (!remainder.isEmpty()) {
						slot.set(remainder);
					}
					slot.setChanged();
				}
				ci.cancel();
			}
			default -> ci.cancel();
		}
	}

	private ItemStack insertItem(ItemStack stack, Player player) {
		Inventory inv = player.getInventory();
		for (int i = 0; i < 36 && !stack.isEmpty(); i++) {
			ItemStack invStack = inv.getItem(i);
			if (ItemStack.isSameItemSameComponents(invStack, stack)) {
				int room = invStack.getMaxStackSize() - invStack.getCount();
				int toMove = Math.min(stack.getCount(), room);
				invStack.grow(toMove);
				stack.shrink(toMove);
			}
		}
		for (int i = 0; i < 36 && !stack.isEmpty(); i++) {
			if (inv.getItem(i).isEmpty()) {
				inv.setItem(i, stack.copy());
				stack.setCount(0);
			}
		}
		return stack;
	}

	@Inject(method = "doClick", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;getCarried()Lnet/minecraft/world/item/ItemStack;", ordinal = 11), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	public void scout$fixDoubleClick(int slotIndex, int button, ClickType actionType, Player player, CallbackInfo ci, Inventory playerInventory, Slot slot3) {
		var cursorStack = this.getCarried();
		if (!cursorStack.isEmpty() && (!slot3.hasItem() || !slot3.mayPickup(player))) {
			var slots = ScoutUtil.getAllBagSlots(player.inventoryMenu);
			var k = button == 0 ? 0 : ScoutUtil.TOTAL_SLOTS - 1;
			var o = button == 0 ? 1 : -1;

			for (int n = 0; n < 2; ++n) {
				for (int p = k; p >= 0 && p < slots.size() && cursorStack.getCount() < cursorStack.getMaxStackSize(); p += o) {
					Slot slot4 = slots.get(p);
					if (slot4.hasItem() && canItemQuickReplace(slot4, cursorStack, true) && slot4.mayPickup(player) && this.canTakeItemForPickAll(cursorStack, slot4)) {
						ItemStack itemStack6 = slot4.getItem();
						if (n != 0 || itemStack6.getCount() != itemStack6.getMaxStackSize()) {
							ItemStack itemStack7 = slot4.safeTake(itemStack6.getCount(), cursorStack.getMaxStackSize() - cursorStack.getCount(), player);
							cursorStack.grow(itemStack7.getCount());
						}
					}
				}
			}
		}
	}

	@Dynamic("Workaround for Debugify. Other calls are modified via the attached transformer class.")
	@Redirect(method = "doClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/NonNullList;get(I)Ljava/lang/Object;", ordinal = 5))
	public Object scout$fixSlotIndexing(NonNullList<Slot> self, int index, int slotIndex, int button, ClickType actionType, Player player) {
		if (ScoutUtil.isBagSlot(index)) {
			return ScoutUtil.getBagSlot(index, player.inventoryMenu);
		} else {
			return self.get(index);
		}
	}

	@Inject(method = "canDragTo(Lnet/minecraft/world/inventory/Slot;)Z", at = @At("HEAD"), cancellable = true)
	private void scout$preventBagSlotDrag(Slot slot, CallbackInfoReturnable<Boolean> cir) {
		if (slot instanceof BagSlot) {
			cir.setReturnValue(false);
		}
	}

	@Shadow
	public static boolean canItemQuickReplace(@Nullable Slot slot, ItemStack stack, boolean allowOverflow) {
		return false;
	}
	@Shadow
	public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
		return true;
	}
	@Shadow
	public abstract ItemStack getCarried();
	@Shadow
	public abstract void setCarried(ItemStack stack);
}
