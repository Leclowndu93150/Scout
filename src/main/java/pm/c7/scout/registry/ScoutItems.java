package pm.c7.scout.registry;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.item.BaseBagItem;

public class ScoutItems {
	public static final Item TANNED_LEATHER = new Item(new Item.Settings());
	public static final Item SATCHEL_STRAP = new Item(new Item.Settings());
	public static final BaseBagItem SATCHEL = new BaseBagItem(new Item.Settings().maxCount(1).component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT), ScoutUtil.MAX_SATCHEL_SLOTS / 2, BaseBagItem.BagType.SATCHEL);
	public static final BaseBagItem UPGRADED_SATCHEL = new BaseBagItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE).component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT), ScoutUtil.MAX_SATCHEL_SLOTS, BaseBagItem.BagType.SATCHEL);
	public static final BaseBagItem POUCH = new BaseBagItem(new Item.Settings().maxCount(1).component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT), ScoutUtil.MAX_POUCH_SLOTS / 2, BaseBagItem.BagType.POUCH);
	public static final BaseBagItem UPGRADED_POUCH = new BaseBagItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE).component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT), ScoutUtil.MAX_POUCH_SLOTS, BaseBagItem.BagType.POUCH);

	private static void register(String name, Item item) {
		Registry.register(Registries.ITEM, Identifier.of(ScoutUtil.MOD_ID, name), item);
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
