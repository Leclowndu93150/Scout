package pm.c7.scout.client.compat;

import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.client.ScoutUtilClient;
import pm.c7.scout.item.BaseBagItem;
import pm.c7.scout.item.BaseBagItem.BagType;
import pm.c7.scout.item.IBagItem;

import java.util.ArrayList;
import java.util.List;

public class ScoutGuiHandler implements IGuiContainerHandler<AbstractContainerScreen<?>> {
	@Override
	public List<Rect2i> getGuiExtraAreas(AbstractContainerScreen<?> screen) {
		List<Rect2i> areas = new ArrayList<>();

		if (!ScoutUtilClient.isScreenAllowed(screen)) return areas;

		Minecraft client = Minecraft.getInstance();
		if (client.player == null) return areas;

		var accessor = (pm.c7.scout.mixin.client.AbstractContainerScreenAccessor<?>) screen;
		AbstractContainerMenu handler = accessor.getMenu();
		int sx = accessor.getLeftPos();
		int sy = accessor.getTopPos();

		var playerInventory = client.player.getInventory();

		ItemStack satchelStack = ScoutUtil.findBagItem(client.player, BagType.SATCHEL, false);
		if (!satchelStack.isEmpty()) {
			IBagItem bagItem = (IBagItem) satchelStack.getItem();
			int slots = bagItem.getSlotCount();
			int rows = (int) Math.ceil(slots / 9.0);

			var hotbarSlot = handler.slots.stream().filter(slot -> slot.container.equals(playerInventory) && slot.getContainerSlot() == 0).findFirst();
			if (hotbarSlot.isPresent() && hotbarSlot.get().isActive()) {
				Slot slot = hotbarSlot.get();
				int x = sx + slot.x - 8;
				int y = sy + slot.y + 22;
				areas.add(new Rect2i(x, y, 176, (rows * 18) + 8));
			}
		}

		ItemStack leftPouchStack = ScoutUtil.findBagItem(client.player, BagType.POUCH, false);
		if (!leftPouchStack.isEmpty()) {
			IBagItem bagItem = (IBagItem) leftPouchStack.getItem();
			int slots = bagItem.getSlotCount();
			int columns = (int) Math.ceil(slots / 3.0);

			var topLeftSlot = handler.slots.stream().filter(slot -> slot.container.equals(playerInventory) && slot.getContainerSlot() == 9).findFirst();
			if (topLeftSlot.isPresent() && topLeftSlot.get().isActive()) {
				Slot slot = topLeftSlot.get();
				int x = sx + slot.x - 7 - (columns * 18);
				int y = sy + slot.y;
				areas.add(new Rect2i(x, y, (columns * 18) + 7, 68));
			}
		}

		ItemStack rightPouchStack = ScoutUtil.findBagItem(client.player, BagType.POUCH, true);
		if (!rightPouchStack.isEmpty()) {
			IBagItem bagItem = (IBagItem) rightPouchStack.getItem();
			int slots = bagItem.getSlotCount();
			int columns = (int) Math.ceil(slots / 3.0);

			var topRightSlot = handler.slots.stream().filter(slot -> slot.container.equals(playerInventory) && slot.getContainerSlot() == 17).findFirst();
			if (topRightSlot.isPresent() && topRightSlot.get().isActive()) {
				Slot slot = topRightSlot.get();
				int x = sx + slot.x;
				int y = sy + slot.y;
				areas.add(new Rect2i(x, y, (columns * 18) + 7, 68));
			}
		}

		return areas;
	}
}
