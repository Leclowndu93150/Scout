package pm.c7.scout.item;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import pm.c7.scout.ScoutNetworking;
import pm.c7.scout.ScoutScreenHandler;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.screen.BagSlot;

import java.util.List;
import java.util.Optional;

public class BaseBagItem extends TrinketItem {
	private final int slots;
	private final BagType type;

	public BaseBagItem(Properties settings, int slots, BagType type) {
		super(settings);

		if (type == BagType.SATCHEL && slots > ScoutUtil.MAX_SATCHEL_SLOTS) {
			throw new IllegalArgumentException("Satchel has too many slots.");
		}
		if (type == BagType.POUCH && slots > ScoutUtil.MAX_POUCH_SLOTS) {
			throw new IllegalArgumentException("Pouch has too many slots.");
		}

		this.slots = slots;
		this.type = type;
	}

	public int getSlotCount() {
		return this.slots;
	}

	public BagType getType() {
		return this.type;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
		super.appendHoverText(stack, context, tooltip, type);
		tooltip.add(Component.translatable("tooltip.scout.slots", Component.literal(String.valueOf(this.slots)).withStyle(ChatFormatting.BLUE)).withStyle(ChatFormatting.GRAY));
	}

	public Container getInventory(ItemStack stack) {
		SimpleContainer inventory = new SimpleContainer(this.slots) {
			@Override
			public void setChanged() {
				NonNullList<ItemStack> stacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
				for (int i = 0; i < this.getContainerSize(); i++) {
					stacks.set(i, this.getItem(i));
				}
				stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(stacks));
				super.setChanged();
			}
		};

		ItemContainerContents container = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
		container.copyInto(inventory.getItems());

		return inventory;
	}

	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
		NonNullList<ItemStack> stacks = NonNullList.create();
		Container inventory = getInventory(stack);

		for (int i = 0; i < slots; i++) {
			stacks.add(inventory.getItem(i));
		}

		if (stacks.stream().allMatch(ItemStack::isEmpty)) return Optional.empty();

		return Optional.of(new BagTooltipData(stacks, slots));
	}

	@Override
	public void onEquip(ItemStack stack, SlotReference slotRef, LivingEntity entity) {
		if (entity instanceof Player player)
			updateSlots(player);
	}

	@Override
	public void onUnequip(ItemStack stack, SlotReference slotRef, LivingEntity entity) {
		if (entity instanceof Player player)
			updateSlots(player);
	}

	private void updateSlots(Player player) {
		ScoutScreenHandler handler = (ScoutScreenHandler) player.inventoryMenu;

		ItemStack satchelStack = ScoutUtil.findBagItem(player, BagType.SATCHEL, false);
		NonNullList<BagSlot> satchelSlots = handler.scout$getSatchelSlots();

		for (int i = 0; i < ScoutUtil.MAX_SATCHEL_SLOTS; i++) {
			BagSlot slot = satchelSlots.get(i);
			slot.setInventory(null);
			slot.setEnabled(false);
		}
		if (!satchelStack.isEmpty()) {
			BaseBagItem satchelItem = (BaseBagItem) satchelStack.getItem();
			Container satchelInv = satchelItem.getInventory(satchelStack);

			for (int i = 0; i < satchelItem.getSlotCount(); i++) {
				BagSlot slot = satchelSlots.get(i);
				slot.setInventory(satchelInv);
				slot.setEnabled(true);
			}
		}

		ItemStack leftPouchStack = ScoutUtil.findBagItem(player, BagType.POUCH, false);
		NonNullList<BagSlot> leftPouchSlots = handler.scout$getLeftPouchSlots();

		for (int i = 0; i < ScoutUtil.MAX_POUCH_SLOTS; i++) {
			BagSlot slot = leftPouchSlots.get(i);
			slot.setInventory(null);
			slot.setEnabled(false);
		}
		if (!leftPouchStack.isEmpty()) {
			BaseBagItem leftPouchItem = (BaseBagItem) leftPouchStack.getItem();
			Container leftPouchInv = leftPouchItem.getInventory(leftPouchStack);

			for (int i = 0; i < leftPouchItem.getSlotCount(); i++) {
				BagSlot slot = leftPouchSlots.get(i);
				slot.setInventory(leftPouchInv);
				slot.setEnabled(true);
			}
		}

		ItemStack rightPouchStack = ScoutUtil.findBagItem(player, BagType.POUCH, true);
		NonNullList<BagSlot> rightPouchSlots = handler.scout$getRightPouchSlots();

		for (int i = 0; i < ScoutUtil.MAX_POUCH_SLOTS; i++) {
			BagSlot slot = rightPouchSlots.get(i);
			slot.setInventory(null);
			slot.setEnabled(false);
		}
		if (!rightPouchStack.isEmpty()) {
			BaseBagItem rightPouchItem = (BaseBagItem) rightPouchStack.getItem();
			Container rightPouchInv = rightPouchItem.getInventory(rightPouchStack);

			for (int i = 0; i < rightPouchItem.getSlotCount(); i++) {
				BagSlot slot = rightPouchSlots.get(i);
				slot.setInventory(rightPouchInv);
				slot.setEnabled(true);
			}
		}

		if (player instanceof ServerPlayer serverPlayer) {
			ServerPlayNetworking.send(serverPlayer, new ScoutNetworking.EnableSlotsPayload());
		}
	}

	@Override
	public boolean canEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
		Item item = stack.getItem();

		ItemStack slotStack = slot.inventory().getItem(slot.index());
		Item slotItem = slotStack.getItem();

		if (slotItem instanceof BaseBagItem) {
			if (((BaseBagItem) item).getType() == BagType.SATCHEL) {
				if (((BaseBagItem) slotItem).getType() == BagType.SATCHEL) {
					return true;
				} else {
					return ScoutUtil.findBagItem((Player) entity, BagType.SATCHEL, false).isEmpty();
				}
			} else if (((BaseBagItem) item).getType() == BagType.POUCH) {
				if (((BaseBagItem) slotItem).getType() == BagType.POUCH) {
					return true;
				} else {
					return ScoutUtil.findBagItem((Player) entity, BagType.POUCH, true).isEmpty();
				}
			}
		} else {
			if (((BaseBagItem) item).getType() == BagType.SATCHEL) {
				return ScoutUtil.findBagItem((Player) entity, BagType.SATCHEL, false).isEmpty();
			} else if (((BaseBagItem) item).getType() == BagType.POUCH) {
				return ScoutUtil.findBagItem((Player) entity, BagType.POUCH, true).isEmpty();
			}
		}

		return false;
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
		var inv = getInventory(stack);

		for (int i = 0; i < inv.getContainerSize(); i++) {
			var invStack = inv.getItem(i);
			invStack.inventoryTick(world, entity, i, false);
		}
	}

	@Override
	public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
		var inv = getInventory(stack);

		for (int i = 0; i < inv.getContainerSize(); i++) {
			var invStack = inv.getItem(i);
			invStack.inventoryTick(entity.level(), entity, i, false);
		}
	}

	public enum BagType {
		SATCHEL,
		POUCH
	}
}
