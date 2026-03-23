package pm.c7.scout.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pm.c7.scout.ScoutMixin;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.client.ScoutUtilClient;
import pm.c7.scout.item.BaseBagItem;
import pm.c7.scout.screen.BagSlot;

@Environment(EnvType.CLIENT)
@ScoutMixin.Transformer(HandledScreenTransformer.class)
@Mixin(value = AbstractContainerScreen.class, priority = 950)
public abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T> {
	protected AbstractContainerScreenMixin() {
		super(null);
	}

	@Shadow
	@Nullable
	protected Slot hoveredSlot;
	@Shadow
	protected int leftPos;
	@Shadow
	protected int topPos;
	@Shadow
	protected int imageWidth;
	@Shadow
	protected int imageHeight;
	@Shadow
	protected T menu;

	@Inject(method = "renderBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderBg(Lnet/minecraft/client/gui/GuiGraphics;FII)V"))
	private void scout$drawSatchelRow(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if (this.minecraft != null && this.minecraft.player != null && ScoutUtilClient.isScreenAllowed(this)) {
			var playerInventory = this.minecraft.player.getInventory();

			ItemStack backStack = ScoutUtil.findBagItem(this.minecraft.player, BaseBagItem.BagType.SATCHEL, false);
			if (!backStack.isEmpty()) {
				BaseBagItem bagItem = (BaseBagItem) backStack.getItem();
				int slots = bagItem.getSlotCount();

				var _hotbarSlot1 = menu.slots.stream().filter(slot->slot.container.equals(playerInventory) && slot.getContainerSlot() == 0).findFirst();
				Slot hotbarSlot1 = _hotbarSlot1.isPresent() ? _hotbarSlot1.get() : null;
				if (hotbarSlot1 != null) {
					int x = this.leftPos + hotbarSlot1.x - 8;
					int y = this.topPos + hotbarSlot1.y + 22;

					graphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);

					graphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 0, 32, 176, 4);
					y += 4;

					int u = 0;
					int v = 36;

					for (int slot = 0; slot < slots; slot++) {
						if (slot % 9 == 0) {
							x = this.leftPos + hotbarSlot1.x - 8;
							u = 0;
							graphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, u, v, 7, 18);
							x += 7;
							u += 7;
						}

						graphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, u, v, 18, 18);

						x += 18;
						u += 18;

						if ((slot + 1) % 9 == 0) {
							graphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, u, v, 7, 18);
							y += 18;
						}
					}

					x = this.leftPos + hotbarSlot1.x - 8;
					graphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 0, 54, 176, 7);

					graphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
				}
			}
		}
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;disableDepthTest()V", remap = false))
	private void scout$drawPouchSlots(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if (this.minecraft != null && this.minecraft.player != null && ScoutUtilClient.isScreenAllowed(this)) {
			var playerInventory = this.minecraft.player.getInventory();

			ItemStack leftPouchStack = ScoutUtil.findBagItem(this.minecraft.player, BaseBagItem.BagType.POUCH, false);
			if (!leftPouchStack.isEmpty()) {
				BaseBagItem bagItem = (BaseBagItem) leftPouchStack.getItem();
				int slots = bagItem.getSlotCount();
				int columns = (int) Math.ceil(slots / 3);

				var _topLeftSlot = menu.slots.stream().filter(slot->slot.container.equals(playerInventory) && slot.getContainerSlot() == 9).findFirst();
				Slot topLeftSlot = _topLeftSlot.isPresent() ? _topLeftSlot.get() : null;
				if (topLeftSlot != null) {
					int x = this.leftPos + topLeftSlot.x - 8;
					int y = this.topPos + topLeftSlot.y + 53;

					graphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);

					graphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 18, 25, 7, 7);
					for (int i = 0; i < columns; i++) {
						x -= 11;
						graphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 7, 25, 11, 7);
					}
					if (columns > 1) {
						for (int i = 0; i < columns - 1; i++) {
							x -= 7;
							graphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 7, 25, 7, 7);
						}
					}
					x -= 7;
					graphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 0, 25, 7, 7);

					x = this.leftPos + topLeftSlot.x - 1;
					y -= 54;
					for (int slot = 0; slot < slots; slot++) {
						if (slot % 3 == 0) {
							x -= 18;
							y += 54;
						}
						y -= 18;
						graphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 7, 7, 18, 18);
					}

					x -= 7;
					y += 54;
					for (int i = 0; i < 3; i++) {
						y -= 18;
						graphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 0, 7, 7, 18);
					}

					x = this.leftPos + topLeftSlot.x - 8;
					y -= 7;
					graphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 18, 0, 7, 7);
					for (int i = 0; i < columns; i++) {
						x -= 11;
						graphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 7, 0, 11, 7);
					}
					if (columns > 1) {
						for (int i = 0; i < columns - 1; i++) {
							x -= 7;
							graphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 7, 0, 7, 7);
						}
					}
					x -= 7;
					graphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 0, 0, 7, 7);

					graphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
				}
			}

			ItemStack rightPouchStack = ScoutUtil.findBagItem(this.minecraft.player, BaseBagItem.BagType.POUCH, true);
			if (!rightPouchStack.isEmpty()) {
				BaseBagItem bagItem = (BaseBagItem) rightPouchStack.getItem();
				int slots = bagItem.getSlotCount();
				int columns = (int) Math.ceil(slots / 3);

				var _topRightSlot = menu.slots.stream().filter(slot->slot.container.equals(playerInventory) && slot.getContainerSlot() == 17).findFirst();
				Slot topRightSlot = _topRightSlot.isPresent() ? _topRightSlot.get() : null;
				if (topRightSlot != null) {
					int x = this.leftPos + topRightSlot.x + 17;
					int y = this.topPos + topRightSlot.y + 53;

					graphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);

					graphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 25, 25, 7, 7);
					x += 7;
					for (int i = 0; i < columns; i++) {
						graphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 7, 25, 11, 7);
						x += 11;
					}
					if (columns > 1) {
						for (int i = 0; i < columns - 1; i++) {
							graphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 7, 25, 7, 7);
							x += 7;
						}
					}
					graphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 32, 25, 7, 7);

					x = this.leftPos + topRightSlot.x - 1;
					y -= 54;
					for (int slot = 0; slot < slots; slot++) {
						if (slot % 3 == 0) {
							x += 18;
							y += 54;
						}
						y -= 18;
						graphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 7, 7, 18, 18);
					}

					x += 18;
					y += 54;
					for (int i = 0; i < 3; i++) {
						y -= 18;
						graphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 32, 7, 7, 18);
					}

					x = this.leftPos + topRightSlot.x + 17;
					y -= 7;
					graphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 25, 0, 7, 7);
					x += 7;
					for (int i = 0; i < columns; i++) {
						graphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 7, 0, 11, 7);
						x += 11;
					}
					if (columns > 1) {
						for (int i = 0; i < columns - 1; i++) {
							graphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 7, 0, 7, 7);
							x += 7;
						}
					}
					graphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 32, 0, 7, 7);

					graphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
				}
			}
		}
	}

	@Inject(method = "hasClickedOutside", at = @At("TAIL"), cancellable = true)
	private void scout$adjustOutsideBounds(double mouseX, double mouseY, int left, int top, int button, CallbackInfoReturnable<Boolean> callbackInfo) {
		if (this.minecraft != null && this.minecraft.player != null && ScoutUtilClient.isScreenAllowed(this)) {
			ItemStack backStack = ScoutUtil.findBagItem(this.minecraft.player, BaseBagItem.BagType.SATCHEL, false);
			if (!backStack.isEmpty()) {
				BaseBagItem bagItem = (BaseBagItem) backStack.getItem();
				int slots = bagItem.getSlotCount();
				int rows = (int) Math.ceil(slots / 9);

				if (mouseY < (top + this.imageHeight) + 8 + (18 * rows) && mouseY >= (top + this.imageHeight) && mouseX >= left && mouseY < (left + this.imageWidth)) {
					callbackInfo.setReturnValue(false);
				}
			}

			ItemStack leftPouchStack = ScoutUtil.findBagItem(this.minecraft.player, BaseBagItem.BagType.POUCH, false);
			if (!leftPouchStack.isEmpty()) {
				BaseBagItem bagItem = (BaseBagItem) leftPouchStack.getItem();
				int slots = bagItem.getSlotCount();
				int columns = (int) Math.ceil(slots / 3);

				if (mouseX >= left - (columns * 18) && mouseX < left && mouseY >= (top + this.imageHeight) - 90 && mouseY < (top + this.imageHeight) - 22) {
					callbackInfo.setReturnValue(false);
				}
			}

			ItemStack rightPouchStack = ScoutUtil.findBagItem(this.minecraft.player, BaseBagItem.BagType.POUCH, true);
			if (!rightPouchStack.isEmpty()) {
				BaseBagItem bagItem = (BaseBagItem) rightPouchStack.getItem();
				int slots = bagItem.getSlotCount();
				int columns = (int) Math.ceil(slots / 3);

				if (mouseX >= (left + this.imageWidth) && mouseX < (left + this.imageWidth) + (columns * 18) && mouseY >= (top + this.imageHeight) - 90 && mouseY < (top + this.imageHeight) - 22) {
					callbackInfo.setReturnValue(false);
				}
			}
		}
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderLabels(Lnet/minecraft/client/gui/GuiGraphics;II)V"))
	public void scout$drawOurSlots(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if (this.minecraft != null && this.minecraft.player != null && ScoutUtilClient.isScreenAllowed(this)) {
			for (int i = ScoutUtil.SATCHEL_SLOT_START; i > ScoutUtil.BAG_SLOTS_END; i--) {
				BagSlot slot = (BagSlot) ScoutUtil.getBagSlot(i, this.minecraft.player.inventoryMenu);
				if (slot != null && slot.isActive()) {
					this.renderSlot(graphics, slot);
				}

				if (this.isHovering(slot, mouseX, mouseY) && slot != null && slot.isActive()) {
					this.hoveredSlot = slot;
					int slotX = slot.getX();
					int slotY = slot.getY();
					renderSlotHighlight(graphics, slotX, slotY, 0);
				}
			}
		}
	}

	@Inject(method = "isHovering", at = @At("HEAD"), cancellable = true)
	public void scout$fixSlotPos(Slot slot, double pointX, double pointY, CallbackInfoReturnable<Boolean> cir) {
		if (slot instanceof BagSlot bagSlot) {
			cir.setReturnValue(this.isHovering(bagSlot.getX(), bagSlot.getY(), 16, 16, pointX, pointY));
		}
	}

	@Inject(method = "findSlot", at = @At("RETURN"), cancellable = true)
	public void scout$addSlots(double x, double y, CallbackInfoReturnable<Slot> cir) {
		if (this.minecraft != null && this.minecraft.player != null && ScoutUtilClient.isScreenAllowed(this)) {
			for (int i = ScoutUtil.SATCHEL_SLOT_START; i > ScoutUtil.BAG_SLOTS_END; i--) {
				BagSlot slot = (BagSlot) ScoutUtil.getBagSlot(i, this.minecraft.player.inventoryMenu);
				if (this.isHovering(slot, x, y) && slot != null && slot.isActive()) {
					cir.setReturnValue(slot);
				}
			}
		}
	}

	@Shadow
	private void renderSlot(GuiGraphics graphics, Slot slot) {}
	@Shadow
	public static void renderSlotHighlight(GuiGraphics graphics, int x, int y, int z) {}
	@Shadow
	private boolean isHovering(Slot slot, double pointX, double pointY) {
		return false;
	}
	@Shadow
	protected boolean isHovering(int x, int y, int width, int height, double pointX, double pointY) {
		return false;
	}
}
