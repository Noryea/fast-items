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
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.client.renderer.entity.ItemEntityRenderer.getSeedForItemStack;

@Mixin(ItemEntityRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class ItemEntityRendererMixin extends EntityRenderer<ItemEntity> {

    @Final
    @Shadow private ItemRenderer itemRenderer;

    @Final
    @Shadow private RandomSource random;

    protected ItemEntityRendererMixin(EntityRendererProvider.Context context) {
        super(context);
    }


    @Unique
    private static int getRenderedAmount(ItemStack stackSize) {
        int count = stackSize.getCount();
        if (count <= 1) {
            return 1;
        } else if (count <= 16) {
            return 2;
        } else if (count <= 32) {
            return 3;
        } else {
            return count <= 48 ? 4 : 5;
        }
    }

    @Inject(method = "render*", at = @At("HEAD"), cancellable = true)
    public void render(ItemEntity itemEntity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        //early exit
        if (!FastItemsConfig.enable) {
            return;
        }

        poseStack.pushPose();
        ItemStack itemStack = itemEntity.getItem();
        this.random.setSeed(getSeedForItemStack(itemStack));

        BakedModel bakedModel = this.itemRenderer.getModel(itemStack, itemEntity.level(), null, itemEntity.getId());
        boolean gui3d = bakedModel.isGui3d();

        //CONFIG: castShadows
        this.shadowRadius = FastItemsConfig.castShadows ? 0.15F : 0.0F;

        // up and down
        float l = Mth.sin(((float)itemEntity.getAge() + g) / 10.0F + itemEntity.bobOffs) * 0.1F + 0.1F;
        float m = bakedModel.getTransforms().getTransform(ItemDisplayContext.GROUND).scale.y();
        poseStack.translate(0.0F, l + 0.25F * m, 0.0F);

        // face to player
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        // rotation is no longer need to fix
        //poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));

        float o = bakedModel.getTransforms().ground.scale.x();
        float p = bakedModel.getTransforms().ground.scale.y();
        float q = bakedModel.getTransforms().ground.scale.z();
        float s;
        float t;

        int renderedAmount = getRenderedAmount(itemStack);

        if (!gui3d) {
            float r = -0.0F * (float)(renderedAmount - 1) * 0.5F * o;
            s = -0.0F * (float)(renderedAmount - 1) * 0.5F * p;
            t = -0.09375F * (float)(renderedAmount - 1) * 0.5F * q;
            poseStack.translate(r, s, t);
        }

        for(int u = 0; u < renderedAmount; ++u) {
            poseStack.pushPose();

            if (u > 0) {
                if (gui3d) {
                    s = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    t = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float v = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    poseStack.translate(s, t, v);
                } else {
                    s = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                    t = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                    poseStack.translate(s, t, 0.0F);
                }
            }
            itemRenderer.render(itemStack, ItemDisplayContext.GROUND, false, poseStack, multiBufferSource, i, OverlayTexture.NO_OVERLAY, bakedModel);
            poseStack.popPose();

            if (!gui3d) {
                poseStack.translate(0.0F * o, 0.0F * p, 0.0425F * q);
            }
        }
        poseStack.popPose();
        super.render(itemEntity, f, g, poseStack, multiBufferSource, i);

        ci.cancel();
    }

}
