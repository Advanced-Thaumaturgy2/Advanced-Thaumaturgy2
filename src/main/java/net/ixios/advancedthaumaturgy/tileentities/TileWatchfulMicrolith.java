package net.ixios.advancedthaumaturgy.tileentities;

import java.awt.Color;

import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;

@Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral",modid = "ComputerCraft")
public class TileWatchfulMicrolith extends TileMicrolithBase implements IPeripheral
{

	Ticket ticket = null;
	
	public TileWatchfulMicrolith()
    {
	    super(new Color(0, 0, 128));
    }

	@Override
	public boolean canUpdate()
	{
		return true;
	}
	
	@Override
	public void updateEntity()
	{
	    super.updateEntity();
	    if (ticket == null)
	    	return;
	    
	    if (worldObj.rand.nextBoolean())
	    	AdvThaum.proxy.createCustomParticle(worldObj, xCoord + worldObj.rand.nextFloat(), yCoord, zCoord + worldObj.rand.nextFloat(), 0, worldObj.rand.nextGaussian() / 10, 0, 0xFF000099);
	}
	
	@Override
    public boolean onBlockActivated(World world, int x, int y, int z,
            EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
		ChunkCoordIntPair coords = new ChunkCoordIntPair(xCoord >> 4, zCoord >> 4);
		
		if (ticket == null)
		{
			ticket = ForgeChunkManager.requestTicket(AdvThaum.instance, worldObj, Type.NORMAL);
		    if (ticket == null)
		    	return true;
		    ForgeChunkManager.forceChunk(ticket, coords);
		}
		else
		{
			ForgeChunkManager.unforceChunk(ticket, coords);
			ForgeChunkManager.releaseTicket(ticket);
			ticket = null;
		}
	    return true;
    }


    @Optional.Method(modid = "ComputerCraft")
	@Override
	public String getType() {
		return "watchfulMicrolith";
	}

	@Optional.Method(modid = "ComputerCraft")
	@Override
	public String[] getMethodNames() {
		return new String[]{"toggleActive","getActive"};
	}

	@Optional.Method(modid = "ComputerCraft")
	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		switch (method)
		{
			case 0:
				onBlockActivated(worldObj,xCoord,yCoord,zCoord,null,0,0,0,0);
				return new Object[]{};
			case 1:
				return new Object[]{ticket!=null};
		}
		return new Object[]{};
	}

	@Optional.Method(modid = "ComputerCraft")
	@Override
	public void attach(IComputerAccess computer) {

	}

	@Optional.Method(modid = "ComputerCraft")
	@Override
	public void detach(IComputerAccess computer) {

	}

	@Optional.Method(modid = "ComputerCraft")
	@Override
	public boolean equals(IPeripheral other) {
		return this.equals(other);
	}
}
