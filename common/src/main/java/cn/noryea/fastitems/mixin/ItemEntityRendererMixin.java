package cn.noryea.fastitems.mixin;

import cn.noryea.fastitems.config.FastItemsConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntityRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class ItemEntityRendererMixin extends EntityRenderer<ItemEntity> {

    @Final
    @Shadow private ItemRenderer itemRenderer;

    @Final
    @Shadow private RandomSource random;

    @Shadow
    protected abstract int getRenderAmount(ItemStack arg);


    protected ItemEntityRendererMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    @Inject(method = "render*", at = @At("HEAD"), cancellable = true)
    public void render(ItemEntity itemEntity, float g, float h, PoseStack poseStack, MultiBufferSource multiBufferSource, int l, CallbackInfo ci) {
        //CONFIG: early exit if mod is disabled
        if (!FastItemsConfig.enable) {
            return;
        }

        ItemStack itemStack = itemEntity.getItem();
        BakedModel bakedModel = this.itemRenderer.getModel(itemStack, itemEntity.level(), null, itemEntity.getId());
        boolean gui3d = bakedModel.isGui3d();
        //CONFIG: exit if model is 3D and not affecting 3D models enabled
        if (gui3d && !FastItemsConfig.affect3DModels) {
            return;
        }

        poseStack.pushPose();
        int i = itemStack.isEmpty() ? 187 : Item.getId(itemStack.getItem()) + itemStack.getDamageValue();
        this.random.setSeed(i);
        //CONFIG: castShadows
        this.shadowRadius = FastItemsConfig.castShadows ? 0.15F : 0.0F;

        // up and down animation
        float f1 = Mth.sin(((float)itemEntity.getAge() + h) / 10.0F + itemEntity.bobOffs) * 0.1F + 0.1F;
        float f2 = bakedModel.getTransforms().getTransform(ItemDisplayContext.GROUND).scale.y();
        poseStack.translate(0.0F, f1 + 0.25F * f2, 0.0F);
        // face to player
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        // count visual
        fastitems$renderMultipleFromCount(poseStack, multiBufferSource, l, itemStack, gui3d, bakedModel);

        poseStack.popPose();
        super.render(itemEntity, g, h, poseStack, multiBufferSource, l);

        ci.cancel();
    }

    @Unique
    private void fastitems$renderMultipleFromCount(PoseStack poseStack, MultiBufferSource multiBufferSource, int l, ItemStack itemStack, boolean gui3d, BakedModel bakedModel) {
        int renderAmount = this.getRenderAmount(itemStack);
        float f11;
        float f13;
        if (!gui3d) {
            float f7 = -0.0F * (float)(renderAmount - 1) * 0.5F;
            f11 = -0.0F * (float)(renderAmount - 1) * 0.5F;
            f13 = -0.09375F * (float)(renderAmount - 1) * 0.5F;
            poseStack.translate(f7, f11, f13);
        }

        for(int k = 0; k < renderAmount; ++k) {
            poseStack.pushPose();
            if (k > 0) {
                if (gui3d) {
                    f11 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    f13 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float f10 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    poseStack.translate(this.fastitems$shouldSpreadItems() ? f11 : 0.0F, this.fastitems$shouldSpreadItems() ? f13 : 0.0F, this.fastitems$shouldSpreadItems() ? f10 : 0.0F);
                } else {
                    f11 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                    f13 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                    poseStack.translate(this.fastitems$shouldSpreadItems() ? f11 : 0.0, this.fastitems$shouldSpreadItems() ? f13 : 0.0, 0.0);
                }
            }

            this.itemRenderer.render(itemStack, ItemDisplayContext.GROUND, false, poseStack, multiBufferSource, l, OverlayTexture.NO_OVERLAY, bakedModel);
            poseStack.popPose();
            if (!gui3d) {
                poseStack.translate(0.0, 0.0, 0.0425 * bakedModel.getTransforms().ground.scale.z());
            }
        }
    }

    @Unique
    private boolean fastitems$shouldSpreadItems() {
        return true;
    }
}