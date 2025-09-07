package cn.noryea.fastitems;

public final class FastItems {
    public static boolean depth = false;
    public static boolean consumeDepth() {
        var d = depth;
        depth = false;
        return d;
    }
    private FastItems() {}
}
