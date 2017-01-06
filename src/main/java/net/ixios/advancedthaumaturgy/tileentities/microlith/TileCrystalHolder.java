package net.ixios.advancedthaumaturgy.tileentities.microlith;

import net.ixios.advancedthaumaturgy.AdvThaum;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import thaumcraft.api.wands.IWandable;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigBlocks;

/**
 * Created by katsw on 17/11/2016.
 */
public class TileCrystalHolder extends TileEntity implements IWandable {

    @Override
    public int onWandRightClick(World world, ItemStack itemStack, EntityPlayer entityPlayer, int i, int i1, int i2, int i3, int i4) {
        return 0;
    }

    @Override
    public ItemStack onWandRightClick(World world, ItemStack itemStack, EntityPlayer entityPlayer) {
        entityPlayer.addChatMessage(new ChatComponentText("Is unformed valid?"+checkMultiBlock(false)));
        if(checkMultiBlock(false))
            makeMultiBlock();
        entityPlayer.addChatMessage(new ChatComponentText("Is formed valid?"+checkMultiBlock(true)));
        return itemStack;
    }

    @Override
    public void onUsingWandTick(ItemStack itemStack, EntityPlayer entityPlayer, int i) {

    }

    @Override
    public void onWandStoppedUsing(ItemStack itemStack, World world, EntityPlayer entityPlayer, int i) {

    }

    public void makeMultiBlock()
    {
        worldObj.setBlock(xCoord,yCoord-4,zCoord+2,AdvThaum.MicrolithModelMultiBlock);
        Thaumcraft.proxy.blockRunes(worldObj,xCoord,yCoord-4,zCoord+2,1,0,1,5,3);
        worldObj.setBlock(xCoord,yCoord-4,zCoord-2,AdvThaum.MicrolithModelMultiBlock);
        Thaumcraft.proxy.blockRunes(worldObj,xCoord,yCoord-4,zCoord-2,1,0,1,5,3);
        worldObj.setBlock(xCoord+2,yCoord-4,zCoord,AdvThaum.MicrolithModelMultiBlock);
        Thaumcraft.proxy.blockRunes(worldObj,xCoord+2,yCoord-4,zCoord,1,0,1,5,3);
        worldObj.setBlock(xCoord-2,yCoord-4,zCoord,AdvThaum.MicrolithModelMultiBlock);
        Thaumcraft.proxy.blockRunes(worldObj,xCoord-2,yCoord-4,zCoord,1,0,1,5,3);
        for(int x=xCoord-1;x<=xCoord+1;x++)
        {
            for(int y=yCoord-4;y<=yCoord-3;y++)
            {
                for(int z=zCoord-1;z<=zCoord+1;z++)
                {
                    worldObj.setBlock(x,y,z,AdvThaum.MicrolithMultiBlock);
                    Thaumcraft.proxy.blockRunes(worldObj,x,y,z,1,0,1,5,3);
                }
            }
        }

        for(int x=xCoord-1;x<=xCoord+1;x++)
        {
            for(int z=zCoord-1;z<=zCoord+1;z++)
            {
                worldObj.setBlock(x,yCoord-2,z,AdvThaum.MicrolithModelMultiBlock);
                Thaumcraft.proxy.blockRunes(worldObj,x,yCoord-2,z,1,0,1,5,3);
            }
        }
        worldObj.setBlock(xCoord,yCoord-1,zCoord+1,AdvThaum.MicrolithModelMultiBlock);
        Thaumcraft.proxy.blockRunes(worldObj,xCoord,yCoord-1,zCoord+1,1,0,1,5,3);
        worldObj.setBlock(xCoord,yCoord-1,zCoord-1,AdvThaum.MicrolithModelMultiBlock);
        Thaumcraft.proxy.blockRunes(worldObj,xCoord,yCoord-1,zCoord-1,1,0,1,5,3);
        worldObj.setBlock(xCoord+1,yCoord-1,zCoord,AdvThaum.MicrolithModelMultiBlock);
        Thaumcraft.proxy.blockRunes(worldObj,xCoord+1,yCoord-1,zCoord,1,0,1,5,3);
        worldObj.setBlock(xCoord-1,yCoord-1,zCoord,AdvThaum.MicrolithModelMultiBlock);
        Thaumcraft.proxy.blockRunes(worldObj,xCoord-1,yCoord-1,zCoord,1,0,1,5,3);
    }

    public void unmakeMultiBlock()
    {
        if(!worldObj.isAirBlock(xCoord,yCoord-4,zCoord+2))
            worldObj.setBlock(xCoord,yCoord-4,zCoord+2,ConfigBlocks.blockCosmeticSolid,1,2);
        if(!worldObj.isAirBlock(xCoord,yCoord-4,zCoord-2))
            worldObj.setBlock(xCoord,yCoord-4,zCoord-2,ConfigBlocks.blockCosmeticSolid,1,2);
        if(!worldObj.isAirBlock(xCoord+2,yCoord-4,zCoord))
            worldObj.setBlock(xCoord+2,yCoord-4,zCoord,ConfigBlocks.blockCosmeticSolid,1,2);
        if(!worldObj.isAirBlock(xCoord-2,yCoord-4,zCoord))
            worldObj.setBlock(xCoord-2,yCoord-4,zCoord,ConfigBlocks.blockCosmeticSolid,1,2);

        for(int x=xCoord-1;x<=xCoord+1;x++)
        {
            for(int y=yCoord-4;y<=yCoord-3;y++)
            {
                for(int z=zCoord-1;z<=zCoord+1;z++)
                {
                    if(!worldObj.isAirBlock(x,y,z))
                        worldObj.setBlock(x,y,z,Blocks.obsidian);
                }
            }
        }

        for(int x=xCoord-1;x<=xCoord+1;x++)
        {
            for(int z=zCoord-1;z<=zCoord+1;z++)
            {
                if(!worldObj.isAirBlock(x,yCoord-2,z))
                    worldObj.setBlock(x,yCoord-2,z,Blocks.obsidian);
            }
        }
        if(!worldObj.isAirBlock(xCoord,yCoord-1,zCoord+1))
            worldObj.setBlock(xCoord,yCoord-1,zCoord+1,AdvThaum.MicrolithModelMultiBlock);
        if(!worldObj.isAirBlock(xCoord,yCoord-1,zCoord-1))
            worldObj.setBlock(xCoord,yCoord-1,zCoord-1,AdvThaum.MicrolithModelMultiBlock);
        if(!worldObj.isAirBlock(xCoord+1,yCoord-1,zCoord))
            worldObj.setBlock(xCoord+1,yCoord-1,zCoord,AdvThaum.MicrolithModelMultiBlock);
        if(!worldObj.isAirBlock(xCoord-1,yCoord-1,zCoord))
            worldObj.setBlock(xCoord-1,yCoord-1,zCoord,AdvThaum.MicrolithModelMultiBlock);
    }

    public boolean checkMultiBlock(boolean formed)
    {
        Block sides=formed? AdvThaum.MicrolithModelMultiBlock: ConfigBlocks.blockCosmeticSolid;
        Block top=formed?AdvThaum.MicrolithModelMultiBlock: Blocks.obsidian;
        Block main=formed?AdvThaum.MicrolithMultiBlock:Blocks.obsidian;



        boolean valid=true;

        if(worldObj.getBlock(xCoord,yCoord-4,zCoord+2)!=sides || worldObj.getBlock(xCoord,yCoord-4,zCoord-2)!=sides || worldObj.getBlock(xCoord+2,yCoord-4,zCoord)!=sides || worldObj.getBlock(xCoord-2,yCoord-4,zCoord)!=sides)
            return false;
        for(int x=xCoord-1;x<=xCoord+1;x++)
        {
            for(int y=yCoord-4;y<=yCoord-3;y++)
            {
                for(int z=zCoord-1;z<=zCoord+1;z++)
                {
                    if(worldObj.getBlock(x,y,z)!=main)
                        return false;
                }
            }
        }

        for(int x=xCoord-1;x<=xCoord+1;x++)
        {
                for(int z=zCoord-1;z<=zCoord+1;z++)
                {
                    if(worldObj.getBlock(x,yCoord-2,z)!=top)
                        return false;
                }
        }
        if(worldObj.getBlock(xCoord,yCoord-1,zCoord+1)!=sides || worldObj.getBlock(xCoord,yCoord-1,zCoord-1)!=sides || worldObj.getBlock(xCoord+1,yCoord-1,zCoord)!=sides || worldObj.getBlock(xCoord-1,yCoord-1,zCoord)!=sides)
            return false;
        return true;

    }
}
