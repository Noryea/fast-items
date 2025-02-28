package cn.noryea.fastitems;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SimpleItemModel implements BakedModel {

    private BakedModel flattenedItem;
    private final List<BakedQuad> nullQuadList = new ObjectArrayList<>();

    public void setItem(BakedModel model) {
        this.flattenedItem = model;
    }

    private boolean isCorrectDirectionForType(Direction direction) {
        return direction == Direction.SOUTH;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, RandomSource random) {
        if(face != null) {
            return isCorrectDirectionForType(face) ? flattenedItem.getQuads(state, face, random) : ImmutableList.of();
        }

        nullQuadList.clear();
        List<BakedQuad> realList = flattenedItem.getQuads(state, null, random);
        for (BakedQuad quad : realList) {
            if (isCorrectDirectionForType(quad.getDirection())) {
                nullQuadList.add(quad);
            }
        }
        return nullQuadList;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return flattenedItem.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return flattenedItem.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return flattenedItem.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return flattenedItem.isCustomRenderer();
    }

    @Override
    public @NotNull TextureAtlasSprite getParticleIcon() {
        return flattenedItem.getParticleIcon();
    }

    @Override
    public @NotNull ItemTransforms getTransforms() {
        return flattenedItem.getTransforms();
    }

}
