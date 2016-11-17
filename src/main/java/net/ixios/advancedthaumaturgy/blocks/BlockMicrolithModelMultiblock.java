package net.ixios.advancedthaumaturgy.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import net.ixios.advancedthaumaturgy.tileentities.TileMicrolithModelMultiblock;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by katsw on 17/11/2016.
 */
public class BlockMicrolithModelMultiblock extends BlockMicrolithMultiblock {


    public BlockMicrolithModelMultiblock() {
        super();
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TileMicrolithModelMultiblock();
    }

    @Override
    public void register() {
        setBlockName("blockMicrolithModelMultiblock");
        GameRegistry.registerBlock(this,"blockMicrolithModelMultiblock");
        GameRegistry.registerTileEntity(TileMicrolithModelMultiblock.class,"blockMicrolithModelMultiblock");
    }
}
