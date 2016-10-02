package net.ixios.advancedthaumaturgy.tileentities;

import java.util.HashMap;

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

public class TileEssentiaEngine extends TileEntity implements IEnergyProvider
{
	public static final int maxEnergy = 32000;
	public static final int maxOutput = 80;
	public static final int rfPerAspectValue = 1000;
	public static final HashMap<Aspect, Integer> aspectvalues;
	
	static
	{
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
	
	
	protected EnergyStorage energyStorage;
	
	private boolean currentlyactive = false;
	
	public TileEssentiaEngine()
	{
		energyStorage = new EnergyStorage(maxEnergy, maxEnergy, maxOutput);
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
		TileJarFillable essentiajar = null;
		
		for (Aspect aspect : aspectvalues.keySet())
		{
			essentiajar = Utilities.findEssentiaJar(worldObj, aspect, this, 20, 2, 20);
			if (essentiajar != null)
			{
				int amount = aspectvalues.get(essentiajar.aspect) * rfPerAspectValue;
				if (energyStorage.receiveEnergy(amount, true) == amount)
				{
					// createParticls is a blank method in common proxy, and has actual code in client proxy
		            AdvThaum.proxy.createParticle(worldObj, (float)essentiajar.xCoord + 0.5F, essentiajar.yCoord + 1, (float)essentiajar.zCoord + 0.5F, 
		            		(float)xCoord + 0.5F, (float)yCoord + 0.8F, (float)zCoord + 0.5F, essentiajar.aspect.getColor());
		            essentiajar.takeFromContainer(aspect, 1);
		            energyStorage.receiveEnergy(amount, false);
		            if (!worldObj.isRemote)
		            {
		            	worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		            	worldObj.markBlockForUpdate(essentiajar.xCoord, essentiajar.yCoord, essentiajar.zCoord);
		            }
		            else
		            {
						AdvThaum.proxy.createOrbitingParticle(worldObj, this, 20, 0.2F, aspect.getColor());
						AdvThaum.proxy.createOrbitingParticle(worldObj, this, 20, 0.2F, aspect.getColor());
						AdvThaum.proxy.createOrbitingParticle(worldObj, this, 20, 0.2F, aspect.getColor());
						AdvThaum.proxy.createOrbitingParticle(worldObj, this, 20, 0.2F, aspect.getColor());
		            }
					break;
				}
			}
		}
	}
	
	private void outputEnergy()
	{
		TileEntity tile = worldObj.getTileEntity(xCoord, yCoord + 1, zCoord);
		if (tile != null && tile instanceof IEnergyReceiver) 
		{
			IEnergyReceiver receiver = (IEnergyReceiver) tile;
			
			int received = receiver.receiveEnergy(ForgeDirection.DOWN, Math.min(energyStorage.getMaxExtract(), energyStorage.getEnergyStored()), false);
			energyStorage.extractEnergy(received, false);
			
			if (worldObj.getWorldTime() % 4 == 0)
				AdvThaum.proxy.createEngineParticle(worldObj, xCoord, yCoord, zCoord, ForgeDirection.UP, 0xFF00FFFF);
			
			if ((!worldObj.isRemote))
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
		
		restockEnergy();
		
		if (energyStorage.getEnergyStored() <= 0)
			return;
		
		if (worldObj.isRemote)
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
		}
		
		if (currentlyactive)
			outputEnergy();
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

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		energyStorage=energyStorage.readFromNBT(nbt.getCompoundTag("energy"));
		currentlyactive = nbt.getBoolean("active");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
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

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return (from == ForgeDirection.UP);
	}
	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		if(from==ForgeDirection.UP)
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
		return energyStorage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return energyStorage.getMaxEnergyStored();
	}
}
