package pm.c7.scout.client.render;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import com.mojang.math.Axis;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.item.BaseBagItem;

public class PouchFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
	private final ItemInHandRenderer heldItemRenderer;

	public PouchFeatureRenderer(RenderLayerParent<T, M> context, ItemInHandRenderer heldItemRenderer) {
		super(context);
		this.heldItemRenderer = heldItemRenderer;
	}
	@Override
	public void render(PoseStack matrices, MultiBufferSource vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		var leftPouch = ScoutUtil.findBagItem((Player) entity, BaseBagItem.BagType.POUCH, false);
		var rightPouch = ScoutUtil.findBagItem((Player) entity, BaseBagItem.BagType.POUCH, true);

		if (!leftPouch.isEmpty()) {
			matrices.pushPose();
			((PlayerModel<?>) this.getParentModel()).leftLeg.translateAndRotate(matrices);
			matrices.mulPose(Axis.XP.rotationDegrees(180.0F));
			matrices.mulPose(Axis.YP.rotationDegrees(-90.0F));
			matrices.scale(0.325F, 0.325F, 0.325F);
			matrices.translate(0F, -0.325F, -0.475F);
			this.heldItemRenderer.renderItem(entity, leftPouch, ItemDisplayContext.FIXED, false, matrices, vertexConsumers, light);
			matrices.popPose();
		}
		if (!rightPouch.isEmpty()) {
			matrices.pushPose();
			((PlayerModel<?>) this.getParentModel()).rightLeg.translateAndRotate(matrices);
			matrices.mulPose(Axis.XP.rotationDegrees(180.0F));
			matrices.mulPose(Axis.YP.rotationDegrees(-90.0F));
			matrices.scale(0.325F, 0.325F, 0.325F);
			matrices.translate(0F, -0.325F, 0.475F);
			this.heldItemRenderer.renderItem(entity, rightPouch, ItemDisplayContext.FIXED, false, matrices, vertexConsumers, light);
			matrices.popPose();
		}
	}
}
