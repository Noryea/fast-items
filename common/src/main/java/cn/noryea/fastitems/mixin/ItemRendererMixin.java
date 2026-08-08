package cn.noryea.fastitems.mixin;

import cn.noryea.fastitems.config.FastItemsConfig;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * 26.2's item renderer builds render-state layers rather than exposing BakedModel.
 * Filter the ground layer's quads to its front face, preserving the original mod's
 * 2D dropped-item optimization without changing GUI/hand rendering.
 */
@Mixin(ItemStackRenderState.LayerRenderState.class)
public abstract class ItemRendererMixin {
    @Shadow @Final private ItemStackRenderState this$0;

    @Inject(method = "prepareQuadList", at = @At("RETURN"))
    private void fastitems$flattenGroundItems(CallbackInfoReturnable<List<BakedQuad>> cir) {
        if (!FastItemsConfig.enable || FastItemsConfig.renderSidesOfItems) return;
        if (((ItemStackRenderStateAccessor) this.this$0).fastitems$getDisplayContext() != ItemDisplayContext.GROUND) return;
        List<BakedQuad> quads = cir.getReturnValue();
        quads.removeIf(quad -> quad.direction() != Direction.SOUTH);
    }
}
