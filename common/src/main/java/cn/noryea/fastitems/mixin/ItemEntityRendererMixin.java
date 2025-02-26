package cn.noryea.fastitems.mixin;

import cn.noryea.fastitems.config.FastItemsConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.client.renderer.entity.ItemEntityRenderer.getSeedForItemStack;
import static net.minecraft.client.renderer.entity.ItemEntityRenderer.renderMultipleFromCount;

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

    @Inject(method = "render*", at = @At("HEAD"), cancellable = true)
    public void render(ItemEntity itemEntity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
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
        this.random.setSeed(getSeedForItemStack(itemStack));
        //CONFIG: castShadows
        this.shadowRadius = FastItemsConfig.castShadows ? 0.15F : 0.0F;

        // up and down animation
        float l = Mth.sin(((float)itemEntity.getAge() + g) / 10.0F + itemEntity.bobOffs) * 0.1F + 0.1F;
        float m = bakedModel.getTransforms().getTransform(ItemDisplayContext.GROUND).scale.y();
        poseStack.translate(0.0F, l + 0.25F * m, 0.0F);
        // face to player
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation()); //poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
        // count visual
        renderMultipleFromCount(this.itemRenderer, poseStack, multiBufferSource, i, itemStack, bakedModel, gui3d, this.random);

        poseStack.popPose();
        super.render(itemEntity, f, g, poseStack, multiBufferSource, i);

        ci.cancel();
    }

}
