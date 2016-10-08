package net.ixios.advancedthaumaturgy.tileentities;

import java.util.HashMap;
import java.util.Map.Entry;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.misc.Utilities;
import net.ixios.advancedthaumaturgy.misc.Vector3F;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.client.fx.bolt.FXLightningBolt;
import thaumcraft.common.tiles.TileJarFillable;

public class TileEssentiaEngine extends TileEntity implements IEnergyProvider, IAspectContainer, IEssentiaTransport
{
	public static int maxEnergy = 32000;
	public static int maxOutput = 80;
	public static int rfPerAspectValue = 1000;
	public static HashMap<Aspect, Integer> aspectvalues;
	public static boolean useTubes = true;
	public static int maxEssentia = 64;
	public static float conversionRate = 0.08f;
	
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
	
	public static void loadConfig()
	{
		String catName = "EssentiaEngine";
		ConfigCategory cat = AdvThaum.config.getCategory(catName);
		
		useTubes = Utilities.getConfigBoolean(cat, "useTubes", useTubes, 
					"Set to false if the Engine should pull essentia from nearby jars like the Infusion Altar");
		maxEnergy = Utilities.getConfigInteger(cat, "energy_storage", maxEnergy, "Internal energy buffer of the engine");
		maxOutput = Utilities.getConfigInteger(cat, "energy_output", maxOutput, "Energy output per tick");
		rfPerAspectValue = Utilities.getConfigInteger(cat, "energy_generation", rfPerAspectValue, "Energy per Aspect value");
		
		ConfigCategory catAv;
		String avName = catName + ".AspectValues";
		if (AdvThaum.config.hasCategory(avName))
		{
			catAv = AdvThaum.config.getCategory(avName);
			aspectvalues.clear();
			for (Entry<String, Property> e : catAv.getValues().entrySet())
			{
				Aspect a = Aspect.getAspect(e.getKey());
				if (a != null)
					aspectvalues.put(a, e.getValue().getInt());
			}
		}
		else
		{
			catAv = AdvThaum.config.getCategory(avName);
			for (Entry<Aspect, Integer> e : aspectvalues.entrySet())
			{
				Property p = new Property(e.getKey().getTag(), e.getValue().toString(), Property.Type.INTEGER);
				catAv.put(p.getName(), p);
			}
		}
		
		/*
		 * Conversion rate determines how much essentia is converted to energy each tick
		 * It is calculated to be equal to the energy output for the least efficient aspect
		 * For example: Arbor yields 1 * 1000 rf per essentia on default settings
		 * To reach a constant output of 80rf/tick the engine must 
		 * convert 80/1000 = 0.08 arbor essentia per tick
		 */
		conversionRate = (float)maxOutput / (float)rfPerAspectValue;
	}
	
	
	protected EnergyStorage energyStorage;
	private Aspect aspect;
	private Aspect suction;
	private int suctionCycle;
	private float aspectAmount;
	
	private boolean currentlyactive = false;
	
	public TileEssentiaEngine()
	{
		energyStorage = new EnergyStorage(maxEnergy, maxEnergy, maxOutput);
		aspect = null;
		suction = null;
		suctionCycle = 0;
		aspectAmount = 0;
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
	
	private void pullEssentiaFromJar(TileJarFillable jar)
	{
		// createParticls is a blank method in common proxy, and has actual code in client proxy
        AdvThaum.proxy.createParticle(worldObj, (float)jar.xCoord + 0.5F, jar.yCoord + 1, (float)jar.zCoord + 0.5F, 
        		(float)xCoord + 0.5F, (float)yCoord + 0.8F, (float)zCoord + 0.5F, jar.aspect.getColor());
        jar.takeFromContainer(aspect, 1);
        aspectAmount += 1;
        if (!worldObj.isRemote)
        {
        	worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        	worldObj.markBlockForUpdate(jar.xCoord, jar.yCoord, jar.zCoord);
        }
	}
	
	private void pullEssentiaFromJar()
	{
		if (aspectAmount >= maxEssentia)
			return;
		
		TileJarFillable essentiajar = null;
		
		if (aspect != null)
		{
			essentiajar = Utilities.findEssentiaJar(worldObj, aspect, this, 20, 2, 20);
			if (essentiajar != null)
			{
				pullEssentiaFromJar(essentiajar);
			}
            return;
		}
		
		for (Aspect a : aspectvalues.keySet())
		{
			essentiajar = Utilities.findEssentiaJar(worldObj, a, this, 20, 2, 20);
			if (essentiajar != null)
			{
				aspect = a;
				pullEssentiaFromJar(essentiajar);
			}
		}
	}
	
	private void pullEssentiaFromTubes()
	{
		for (ForgeDirection orientation : ForgeDirection.VALID_DIRECTIONS) 
		{
			if (isConnectable(orientation)) 
			{
				IEssentiaTransport connectedTube = (IEssentiaTransport) ThaumcraftApiHelper.getConnectableTile(worldObj, xCoord, yCoord, zCoord, orientation);
				if ((connectedTube != null) 
				 && (connectedTube.getEssentiaAmount(orientation.getOpposite()) > 0))
				{
					Aspect tubeAspect = connectedTube.getEssentiaType(orientation.getOpposite());
					if (aspect == tubeAspect || aspect == null)
					{
						int taken = connectedTube.takeEssentia(tubeAspect, 1, orientation.getOpposite());
						if (taken == 1)
						{
							aspect = tubeAspect;
							suction = aspect;
							addEssentia(tubeAspect, 1, orientation);
						}
					}
				}
			}
		}
	}
	
	private void generateEnergy()
	{
		if (aspect != null && aspectAmount >= conversionRate)
		{
			int amount = Math.round(aspectvalues.get(aspect) * rfPerAspectValue * conversionRate);
			if (energyStorage.receiveEnergy(amount, true) == amount)
			{
	            energyStorage.receiveEnergy(amount, false);
	            if (worldObj.isRemote)
	            {
					AdvThaum.proxy.createOrbitingParticle(worldObj, this, 20, 0.2F, aspect.getColor());
					AdvThaum.proxy.createOrbitingParticle(worldObj, this, 20, 0.2F, aspect.getColor());
					AdvThaum.proxy.createOrbitingParticle(worldObj, this, 20, 0.2F, aspect.getColor());
					AdvThaum.proxy.createOrbitingParticle(worldObj, this, 20, 0.2F, aspect.getColor());
	            }
	            
	            aspectAmount -= conversionRate;
	            if (aspectAmount <= conversionRate)
	            {
	            	aspectAmount = 0;
	            	aspect = null;
	            	suction = null;
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
		
		if (useTubes)
		{
			if (aspect == null)
			{
				// Cycle through and apply suction
				suction = (Aspect)aspectvalues.keySet().toArray()[suctionCycle % aspectvalues.size()];
				if (worldObj.getWorldTime() % 100 == 0)
					suctionCycle++;
			}
			
			pullEssentiaFromTubes();
		}
		else
		{
			pullEssentiaFromJar();
		}
		
		generateEnergy();
		
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
		aspect = Aspect.getAspect(nbt.getString("aspect"));
		suction = aspect;
		aspectAmount = nbt.getFloat("amount");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		NBTTagCompound cmp=new NBTTagCompound();
		energyStorage.writeToNBT(cmp);
		nbt.setTag("energy",cmp);
		if (aspect != null)
		{
			nbt.setString("aspect", aspect.getTag());
			nbt.setFloat("amount", aspectAmount);
		}
		
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
	
	/*
	 * IEnergyProvider
	 */

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
	
	/*
	 * IAspectContainer
	 */

	@Override
	public int addToContainer(Aspect a, int amount) {
		if (aspect == a)
		{
			int dif = maxEssentia - (int)Math.ceil(aspectAmount);
			aspectAmount += Math.min(dif, amount);
			return Math.max(0, amount - dif);
		}
		return amount;
	}

	@Override
	public int containerContains(Aspect a) {
		return (aspect == a) ? (int)aspectAmount : 0;
	}

	@Override
	public boolean doesContainerAccept(Aspect a) {
		return a == aspect && aspectAmount <= maxEssentia - 1;
	}

	@Override
	public boolean doesContainerContain(AspectList arg0) {
		return false;
	}

	@Override
	public boolean doesContainerContainAmount(Aspect a, int amount) {
		return a == aspect && aspectAmount >= amount;
	}

	@Override
	public AspectList getAspects() {
		if (aspect == null)
			return null;
		return new AspectList().add(aspect, (int)aspectAmount);
	}

	@Override
	public void setAspects(AspectList arg0) {
	}

	@Override
	public boolean takeFromContainer(AspectList arg0) {
		return false;
	}

	@Override
	public boolean takeFromContainer(Aspect arg0, int arg1) {
		return false;
	}
	
	/*
	 * IEssentiaTransport
	 */


	@Override
	public boolean isConnectable(ForgeDirection face) 
	{
		switch (face) 
		{
		case NORTH:
		case EAST:
		case SOUTH:
		case WEST:
			return useTubes;
		default:
			return false;
		}
	}

	@Override
	public boolean canInputFrom(ForgeDirection face) 
	{
		return isConnectable(face);
	}

	@Override
	public Aspect getSuctionType(ForgeDirection face) 
	{
		return suction;
	}

	@Override
	public int getSuctionAmount(ForgeDirection face) 
	{
		return 128;
	}

	@Override
	public int addEssentia(Aspect aspect, int amount, ForgeDirection face) 
	{
		return canInputFrom(face) ? addToContainer(aspect, amount) : 0;
	}

	@Override
	public int takeEssentia(Aspect aspect, int amount, ForgeDirection face) 
	{
		return 0;
	}
	
	@Override
	public int getEssentiaAmount(ForgeDirection face) 
	{
		return (int)aspectAmount;
	}
	
	@Override
	public Aspect getEssentiaType(ForgeDirection face) 
	{
		return suction;
	}

	@Override
	public boolean canOutputTo(ForgeDirection face) {return false;}

	@Override
	public void setSuction(Aspect aspect, int amount) {}

	@Override
	public int getMinimumSuction() {return 128;}

	@Override
	public boolean renderExtendedTube() {return false;}
}
