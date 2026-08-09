package cn.noryea.fastitems.mixin;

import net.minecraft.client.renderer.entity.state.ItemClusterRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BedItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemClusterRenderState.class)
public abstract class ItemClusterRenderStateMixin {
    @Inject(method = "extractItemGroupRenderState", at = @At("RETURN"))
    private void fastitems$markBedsUnflattened(Entity entity, ItemStack stack, ItemModelResolver resolver, CallbackInfo ci) {
        ItemStackRenderState state = ((ItemClusterRenderState) (Object) this).item;
        ((FastItemsItemStackRenderStateExtension) state).fastitems$setSkipFlattening(stack.getItem() instanceof BedItem);
    }
}
