package pm.c7.scout.fabric.item;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.component.ItemContainerContents;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.item.BaseBagItem;
import pm.c7.scout.item.BagTooltipData;
import pm.c7.scout.item.IBagItem;

import java.util.List;
import java.util.Optional;

public class FabricBagItem extends TrinketItem implements IBagItem {
	private final int slots;
	private final BaseBagItem.BagType type;

	public FabricBagItem(Properties settings, int slots, BaseBagItem.BagType type) {
		super(settings);
		if (type == BaseBagItem.BagType.SATCHEL && slots > ScoutUtil.MAX_SATCHEL_SLOTS) {
			throw new IllegalArgumentException("Satchel has too many slots.");
		}
		if (type == BaseBagItem.BagType.POUCH && slots > ScoutUtil.MAX_POUCH_SLOTS) {
			throw new IllegalArgumentException("Pouch has too many slots.");
		}
		this.slots = slots;
		this.type = type;
	}

	public int getSlotCount() {
		return this.slots;
	}

	public BaseBagItem.BagType getType() {
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
			BaseBagItem.updateSlots(player);
	}

	@Override
	public void onUnequip(ItemStack stack, SlotReference slotRef, LivingEntity entity) {
		if (entity instanceof Player player)
			BaseBagItem.updateSlots(player);
	}

	@Override
	public boolean canEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
		if (!(entity instanceof Player player)) return false;
		ItemStack slotStack = slot.inventory().getItem(slot.index());
		return BaseBagItem.canEquipBag(stack, player, this.type, slotStack);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
		BaseBagItem.tickBagInventory(stack, entity);
	}

	@Override
	public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
		BaseBagItem.tickBagInventory(stack, entity);
	}
}
