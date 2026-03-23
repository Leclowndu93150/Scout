package pm.c7.scout.fabric.registry;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemContainerContents;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.fabric.item.FabricBagItem;
import pm.c7.scout.item.BaseBagItem;
import pm.c7.scout.registry.ScoutItems;

public class FabricScoutItems {
	public static final CreativeModeTab ITEM_GROUP = FabricItemGroup.builder()
		.icon(() -> new ItemStack(ScoutItems.SATCHEL))
		.title(Component.translatable("itemGroup.scout.itemgroup"))
		.displayItems((context, entries) -> {
			entries.accept(ScoutItems.TANNED_LEATHER);
			entries.accept(ScoutItems.SATCHEL_STRAP);
			entries.accept(ScoutItems.SATCHEL);
			entries.accept(ScoutItems.UPGRADED_SATCHEL);
			entries.accept(ScoutItems.POUCH);
			entries.accept(ScoutItems.UPGRADED_POUCH);
		})
		.build();

	private static void register(String name, Item item) {
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(ScoutUtil.MOD_ID, name), item);
	}

	public static void init() {
		ScoutItems.TANNED_LEATHER = new Item(new Item.Properties());
		ScoutItems.SATCHEL_STRAP = new Item(new Item.Properties());
		ScoutItems.SATCHEL = new FabricBagItem(new Item.Properties().stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY), ScoutUtil.MAX_SATCHEL_SLOTS / 2, BaseBagItem.BagType.SATCHEL);
		ScoutItems.UPGRADED_SATCHEL = new FabricBagItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY), ScoutUtil.MAX_SATCHEL_SLOTS, BaseBagItem.BagType.SATCHEL);
		ScoutItems.POUCH = new FabricBagItem(new Item.Properties().stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY), ScoutUtil.MAX_POUCH_SLOTS / 2, BaseBagItem.BagType.POUCH);
		ScoutItems.UPGRADED_POUCH = new FabricBagItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY), ScoutUtil.MAX_POUCH_SLOTS, BaseBagItem.BagType.POUCH);

		register("tanned_leather", ScoutItems.TANNED_LEATHER);
		register("satchel_strap", ScoutItems.SATCHEL_STRAP);
		register("satchel", ScoutItems.SATCHEL);
		register("upgraded_satchel", ScoutItems.UPGRADED_SATCHEL);
		register("pouch", ScoutItems.POUCH);
		register("upgraded_pouch", ScoutItems.UPGRADED_POUCH);

		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath(ScoutUtil.MOD_ID, "itemgroup"), ITEM_GROUP);
	}
}
