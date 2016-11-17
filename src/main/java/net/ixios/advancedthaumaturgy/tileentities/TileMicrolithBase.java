package net.ixios.advancedthaumaturgy.tileentities;

import java.awt.Color;

import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

@Optional.InterfaceList({
		@Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral",modid = "ComputerCraft"),
		@Optional.Interface(iface = "li.cil.oc.api.network.SimpleComponent",modid = "OpenComputers")
})

public abstract class TileMicrolithBase extends TileEntity implements IPeripheral,SimpleComponent
{

	private Color color = null;
	private boolean active=false;
	
	public TileMicrolithBase(Color color)
	{
		this.color = new Color(color.getRed(), color.getGreen(), color.getBlue(), 255);
	}
	
	public Color getColor()
	{
		return color;
	}


	public boolean getActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active=active;
	}


	@Override
	public void readFromNBT(NBTTagCompound p_145839_1_) {
		super.readFromNBT(p_145839_1_);
		this.readExtraNBT(p_145839_1_);
	}

	public void readExtraNBT(NBTTagCompound tagCompound) {
		active=tagCompound.getBoolean("active");
	}


	@Override
	public void writeToNBT(NBTTagCompound p_145841_1_) {
		super.writeToNBT(p_145841_1_);
		this.writeExtraNBT(p_145841_1_);
	}

	public void writeExtraNBT(NBTTagCompound tagCompound) {
		tagCompound.setBoolean("active",active);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound cmp=new NBTTagCompound();
		writeExtraNBT(cmp);
		return new S35PacketUpdateTileEntity(xCoord,yCoord,zCoord,0,cmp);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		readExtraNBT(pkt.func_148857_g());
	}



	@Optional.Method(modid = "ComputerCraft")
	@Override
	public String getType() {
		return "microlith";
	}

	@Optional.Method(modid = "ComputerCraft")
	@Override
	public String[] getMethodNames() {
		return new String[]{"toggleActive","getActive","setActive","getMicrolithType"};
	}

	@Optional.Method(modid = "ComputerCraft")
	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		switch (method)
		{
			case 0:
				setActive(!getActive());
				return new Object[]{};
			case 1:
				return new Object[]{getActive()};
			case 2:
				setActive((Boolean)arguments[0]);
				return new Object[]{};
			case 3:
				return new Object[]{getMicrolithType()};
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
		return super.equals(other);
	}
	public abstract boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
											 float hitY, float hitZ);

	public abstract String getMicrolithType();

	@Override
	@Optional.Method(modid = "OpenComputers")
	public String getComponentName() {
		return "microlith";
	}

	@Optional.Method(modid = "OpenComputers")
	@Callback
	public Object[] getActive(Context context, Arguments args)
	{
		return new Object[]{getActive()};
	}

	@Optional.Method(modid = "OpenComputers")
	@Callback
	public Object[] getMicrolithType(Context context, Arguments args)
	{
		return new Object[]{getMicrolithType()};
	}

	@Optional.Method(modid = "OpenComputers")
	@Callback
	public Object[] setActive(Context context, Arguments args)
	{
		setActive(args.checkBoolean(0));
		return new Object[]{};
	}

	@Optional.Method(modid = "OpenComputers")
	@Callback
	public Object[] toggleActive(Context context, Arguments args)
	{
		setActive(!getActive());
		return new Object[]{};
	}
}
