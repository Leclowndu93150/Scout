package pm.c7.scout.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import pm.c7.scout.ScoutNetworking;
import pm.c7.scout.config.ScoutConfig;
import pm.c7.scout.fabric.registry.FabricScoutItems;

public class Scout implements ModInitializer {
	@Override
	public void onInitialize() {
		ScoutConfig.loadConfig();
		PayloadTypeRegistry.playS2C().register(ScoutNetworking.EnableSlotsPayload.ID, ScoutNetworking.EnableSlotsPayload.CODEC);
		FabricScoutItems.init();
	}
}
