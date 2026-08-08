package cn.noryea.fastitems.mixin;

/**
 * The pre-26.2 ItemEntityRenderer hook used the removed immediate-render API.
 * Flattening is now applied when 26.2 constructs ItemStackRenderState layers.
 */
final class ItemEntityRendererMixin {
    private ItemEntityRendererMixin() {
    }
}
