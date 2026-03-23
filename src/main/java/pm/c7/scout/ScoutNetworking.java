package pm.c7.scout;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

public class ScoutNetworking {
	public record EnableSlotsPayload() implements CustomPacketPayload {
		public static final Type<EnableSlotsPayload> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(ScoutUtil.MOD_ID, "enable_slots"));
		public static final StreamCodec<FriendlyByteBuf, EnableSlotsPayload> CODEC = StreamCodec.unit(new EnableSlotsPayload());

		@Override
		public Type<? extends CustomPacketPayload> type() {
			return ID;
		}
	}

	public static void init() {
		PayloadTypeRegistry.playS2C().register(EnableSlotsPayload.ID, EnableSlotsPayload.CODEC);
	}
}
