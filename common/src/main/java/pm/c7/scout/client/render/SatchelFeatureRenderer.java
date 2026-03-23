package pm.c7.scout.client.render;

import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.ItemRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.client.model.SatchelModel;
import pm.c7.scout.item.BaseBagItem;
import pm.c7.scout.item.IBagItem;

public class SatchelFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
	private static final ResourceLocation SATCHEL_TEXTURE = ResourceLocation.fromNamespaceAndPath(ScoutUtil.MOD_ID, "textures/entity/satchel.png");
	private static final ResourceLocation UPGRADED_SATCHEL_TEXTURE = ResourceLocation.fromNamespaceAndPath(ScoutUtil.MOD_ID, "textures/entity/upgraded_satchel.png");

	private final SatchelModel<T> satchel;

	public SatchelFeatureRenderer(RenderLayerParent<T, M> context) {
		super(context);
		LayerDefinition modelData = SatchelModel.getTexturedModelData();
		this.satchel = new SatchelModel<>(modelData.bakeRoot());
	}

	@Override
	public void render(PoseStack matrices, MultiBufferSource vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		var satchel = ScoutUtil.findBagItem((Player) entity, BaseBagItem.BagType.SATCHEL, false);

		if (!satchel.isEmpty()) {
			IBagItem satchelItem = (IBagItem) satchel.getItem();
			var texture = SATCHEL_TEXTURE;
			if (satchelItem.getSlotCount() == ScoutUtil.MAX_SATCHEL_SLOTS)
				texture = UPGRADED_SATCHEL_TEXTURE;

			matrices.pushPose();
			((PlayerModel<?>) this.getParentModel()).body.translateAndRotate(matrices);
			this.getParentModel().copyPropertiesTo(this.satchel);
			VertexConsumer vertexConsumer = ItemRenderer.getArmorFoilBuffer(
					vertexConsumers, RenderType.armorCutoutNoCull(texture), satchel.hasFoil()
			);
			this.satchel.renderToBuffer(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
			matrices.popPose();
		}
	}
}
