package net.ixios.advancedthaumaturgy.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.tileentities.TileCrystalHolder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Created by katsw on 17/11/2016.
 */
public class BlockCrystalHolder extends BlockContainer {

    public BlockCrystalHolder() {
        super(Material.glass);
        setBlockName("crystalHolder");
        setCreativeTab(AdvThaum.tabAdvThaum);

    }


    @Override
    public void registerBlockIcons(IIconRegister p_149651_1_) {
        super.registerBlockIcons(p_149651_1_);
    }

    @Override
    public IIcon getIcon(IBlockAccess p_149673_1_, int p_149673_2_, int p_149673_3_, int p_149673_4_, int p_149673_5_) {
        return super.getIcon(p_149673_1_, p_149673_2_, p_149673_3_, p_149673_4_, p_149673_5_);
    }

    public void register()
    {
        GameRegistry.registerBlock(this,"blockCrystalHolder");
        GameRegistry.registerTileEntity(TileCrystalHolder.class,"blockCrystalHolder");

    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TileCrystalHolder();
    }
}
