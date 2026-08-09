package cn.noryea.fastitems.mixin;

import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemStackRenderState.class)
public abstract class ItemStackRenderStateMixin implements FastItemsItemStackRenderStateExtension {
    @Unique private boolean fastitems$skipFlattening;

    @Override
    public boolean fastitems$shouldSkipFlattening() {
        return this.fastitems$skipFlattening;
    }

    @Override
    public void fastitems$setSkipFlattening(boolean skipFlattening) {
        this.fastitems$skipFlattening = skipFlattening;
    }
}
