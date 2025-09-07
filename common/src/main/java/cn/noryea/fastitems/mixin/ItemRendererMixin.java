package cn.noryea.fastitems.mixin;

import cn.noryea.fastitems.FastItems;
import cn.noryea.fastitems.config.FastItemsConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Unique
    private static ItemDisplayContext fastitems$displayContext;

    @Inject(method = "renderItem", at = @At("HEAD"))
    private static void getRenderType(ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, int[] is, List<BakedQuad> list, RenderType renderType, ItemStackRenderState.FoilType foilType, CallbackInfo ci) {
        fastitems$displayContext = itemDisplayContext;
    }

    @ModifyArg(method = "renderItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderQuadList(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Ljava/util/List;[III)V"), index = 2)
    private static List<BakedQuad> ZuseFlattenItem(List<BakedQuad> list) {
        if (FastItemsConfig.enable && !FastItemsConfig.renderSidesOfItems && !FastItems.consumeDepth() && fastitems$displayContext == ItemDisplayContext.GROUND) {
            var quads = new ArrayList<BakedQuad>();
            for (var q : list) {
                if (q.direction() == Direction.SOUTH) quads.add(q);
            }
            return quads;
        } else return list;
    }
}
