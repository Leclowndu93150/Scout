package pm.c7.scout.item;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public interface IBagItem {
	int getSlotCount();
	BaseBagItem.BagType getType();
	Container getInventory(ItemStack stack);
}
