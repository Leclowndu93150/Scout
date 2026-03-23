package pm.c7.scout.client.compat;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.widget.Bounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.client.ScoutUtilClient;
import pm.c7.scout.item.BaseBagItem;
import pm.c7.scout.item.BaseBagItem.BagType;
import pm.c7.scout.mixin.client.AbstractContainerScreenAccessor;

public class ScoutEmiPlugin implements EmiPlugin {
	@Override
	public void register(EmiRegistry registry) {
		registry.addGenericExclusionArea((screen, consumer) -> {
			if (!(screen instanceof AbstractContainerScreen<?> handledScreen)) return;
			if (!ScoutUtilClient.isScreenAllowed(screen)) return;

			Minecraft client = Minecraft.getInstance();

			var handledScreenAccessor = (AbstractContainerScreenAccessor<?>) handledScreen;
			AbstractContainerMenu handler = handledScreenAccessor.getMenu();
			var sx = handledScreenAccessor.getLeftPos();
			var sy = handledScreenAccessor.getTopPos();
			var sw = handledScreenAccessor.getImageWidth();
			var sh = handledScreenAccessor.getImageHeight();

			var playerInventory = client.player.getInventory();

			ItemStack satchelStack = ScoutUtil.findBagItem(client.player, BagType.SATCHEL, false);
			if (!satchelStack.isEmpty()) {
				BaseBagItem bagItem = (BaseBagItem) satchelStack.getItem();
				int slots = bagItem.getSlotCount();
				int rows = (int) Math.ceil(slots / 9);

				var _hotbarSlot1 = handler.slots.stream().filter(slot->slot.container.equals(playerInventory) && slot.getContainerSlot() == 0).findFirst();
				Slot hotbarSlot1 = _hotbarSlot1.isPresent() ? _hotbarSlot1.get() : null;
				if (hotbarSlot1 != null) {
					if (hotbarSlot1.isActive()) {
						int x = sx + hotbarSlot1.x - 8;
						int y = sy + hotbarSlot1.y + 22;

						int w = 176;
						int h = (rows * 18) + 8;

						consumer.accept(new Bounds(x, y, w, h));
					}
				}
			}

			ItemStack leftPouchStack = ScoutUtil.findBagItem(client.player, BagType.POUCH, false);
			if (!leftPouchStack.isEmpty()) {
				BaseBagItem bagItem = (BaseBagItem) leftPouchStack.getItem();
				int slots = bagItem.getSlotCount();
				int columns = (int) Math.ceil(slots / 3);

				var _topLeftSlot = handler.slots.stream().filter(slot->slot.container.equals(playerInventory) && slot.getContainerSlot() == 9).findFirst();
				Slot topLeftSlot = _topLeftSlot.isPresent() ? _topLeftSlot.get() : null;
				if (topLeftSlot != null) {
					if (topLeftSlot.isActive()) {
						int x = sx + topLeftSlot.x - 7 - (columns * 18);
						int y = sy + topLeftSlot.y;

						int w = (columns * 18) + 7;
						int h = 68;

						consumer.accept(new Bounds(x, y, w, h));
					}
				}
			}

			ItemStack rightPouchStack = ScoutUtil.findBagItem(client.player, BagType.POUCH, true);
			if (!rightPouchStack.isEmpty()) {
				BaseBagItem bagItem = (BaseBagItem) rightPouchStack.getItem();
				int slots = bagItem.getSlotCount();
				int columns = (int) Math.ceil(slots / 3);

				var _topRightSlot = handler.slots.stream().filter(slot->slot.container.equals(playerInventory) && slot.getContainerSlot() == 17).findFirst();
				Slot topRightSlot = _topRightSlot.isPresent() ? _topRightSlot.get() : null;
				if (topRightSlot != null) {
					if (topRightSlot.isActive()) {
						int x = sx + topRightSlot.x;
						int y = sy + topRightSlot.y;

						int w = (columns * 18) + 7;
						int h = 68;

						consumer.accept(new Bounds(x, y, w, h));
					}
				}
			}
		});
	}

}
