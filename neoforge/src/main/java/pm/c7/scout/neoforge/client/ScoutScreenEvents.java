package pm.c7.scout.neoforge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.client.ScoutUtilClient;
import pm.c7.scout.mixin.client.AbstractContainerScreenAccessor;
import pm.c7.scout.screen.BagSlot;

@EventBusSubscriber(modid = ScoutUtil.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class ScoutScreenEvents {

	@SubscribeEvent
	public static void onScreenInit(ScreenEvent.Init.Post event) {
		if (!(event.getScreen() instanceof AbstractContainerScreen<?> handledScreen)) return;
		Minecraft client = Minecraft.getInstance();
		if (client.player == null) return;

		if (!ScoutUtilClient.isScreenAllowed(event.getScreen())) {
			for (Slot slot : ScoutUtil.getAllBagSlots(client.player.inventoryMenu)) {
				BagSlot bagSlot = (BagSlot) slot;
				bagSlot.setX(Integer.MAX_VALUE);
				bagSlot.setY(Integer.MAX_VALUE);
			}
			return;
		}

		var handledScreenAccessor = (AbstractContainerScreenAccessor<?>) handledScreen;
		AbstractContainerMenu handler = handledScreenAccessor.getMenu();
		var playerInventory = client.player.getInventory();

		int x = 0;
		int y = 0;

		var _hotbarSlot1 = handler.slots.stream().filter(slot->slot.container.equals(playerInventory) && slot.getContainerSlot() == 0).findFirst();
		Slot hotbarSlot1 = _hotbarSlot1.isPresent() ? _hotbarSlot1.get() : null;
		if (hotbarSlot1 != null) {
			if (!hotbarSlot1.isActive()) {
				for (int i = 0; i < ScoutUtil.MAX_SATCHEL_SLOTS; i++) {
					BagSlot slot = (BagSlot) ScoutUtil.getBagSlot(ScoutUtil.SATCHEL_SLOT_START - i, client.player.inventoryMenu);
					if (slot != null) { slot.setX(Integer.MAX_VALUE); slot.setY(Integer.MAX_VALUE); }
				}
			} else {
				x = hotbarSlot1.x;
				y = hotbarSlot1.y + 27;
				for (int i = 0; i < ScoutUtil.MAX_SATCHEL_SLOTS; i++) {
					if (i % 9 == 0) { x = hotbarSlot1.x; }
					BagSlot slot = (BagSlot) ScoutUtil.getBagSlot(ScoutUtil.SATCHEL_SLOT_START - i, client.player.inventoryMenu);
					if (slot != null) { slot.setX(x); slot.setY(y); }
					x += 18;
					if ((i + 1) % 9 == 0) { y += 18; }
				}
			}
		}

		var _topLeftSlot = handler.slots.stream().filter(slot->slot.container.equals(playerInventory) && slot.getContainerSlot() == 9).findFirst();
		Slot topLeftSlot = _topLeftSlot.isPresent() ? _topLeftSlot.get() : null;
		if (topLeftSlot != null) {
			if (!topLeftSlot.isActive()) {
				for (int i = 0; i < ScoutUtil.MAX_POUCH_SLOTS; i++) {
					BagSlot slot = (BagSlot) ScoutUtil.getBagSlot(ScoutUtil.LEFT_POUCH_SLOT_START - i, client.player.inventoryMenu);
					if (slot != null) { slot.setX(Integer.MAX_VALUE); slot.setY(Integer.MAX_VALUE); }
				}
			} else {
				x = topLeftSlot.x;
				y = topLeftSlot.y - 18;
				for (int i = 0; i < ScoutUtil.MAX_POUCH_SLOTS; i++) {
					if (i % 3 == 0) { x -= 18; y += 54; }
					BagSlot slot = (BagSlot) ScoutUtil.getBagSlot(ScoutUtil.LEFT_POUCH_SLOT_START - i, client.player.inventoryMenu);
					if (slot != null) { slot.setX(x); slot.setY(y); }
					y -= 18;
				}
			}
		}

		var _topRightSlot = handler.slots.stream().filter(slot->slot.container.equals(playerInventory) && slot.getContainerSlot() == 17).findFirst();
		Slot topRightSlot = _topRightSlot.isPresent() ? _topRightSlot.get() : null;
		if (topRightSlot != null) {
			if (!topRightSlot.isActive()) {
				for (int i = 0; i < ScoutUtil.MAX_POUCH_SLOTS; i++) {
					BagSlot slot = (BagSlot) ScoutUtil.getBagSlot(ScoutUtil.RIGHT_POUCH_SLOT_START - i, client.player.inventoryMenu);
					if (slot != null) { slot.setX(Integer.MAX_VALUE); slot.setY(Integer.MAX_VALUE); }
				}
			} else {
				x = topRightSlot.x;
				y = topRightSlot.y - 18;
				for (int i = 0; i < ScoutUtil.MAX_POUCH_SLOTS; i++) {
					if (i % 3 == 0) { x += 18; y += 54; }
					BagSlot slot = (BagSlot) ScoutUtil.getBagSlot(ScoutUtil.RIGHT_POUCH_SLOT_START - i, client.player.inventoryMenu);
					if (slot != null) { slot.setX(x); slot.setY(y); }
					y -= 18;
				}
			}
		}
	}
}
