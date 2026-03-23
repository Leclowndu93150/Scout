package pm.c7.scout.neoforge.platform;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.network.PacketDistributor;
import pm.c7.scout.ScoutNetworking;
import pm.c7.scout.item.BaseBagItem;
import pm.c7.scout.item.IBagItem;
import pm.c7.scout.platform.services.IScoutPlatform;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.nio.file.Path;
import java.util.List;

public class NeoForgeScoutPlatform implements IScoutPlatform {
	@Override
	public String getPlatformName() {
		return "NeoForge";
	}

	@Override
	public boolean isModLoaded(String modId) {
		return FMLLoader.getLoadingModList().getModFileById(modId) != null;
	}

	@Override
	public Path getConfigDir() {
		return FMLPaths.CONFIGDIR.get();
	}

	@Override
	public ItemStack findBagItem(Player player, BaseBagItem.BagType type, boolean right) {
		ItemStack targetStack = ItemStack.EMPTY;
		boolean hasFirstPouch = false;

		String slotType = type == BaseBagItem.BagType.SATCHEL ? "satchel" : "pouch";
		List<SlotResult> results = CuriosApi.getCuriosInventory(player)
				.map(handler -> handler.findCurios(stack -> stack.getItem() instanceof IBagItem bagItem && bagItem.getType() == type))
				.orElse(List.of());

		for (SlotResult result : results) {
			ItemStack slotStack = result.stack();
			if (slotStack.getItem() instanceof IBagItem bagItem && bagItem.getType() == type) {
				if (type == BaseBagItem.BagType.POUCH) {
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
		return targetStack;
	}

	@Override
	public void sendEnableSlotsPacket(ServerPlayer player) {
		PacketDistributor.sendToPlayer(player, new ScoutNetworking.EnableSlotsPayload());
	}
}
