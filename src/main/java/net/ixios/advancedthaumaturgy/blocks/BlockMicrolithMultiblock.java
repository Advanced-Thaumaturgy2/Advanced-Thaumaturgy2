package net.ixios.advancedthaumaturgy.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import net.ixios.advancedthaumaturgy.tileentities.TileMicrolithMultiblock;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by katsw on 17/11/2016.
 */
public class BlockMicrolithMultiblock extends BlockContainer {
    public BlockMicrolithMultiblock() {
        super(Material.rock);
        setBlockTextureName("obsidian");

    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TileMicrolithMultiblock();
    }

    public void register()
    {
        setBlockName("blockMicrolithMultiblock");
        GameRegistry.registerBlock(this,"blockMicrolithMultiblock");
        GameRegistry.registerTileEntity(TileMicrolithMultiblock.class,"blockMicrolithMultiblock");
    }
}
