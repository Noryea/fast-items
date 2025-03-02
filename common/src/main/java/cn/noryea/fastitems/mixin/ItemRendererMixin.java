package cn.noryea.fastitems.mixin;

import cn.noryea.fastitems.SimpleItemModel;
import cn.noryea.fastitems.config.FastItemsConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Unique
    private final SimpleItemModel fastitems$flattenedModel = new SimpleItemModel();

    @Unique
    private ItemDisplayContext fastitems$displayMode;

    @Inject(method = "render*", at = @At("HEAD"))
    private void getRenderType(ItemStack itemStack, ItemDisplayContext itemDisplayContext, boolean bl, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, BakedModel bakedModel, CallbackInfo ci) {
        this.fastitems$displayMode = itemDisplayContext;
    }

    @ModifyVariable(method = "renderModelLists", at = @At("HEAD"), index = 1, argsOnly = true)
    private BakedModel useFlattenItem(BakedModel model, BakedModel bakedModel, ItemStack itemStack, int i, int j, PoseStack poseStack, VertexConsumer vertexConsumer) {
        if(FastItemsConfig.enable && !FastItemsConfig.renderSidesOfItems && !itemStack.isEmpty() && !model.isGui3d() && fastitems$displayMode == ItemDisplayContext.GROUND) {
            fastitems$flattenedModel.setItem(model);
            return fastitems$flattenedModel;
        } else
            return model;
    }

}