package pm.c7.scout;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class ScoutNetworking {
	public record EnableSlotsPayload() implements CustomPayload {
		public static final Id<EnableSlotsPayload> ID = new Id<>(Identifier.of(ScoutUtil.MOD_ID, "enable_slots"));
		public static final PacketCodec<PacketByteBuf, EnableSlotsPayload> CODEC = PacketCodec.unit(new EnableSlotsPayload());

		@Override
		public Id<? extends CustomPayload> getId() {
			return ID;
		}
	}

	public static void init() {
		PayloadTypeRegistry.playS2C().register(EnableSlotsPayload.ID, EnableSlotsPayload.CODEC);
	}
}
