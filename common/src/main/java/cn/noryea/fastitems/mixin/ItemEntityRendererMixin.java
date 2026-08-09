package cn.noryea.fastitems.mixin;

import cn.noryea.fastitems.config.FastItemsConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Replaces 26.2's spinning dropped-item submission with the original Fast Items
 * billboard transform: each flattened item always faces the active camera.
 */
@Mixin(ItemEntityRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class ItemEntityRendererMixin extends EntityRenderer<ItemEntity, ItemEntityRenderState> {
    @Shadow @Final private RandomSource random;

    protected ItemEntityRendererMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    @Inject(method = "submit", at = @At("HEAD"), cancellable = true)
    private void fastitems$submitBillboard(ItemEntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera, CallbackInfo ci) {
        if (!FastItemsConfig.enable || state.item.isEmpty()) return;

        poseStack.pushPose();
        AABB bounds = state.item.getModelBoundingBox();
        float modelBottom = -(float) bounds.minY + 0.0625F;
        float bob = Mth.sin(state.ageInTicks / 10.0F + state.bobOffset) * 0.1F + 0.1F;
        poseStack.translate(0.0F, bob + modelBottom, 0.0F);
        poseStack.mulPose(camera.orientation);
        ItemEntityRenderer.submitMultipleFromCount(poseStack, collector, state.lightCoords, state, this.random, bounds);
        poseStack.popPose();

        super.submit(state, poseStack, collector, camera);
        ci.cancel();
    }
}
