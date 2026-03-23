package pm.c7.scout.neoforge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.core.NonNullList;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import pm.c7.scout.ScoutScreenHandler;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.client.ScoutUtilClient;
import pm.c7.scout.client.gui.BagTooltipComponent;
import pm.c7.scout.client.render.PouchFeatureRenderer;
import pm.c7.scout.client.render.SatchelFeatureRenderer;
import pm.c7.scout.item.BagTooltipData;
import pm.c7.scout.item.BaseBagItem;
import pm.c7.scout.item.BaseBagItem.BagType;
import pm.c7.scout.item.IBagItem;
import pm.c7.scout.mixin.client.AbstractContainerScreenAccessor;
import pm.c7.scout.screen.BagSlot;

@EventBusSubscriber(modid = ScoutUtil.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ScoutClient {

	@SubscribeEvent
	public static void onRegisterTooltipFactories(RegisterClientTooltipComponentFactoriesEvent event) {
		event.register(BagTooltipData.class, BagTooltipComponent::new);
	}

	@SubscribeEvent
	public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
		// Entity renderers are added via mixin or other mechanism on NeoForge
		// For now we skip the feature renderers - they need the player renderer context
	}

	public static void handleEnableSlots(IPayloadContext context) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null) return;

		ScoutScreenHandler screenHandler = (ScoutScreenHandler) client.player.inventoryMenu;

		ItemStack satchelStack = ScoutUtil.findBagItem(client.player, BagType.SATCHEL, false);
		NonNullList<BagSlot> satchelSlots = screenHandler.scout$getSatchelSlots();
		for (int i = 0; i < ScoutUtil.MAX_SATCHEL_SLOTS; i++) {
			BagSlot slot = satchelSlots.get(i);
			slot.setInventory(null);
			slot.setEnabled(false);
		}
		if (!satchelStack.isEmpty()) {
			IBagItem satchelItem = (IBagItem) satchelStack.getItem();
			Container satchelInv = satchelItem.getInventory(satchelStack);
			for (int i = 0; i < satchelItem.getSlotCount(); i++) {
				BagSlot slot = satchelSlots.get(i);
				slot.setInventory(satchelInv);
				slot.setEnabled(true);
			}
		}

		ItemStack leftPouchStack = ScoutUtil.findBagItem(client.player, BagType.POUCH, false);
		NonNullList<BagSlot> leftPouchSlots = screenHandler.scout$getLeftPouchSlots();
		for (int i = 0; i < ScoutUtil.MAX_POUCH_SLOTS; i++) {
			BagSlot slot = leftPouchSlots.get(i);
			slot.setInventory(null);
			slot.setEnabled(false);
		}
		if (!leftPouchStack.isEmpty()) {
			IBagItem leftPouchItem = (IBagItem) leftPouchStack.getItem();
			Container leftPouchInv = leftPouchItem.getInventory(leftPouchStack);
			for (int i = 0; i < leftPouchItem.getSlotCount(); i++) {
				BagSlot slot = leftPouchSlots.get(i);
				slot.setInventory(leftPouchInv);
				slot.setEnabled(true);
			}
		}

		ItemStack rightPouchStack = ScoutUtil.findBagItem(client.player, BagType.POUCH, true);
		NonNullList<BagSlot> rightPouchSlots = screenHandler.scout$getRightPouchSlots();
		for (int i = 0; i < ScoutUtil.MAX_POUCH_SLOTS; i++) {
			BagSlot slot = rightPouchSlots.get(i);
			slot.setInventory(null);
			slot.setEnabled(false);
		}
		if (!rightPouchStack.isEmpty()) {
			IBagItem rightPouchItem = (IBagItem) rightPouchStack.getItem();
			Container rightPouchInv = rightPouchItem.getInventory(rightPouchStack);
			for (int i = 0; i < rightPouchItem.getSlotCount(); i++) {
				BagSlot slot = rightPouchSlots.get(i);
				slot.setInventory(rightPouchInv);
				slot.setEnabled(true);
			}
		}
	}
}
