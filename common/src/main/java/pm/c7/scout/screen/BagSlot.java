package pm.c7.scout.screen;

import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.Slot;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.config.ScoutConfig;
import pm.c7.scout.item.IBagItem;
import pm.c7.scout.mixin.SlotAccessor;

public class BagSlot extends Slot {
	private final int containerIndex;
	public Container inventory;
	private boolean enabled = false;
	private int realX;
	private int realY;

	public BagSlot(int containerIndex, int x, int y) {
		super(null, containerIndex, x, y);
		this.containerIndex = containerIndex;
		this.realX = x;
		this.realY = y;
	}

	public void setBagSlotIndex(int slotIndex) {
		((SlotAccessor) this).setIndex(slotIndex);
	}

	public void setInventory(Container inventory) {
		this.inventory = inventory;
	}

	public void setEnabled(boolean state) {
		enabled = state;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		if (stack.getItem() instanceof IBagItem)
			return false;

		if (stack.is(ScoutUtil.TAG_ITEM_BLACKLIST)) {
			return false;
		}

		if (stack.getItem() instanceof BlockItem blockItem) {
			if (blockItem.getBlock() instanceof ShulkerBoxBlock)
				return enabled && inventory != null && ScoutConfig.allowShulkers;
		}

		return enabled && inventory != null;
	}

	@Override
	public boolean mayPickup(Player playerEntity) {
		return enabled && inventory != null;
	}

	@Override
	public boolean isActive() {
		return enabled && inventory != null;
	}

	@Override
	public ItemStack getItem() {
		return enabled && this.inventory != null ? this.inventory.getItem(this.containerIndex) : ItemStack.EMPTY;
	}

	@Override
	public void set(ItemStack stack) {
		if (enabled && this.inventory != null) {
			this.inventory.setItem(this.containerIndex, stack);
			this.setChanged();
		}
	}

	@Override
	public void setChanged() {
		if (enabled && this.inventory != null) {
			this.inventory.setChanged();
		}
	}

	@Override
	public ItemStack remove(int amount) {
		return enabled && this.inventory != null ? this.inventory.removeItem(this.containerIndex, amount) : ItemStack.EMPTY;
	}

	@Override
	public int getMaxStackSize() {
		return enabled && this.inventory != null ? this.inventory.getMaxStackSize() : 0;
	}

	public int getX() {
		return this.realX;
	}
	public int getY() {
		return this.realY;
	}
	public void setX(int x) {
		this.realX = x;
		this.x = x;
	}
	public void setY(int y) {
		this.realY = y;
		this.y = y;
	}
}
