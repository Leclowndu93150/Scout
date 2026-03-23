package pm.c7.scout.neoforge.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.item.BaseBagItem;
import pm.c7.scout.neoforge.item.NeoForgeBagItem;
import pm.c7.scout.registry.ScoutItems;

import java.util.function.Supplier;

public class NeoForgeScoutItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, ScoutUtil.MOD_ID);
	public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ScoutUtil.MOD_ID);

	public static final Supplier<Item> TANNED_LEATHER = ITEMS.register("tanned_leather", () -> new Item(new Item.Properties()));
	public static final Supplier<Item> SATCHEL_STRAP = ITEMS.register("satchel_strap", () -> new Item(new Item.Properties()));
	public static final Supplier<NeoForgeBagItem> SATCHEL = ITEMS.register("satchel", () -> new NeoForgeBagItem(new Item.Properties().stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY), ScoutUtil.MAX_SATCHEL_SLOTS / 2, BaseBagItem.BagType.SATCHEL));
	public static final Supplier<NeoForgeBagItem> UPGRADED_SATCHEL = ITEMS.register("upgraded_satchel", () -> new NeoForgeBagItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY), ScoutUtil.MAX_SATCHEL_SLOTS, BaseBagItem.BagType.SATCHEL));
	public static final Supplier<NeoForgeBagItem> POUCH = ITEMS.register("pouch", () -> new NeoForgeBagItem(new Item.Properties().stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY), ScoutUtil.MAX_POUCH_SLOTS / 2, BaseBagItem.BagType.POUCH));
	public static final Supplier<NeoForgeBagItem> UPGRADED_POUCH = ITEMS.register("upgraded_pouch", () -> new NeoForgeBagItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY), ScoutUtil.MAX_POUCH_SLOTS, BaseBagItem.BagType.POUCH));

	public static final Supplier<CreativeModeTab> TAB = CREATIVE_TABS.register("itemgroup", () ->
		CreativeModeTab.builder()
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
			.build()
	);

	public static void init(IEventBus bus) {
		ITEMS.register(bus);
		CREATIVE_TABS.register(bus);
	}

	public static void assignToCommon() {
		ScoutItems.TANNED_LEATHER = TANNED_LEATHER.get();
		ScoutItems.SATCHEL_STRAP = SATCHEL_STRAP.get();
		ScoutItems.SATCHEL = SATCHEL.get();
		ScoutItems.UPGRADED_SATCHEL = UPGRADED_SATCHEL.get();
		ScoutItems.POUCH = POUCH.get();
		ScoutItems.UPGRADED_POUCH = UPGRADED_POUCH.get();
	}
}
