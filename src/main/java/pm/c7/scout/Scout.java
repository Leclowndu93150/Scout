package pm.c7.scout;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import pm.c7.scout.config.ScoutConfig;
import pm.c7.scout.registry.ScoutItems;

public class Scout implements ModInitializer {
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


	@Override
	public void onInitialize() {
		ScoutConfig.loadConfig();
		ScoutNetworking.init();
		ScoutItems.init();
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath(ScoutUtil.MOD_ID, "itemgroup"), ITEM_GROUP);
	}
}
