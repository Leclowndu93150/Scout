package pm.c7.scout.platform.services;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import pm.c7.scout.item.BaseBagItem;

import java.nio.file.Path;

public interface IScoutPlatform {
    String getPlatformName();
    boolean isModLoaded(String modId);
    Path getConfigDir();
    ItemStack findBagItem(Player player, BaseBagItem.BagType type, boolean right);
    void sendEnableSlotsPacket(ServerPlayer player);
}
