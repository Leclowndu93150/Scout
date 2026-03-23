package pm.c7.scout.registry;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.item.BaseBagItem;

public class ScoutItems {
	public static final Item TANNED_LEATHER = new Item(new Item.Properties());
	public static final Item SATCHEL_STRAP = new Item(new Item.Properties());
	public static final BaseBagItem SATCHEL = new BaseBagItem(new Item.Properties().stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY), ScoutUtil.MAX_SATCHEL_SLOTS / 2, BaseBagItem.BagType.SATCHEL);
	public static final BaseBagItem UPGRADED_SATCHEL = new BaseBagItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY), ScoutUtil.MAX_SATCHEL_SLOTS, BaseBagItem.BagType.SATCHEL);
	public static final BaseBagItem POUCH = new BaseBagItem(new Item.Properties().stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY), ScoutUtil.MAX_POUCH_SLOTS / 2, BaseBagItem.BagType.POUCH);
	public static final BaseBagItem UPGRADED_POUCH = new BaseBagItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY), ScoutUtil.MAX_POUCH_SLOTS, BaseBagItem.BagType.POUCH);

	private static void register(String name, Item item) {
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(ScoutUtil.MOD_ID, name), item);
	}

	public static void init() {
		register("tanned_leather", TANNED_LEATHER);
		register("satchel_strap", SATCHEL_STRAP);
		register("satchel", SATCHEL);
		register("upgraded_satchel", UPGRADED_SATCHEL);
		register("pouch", POUCH);
		register("upgraded_pouch", UPGRADED_POUCH);
	}
}
