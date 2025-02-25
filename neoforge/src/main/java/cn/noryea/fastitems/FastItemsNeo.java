package cn.noryea.fastitems;

import cn.noryea.fastitems.config.FastItemsConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(dist = Dist.CLIENT, value = "fastitems")
public class FastItemsNeo {
    public FastItemsNeo() {
        FastItemsConfig.init("fastitems", FastItemsConfig.class);
    }
}
