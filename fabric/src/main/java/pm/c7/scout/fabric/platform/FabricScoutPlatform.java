package pm.c7.scout.fabric.platform;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.Tuple;
import pm.c7.scout.ScoutNetworking;
import pm.c7.scout.item.BaseBagItem;
import pm.c7.scout.item.BaseBagItem.BagType;
import pm.c7.scout.item.IBagItem;
import pm.c7.scout.platform.services.IScoutPlatform;

import java.nio.file.Path;
import java.util.Optional;

public class FabricScoutPlatform implements IScoutPlatform {
	@Override
	public String getPlatformName() {
		return "Fabric";
	}

	@Override
	public boolean isModLoaded(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	@Override
	public Path getConfigDir() {
		return FabricLoader.getInstance().getConfigDir();
	}

	@Override
	public ItemStack findBagItem(Player player, BaseBagItem.BagType type, boolean right) {
		ItemStack targetStack = ItemStack.EMPTY;
		boolean hasFirstPouch = false;
		Optional<TrinketComponent> _component = TrinketsApi.getTrinketComponent(player);
		if (_component.isPresent()) {
			TrinketComponent component = _component.get();
			for (Tuple<SlotReference, ItemStack> pair : component.getAllEquipped()) {
				ItemStack slotStack = pair.getB();
				if (slotStack.getItem() instanceof IBagItem bagItem) {
					if (bagItem.getType() == type) {
						if (type == BagType.POUCH) {
							if (right && !hasFirstPouch) {
								hasFirstPouch = true;
							} else {
								targetStack = slotStack;
								break;
							}
						} else {
							targetStack = slotStack;
							break;
						}
					}
				}
			}
		}
		return targetStack;
	}

	@Override
	public void sendEnableSlotsPacket(ServerPlayer player) {
		ServerPlayNetworking.send(player, new ScoutNetworking.EnableSlotsPayload());
	}
}
