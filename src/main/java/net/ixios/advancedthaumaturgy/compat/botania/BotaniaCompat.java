package net.ixios.advancedthaumaturgy.compat.botania;

import net.minecraft.block.Block;
import vazkii.botania.api.subtile.ISpecialFlower;

/**
 * Created by katsw on 22/09/2016.
 */
public class BotaniaCompat {


    public static boolean isSpecialFlower(Block block) {
        return block instanceof ISpecialFlower;
    }
}
