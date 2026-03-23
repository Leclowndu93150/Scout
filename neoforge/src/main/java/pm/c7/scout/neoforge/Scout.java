package pm.c7.scout.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import pm.c7.scout.ScoutNetworking;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.config.ScoutConfig;
import pm.c7.scout.neoforge.registry.NeoForgeScoutItems;

@Mod(ScoutUtil.MOD_ID)
public class Scout {
	public Scout(IEventBus modBus) {
		ScoutConfig.loadConfig();
		NeoForgeScoutItems.init(modBus);
		modBus.addListener(this::onCommonSetup);
		modBus.addListener(this::onRegisterPayloads);
	}

	private void onCommonSetup(FMLCommonSetupEvent event) {
		NeoForgeScoutItems.assignToCommon();
	}

	private void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar(ScoutUtil.MOD_ID);
		registrar.playToClient(
			ScoutNetworking.EnableSlotsPayload.ID,
			ScoutNetworking.EnableSlotsPayload.CODEC,
			(payload, context) -> {
				context.enqueueWork(() -> {
					pm.c7.scout.neoforge.client.ScoutClient.handleEnableSlots(context);
				});
			}
		);
	}
}
