package pm.c7.scout;

import net.minecraft.core.NonNullList;
import pm.c7.scout.screen.BagSlot;

public interface ScoutScreenHandler {
	NonNullList<BagSlot> scout$getSatchelSlots();
	NonNullList<BagSlot> scout$getLeftPouchSlots();
	NonNullList<BagSlot> scout$getRightPouchSlots();
}
