package net.ixios.advancedthaumaturgy.proxies;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.tiles.TileInfusionMatrix;
import thaumcraft.common.tiles.TileJarFillable;
import thaumcraft.common.tiles.TilePedestal;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.blocks.BlockEssentiaEngine;
import net.ixios.advancedthaumaturgy.compat.energy.EnergyCompatBase;
import net.ixios.advancedthaumaturgy.gui.ContainerNodeModifier;
import net.ixios.advancedthaumaturgy.gui.GuiNodeModifier;
import net.ixios.advancedthaumaturgy.misc.TickManager;
import net.ixios.advancedthaumaturgy.misc.Vector3;
import net.ixios.advancedthaumaturgy.misc.Vector3F;
import net.ixios.advancedthaumaturgy.tileentities.TileNodeModifier;
import net.ixios.advancedthaumaturgy.tileentities.TileNodeModifier.Operation;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class CommonProxy implements IGuiHandler
{

	private TickManager tickmanager = null;
	
	public void register()
	{
		tickmanager = new TickManager();
		MinecraftForge.EVENT_BUS.register(tickmanager);
	}
	
	public void loadData()
	{
		tickmanager.loadData();
	}
	
	public void saveData()
	{
		tickmanager.saveData();
	}
	
	public void beginMonitoring(TileInfusionMatrix im)
	{
		tickmanager.beginMonitoring(im);
	}
	
	public void beginMonitoring(EntityPlayer plr, Vector3 vec, Block block)
	{
		tickmanager.beginMonitoring(plr, vec, block);
	}
	
	public void createParticle(World world, float srcx, float srcy, float srcz, float dstx, float dsty, float dstz, int color) { }
	
	public void createParticle(TileEntity src, float dstx, float dsty, float dstz, int color) { }
	
	public void createSparkleBurst(World world, float x, float y, float z, int count, int color) { }
	
    public void createEngineParticle(World world, int x, int y, int z, ForgeDirection dir, int color) { }
    
    public void createOrbitingParticle(World world, TileEntity te, int lifeticks, double distance, int color) { }
    
    public void createCustomParticle(World world, double srcx, double srcy, double srcz, double chgx, double chgy, double chgz, int color) { }
    
    public void createFloatyLine(World world, Vector3F src, Vector3F dst, int color) { }
    
    public void createFloatyLine(World world, Vector3F src, Vector3F dst, int color, boolean random) {  }
    
    public void createFloatyLine(World world, Vector3F src, Vector3F dst, int color, int age, boolean random) { }
    
    public void shootFireInDirection(World world, Vec3 direction) { }
    
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,	int x, int y, int z) { return null; }

	public void startModification(TileNodeModifier nm, Operation op)
	{
		//nm.startProcess(op); 
	}
    
    public static boolean AspectListcontains(AspectList list, Aspect aspect)
    {
    	for (Aspect a : list.getAspects())
    	{
    		if (a == aspect)
    			return true;
    	}
    	return false;
    }

    



	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity te = world.getTileEntity(x,  y,  z);
		switch (ID)
		{
			case GuiNodeModifier.id:
			{
				if (te instanceof TileNodeModifier)
					return new ContainerNodeModifier(player);
			}
			break;
			
			default:
				return null;
		}
		return null;
	}

	// TODO: NodeModifier start processor packet handle again

	
	public float getSymmetry(TileInfusionMatrix im)
	{
		ArrayList stuff = new ArrayList();
		ArrayList sources = new ArrayList();
		ArrayList pedestals = new ArrayList();

        for (int xx = -12; xx <= 12; xx++)
        {
            for (int zz = -12; zz <= 12; zz++)
            {
                boolean skip = false;
                for (int yy = -5; yy <= 10; yy++)
                {
                    if (xx == 0 && zz == 0)
                        continue;
                    int x = im.xCoord + xx;
                    int y = im.yCoord - yy;
                    int z = im.zCoord + zz;
                    TileEntity te = im.getWorldObj().getTileEntity(x, y, z);
                    if (!skip && yy > 0 && Math.abs(xx) <= 8 && Math.abs(zz) <= 8 && te != null && (te instanceof TilePedestal))
                    {
                        pedestals.add(new ChunkCoordinates(x, y, z));
                        skip = true;
                        continue;
                    }
                    if (te != null && (te instanceof IAspectSource))
                    {
                        sources.add(new ChunkCoordinates(x, y, z));
                        continue;
                    }
                    Block bi = im.getWorldObj().getBlock(x, y, z);
                    if (bi == ConfigBlocks.blockCandle || bi == ConfigBlocks.blockAiry|| bi == ConfigBlocks.blockCrystal || bi == Blocks.skull)
                        stuff.add(new ChunkCoordinates(x, y, z));
                }

            }

        }

        float symmetry = 0;
        Iterator i = pedestals.iterator();
        do
        {
            if(!i.hasNext())
                break;
            ChunkCoordinates cc = (ChunkCoordinates)i.next();
            boolean items = false;
            int x = im.xCoord - cc.posX;
            int z = im.zCoord - cc.posZ;
            TileEntity te = im.getWorldObj().getTileEntity(cc.posX, cc.posY, cc.posZ);
            if (te != null && (te instanceof TilePedestal))
            {
                symmetry += 2;
                //AdvThaum.log("Adding +2 symmetry because te == TilePedestal");
                if (((TilePedestal)te).getStackInSlot(0) != null)
                {
                	//AdvThaum.log("Adding +1 symmetry because te.StackInSlot(0) != null");
                    symmetry++;
                    items = true;
                }
            }
            
            int xx = im.xCoord + x;
            int zz = im.zCoord + z;
            te = im.getWorldObj().getTileEntity(xx, cc.posY, zz);
            
            if (te != null && (te instanceof TilePedestal))
            {
            	//AdvThaum.log("Removing -2 symmetry because te = TilePedestal");
                symmetry -= 2;
                if (((TilePedestal)te).getStackInSlot(0) != null && items)
                {
                	//AdvThaum.log("Removing -1 symmetry because te.getStackInSlot(0) != null && items");
                    symmetry--;
                }
            }
            
        } while (true);
        
        float sym = 0.0F;
        
        i = stuff.iterator();
        do
        {
            if(!i.hasNext())
                break;
            ChunkCoordinates cc = (ChunkCoordinates)i.next();
            boolean items = false;
            
            int x = im.xCoord - cc.posX;
            int z = im.zCoord - cc.posZ;
            
            Block bi = im.getWorldObj().getBlock(cc.posX, cc.posY, cc.posZ);
            
            if (bi == ConfigBlocks.blockCandle|| bi == ConfigBlocks.blockAiry|| bi == ConfigBlocks.blockCrystal|| bi == Blocks.skull)
            {
            	//AdvThaum.log("Adding +0.1f symmmetry because bi is a " + Block.blocksList[bi].getLocalizedName());
                sym += 0.1F;
            }
            
            int xx = im.xCoord + x;
            int zz = im.zCoord + z;
            
            bi = im.getWorldObj().getBlock(xx, cc.posY, zz);
            
            if (bi == ConfigBlocks.blockCandle|| bi == ConfigBlocks.blockAiry|| bi == ConfigBlocks.blockCrystal || bi == Blocks.skull)
            {
            	//AdvThaum.log("Removing -0.2f symmmetry because bi is a " + Block.blocksList[bi].getLocalizedName());
                sym -= 0.2F;
            }
            
        } while(true);
        
        symmetry += sym;
        
        return symmetry;
    }
	
}
