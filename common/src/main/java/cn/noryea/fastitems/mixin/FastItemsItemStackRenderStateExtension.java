package cn.noryea.fastitems.mixin;

public interface FastItemsItemStackRenderStateExtension {
    boolean fastitems$shouldSkipFlattening();

    void fastitems$setSkipFlattening(boolean skipFlattening);
}
