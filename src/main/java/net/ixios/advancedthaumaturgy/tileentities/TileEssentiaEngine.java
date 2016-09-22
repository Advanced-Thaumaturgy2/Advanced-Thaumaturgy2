package net.ixios.advancedthaumaturgy.tileentities;

import java.util.HashMap;

import buildcraft.api.transport.IPipeConnection;
import buildcraft.api.transport.IPipeTile;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import cpw.mods.fml.common.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.client.fx.bolt.FXLightningBolt;
import thaumcraft.common.tiles.TileJarFillable;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.misc.Utilities;
import net.ixios.advancedthaumaturgy.misc.Vector3F;

@cpw.mods.fml.common.Optional.InterfaceList({
	@Optional.Interface(modid = "BuildCraft|Energy", iface = "IPipeConnection"),
	@Optional.Interface(modid = "BuildCraft|Energy", iface = "IPowerEmitter")})
public class TileEssentiaEngine extends TileEntity implements IEnergyProvider, IPipeConnection
{
	protected EnergyStorage energyStorage;
	//private final double costperRF = 1D / x;
	//private final double costperEU = 1D / 4000D;
	private final double costperMJ = 1D / 1800D;

	private Aspect curraspect = null;
	private final float maxEnergy = 100F;

	private HashMap<Aspect, Integer> aspectvalues = null;
	
	private boolean currentlyactive = false;
	
	public TileEssentiaEngine()
	{
		energyStorage=new EnergyStorage(100);
		aspectvalues = new HashMap<Aspect, Integer>();
		aspectvalues.put(Aspect.FIRE, 4);
		aspectvalues.put(Aspect.EARTH, 2);
		aspectvalues.put(Aspect.AIR, 2);
		aspectvalues.put(Aspect.WATER, 2);
		aspectvalues.put(Aspect.ORDER, 4);
		aspectvalues.put(Aspect.ENTROPY, 2);
		
		aspectvalues.put(Aspect.TREE, 1);
		aspectvalues.put(Aspect.PLANT, 1);
		aspectvalues.put(Aspect.METAL, 1);
		aspectvalues.put(Aspect.ENERGY, 8);
		
	}

	public void setActive(boolean value)
	{
		currentlyactive = value;
		if (!worldObj.isRemote)
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	@Override
	public boolean canUpdate() 
	{
		return true;
	}
	
	private void restockEnergy()
	{
	
		if (hasEssentiaTubeConnection())
		{
			/*if (fillFromPipe() == 0)
				return;*/
			energyStorage.modifyEnergyStored(aspectvalues.get(curraspect));
            if (!worldObj.isRemote)
            	worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			return;
		}
		
		// findEssentia is in common proxy
		TileJarFillable essentiajar = null;
		
		for (Aspect aspect : aspectvalues.keySet())
		{
			essentiajar = Utilities.findEssentiaJar(worldObj, aspect, this, 20, 2, 20);
			if (essentiajar != null)
			{
				curraspect = essentiajar.aspect;
				break;
			}
		}
		
		if (essentiajar != null && essentiajar.amount > 0)
        {
			// createParticls is a blank method in common proxy, and has actual code in client proxy
            AdvThaum.proxy.createParticle(worldObj, (float)essentiajar.xCoord + 0.5F, essentiajar.yCoord + 1, (float)essentiajar.zCoord + 0.5F, 
            		(float)xCoord + 0.5F, (float)yCoord + 0.8F, (float)zCoord + 0.5F, essentiajar.aspect.getColor());
            essentiajar.takeFromContainer(curraspect, 1);
            energyStorage.modifyEnergyStored(aspectvalues.get(curraspect));
            if (!worldObj.isRemote)
            {
            	worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            	worldObj.markBlockForUpdate(essentiajar.xCoord, essentiajar.yCoord, essentiajar.zCoord);
            }
            return;
        }
	
	}
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
		
		if ((worldObj.isRemote) && (curraspect != null))
		{
			Vector3F src = new Vector3F(xCoord + 0.5F, yCoord + 1.0F, zCoord + 0.5F);
			Vector3F dst = new Vector3F(src.x, yCoord, src.z);
			
			/*src.x += (worldObj.rand.nextFloat() - 0.5F);
			src.y += (worldObj.rand.nextFloat() - 0.5F);
			src.z += (worldObj.rand.nextFloat() - 0.5F);*/
			
			dst.x += (worldObj.rand.nextFloat() - 0.5F);
			dst.y += (worldObj.rand.nextFloat() - 0.5F);
			dst.z += (worldObj.rand.nextFloat() - 0.5F);
			
			if (Minecraft.getMinecraft().renderViewEntity.ticksExisted % 60 == 0)
			{
				FXLightningBolt bolt = new FXLightningBolt(worldObj, src.x, src.y, src.z, dst.x, dst.y, dst.z, worldObj.rand.nextLong(), 5, 1);
				bolt.defaultFractal();
				bolt.setType(0);
				bolt.finalizeBolt();
			}
			
			if (curraspect != null)
			{
				AdvThaum.proxy.createOrbitingParticle(worldObj, this, 20, 0.2F, curraspect.getColor());
				AdvThaum.proxy.createOrbitingParticle(worldObj, this, 20, 0.2F, curraspect.getColor());
				AdvThaum.proxy.createOrbitingParticle(worldObj, this, 20, 0.2F, curraspect.getColor());
				AdvThaum.proxy.createOrbitingParticle(worldObj, this, 20, 0.2F, curraspect.getColor());
			}
		}
		
		restockEnergy();
				
		if (!currentlyactive || energyStorage.getEnergyStored() <= 0.0F)
			return;
	}
	
	public boolean isPoweredTile(TileEntity tile, ForgeDirection side)
	{
		if (tile instanceof IEnergyReceiver)
			return ((IEnergyReceiver) tile).canConnectEnergy(side);

		return false;
	}
	
	private boolean hasEssentiaTubeConnection()
	{
		TileEntity te = worldObj.getTileEntity(xCoord, yCoord + 1, zCoord);
		return (te instanceof IEssentiaTransport);
	}
	
	
	
	@Optional.Method(modid = "BuildCraft|Energy")
	@Override
	public ConnectOverride overridePipeConnection(IPipeTile.PipeType type, ForgeDirection with)
	{
		if (type != IPipeTile.PipeType.POWER)
			return ConnectOverride.DISCONNECT;
		else
			return ConnectOverride.CONNECT;
	}






	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		if (nbt.hasKey("aspect"))
			curraspect = Aspect.getAspect(nbt.getString("aspect").toLowerCase());
		energyStorage=energyStorage.readFromNBT(nbt.getCompoundTag("energy"));
		currentlyactive = nbt.getBoolean("active");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		if (curraspect != null)
			nbt.setString("aspect", curraspect.getName().toLowerCase());
		NBTTagCompound cmp=new NBTTagCompound();
		energyStorage.writeToNBT(cmp);
		nbt.setTag("energy",cmp);
		nbt.setBoolean("active", currentlyactive);
	}
	
	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readFromNBT(pkt.func_148857_g());
	}

	@Optional.Method(modid = "CoFHAPI|energy")
	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return (from!= ForgeDirection.DOWN);
	}
	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		if(from==ForgeDirection.DOWN)
		{
			return energyStorage.extractEnergy(maxExtract,simulate);
		}
		else
		{
			return 0;
		}
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		if(from==ForgeDirection.DOWN)
		{
			return energyStorage.getEnergyStored();
		}
		else
		{
			return 0;
		}
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		if(from==ForgeDirection.DOWN)
		{
			return energyStorage.getMaxEnergyStored();
		}
		else
		{
			return 0;
		}
	}
}
