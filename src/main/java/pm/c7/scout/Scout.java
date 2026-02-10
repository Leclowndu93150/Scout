package pm.c7.scout;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import pm.c7.scout.config.ScoutConfig;
import pm.c7.scout.registry.ScoutItems;

public class Scout implements ModInitializer {
	public static final ItemGroup ITEM_GROUP = FabricItemGroup.builder()
		.icon(() -> new ItemStack(ScoutItems.SATCHEL))
		.displayName(Text.translatable("itemGroup.scout.itemgroup"))
		.entries((context, entries) -> {
			entries.add(ScoutItems.TANNED_LEATHER);
			entries.add(ScoutItems.SATCHEL_STRAP);
			entries.add(ScoutItems.SATCHEL);
			entries.add(ScoutItems.UPGRADED_SATCHEL);
			entries.add(ScoutItems.POUCH);
			entries.add(ScoutItems.UPGRADED_POUCH);
		})
		.build();


	@Override
	public void onInitialize() {
		ScoutConfig.loadConfig();
		ScoutNetworking.init();
		ScoutItems.init();
		Registry.register(Registries.ITEM_GROUP, Identifier.of(ScoutUtil.MOD_ID, "itemgroup"), ITEM_GROUP);
	}
}
