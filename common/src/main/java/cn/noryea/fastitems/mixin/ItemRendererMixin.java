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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * 26.2's item renderer builds render-state layers rather than exposing BakedModel.
 * Filter the populated ground layer immediately before it is submitted, preserving
 * the original mod's 2D dropped-item optimization without changing GUI/hand rendering.
 */
@Mixin(ItemStackRenderState.LayerRenderState.class)
public abstract class ItemRendererMixin {
    @Shadow @Final private ItemStackRenderState this$0;
    @Shadow @Final private List<BakedQuad> quads;

    @Inject(method = "submit", at = @At("HEAD"))
    private void fastitems$flattenGroundItems(CallbackInfo ci) {
        if (!FastItemsConfig.enable || FastItemsConfig.renderSidesOfItems) return;
        if (((ItemStackRenderStateAccessor) this.this$0).fastitems$getDisplayContext() != ItemDisplayContext.GROUND) return;
        // Bed items are composite 3D block models. Removing every non-SOUTH face leaves most of
        // the bed missing/invisible, so let them render with their original quads.
        if (((FastItemsItemStackRenderStateExtension) this.this$0).fastitems$shouldSkipFlattening()) return;
        // Dropped items are billboarded toward the camera, so the front face alone is sufficient.
        quads.removeIf(quad -> quad.direction() != Direction.SOUTH);
    }
}
