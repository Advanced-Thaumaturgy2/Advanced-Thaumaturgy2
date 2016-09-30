package net.ixios.advancedthaumaturgy.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.ixios.advancedthaumaturgy.tileentities.TileNodeModifier;
import net.ixios.advancedthaumaturgy.tileentities.TileNodeModifier.Operation;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;


public class PacketStartNodeModification implements IMessage {
	
	private int x;
	private int y;
	private int z;
	private Operation op;
	
	/**
	 * Dummy constructor for forge
	 */
	public PacketStartNodeModification() 
	{
		
	}
	
	/**
	 * Constructor
	 * @param x,y,z Coordinates of the Modifier
	 * @param op Modifier operation
	 */
	public PacketStartNodeModification(int x, int y, int z, Operation op) 
	{
		this.x = x; this.y = y; this.z = z; this.op = op;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		op = Operation.parse(buf.readByte());
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeByte(op.getId());
	}
	
	public static class Handler implements IMessageHandler<PacketStartNodeModification, IMessage> 
	{
        @Override
        public IMessage onMessage(PacketStartNodeModification message, MessageContext ctx) 
        {
        	if (ctx.side == Side.SERVER) 
        	{
        		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
    			World world = player.worldObj;
    			TileEntity nm = world.getTileEntity(message.x, message.y, message.z);
    			
    			if (nm instanceof TileNodeModifier) 
    			{
        			((TileNodeModifier)nm).startProcess(message.op);
    			}
        	}
			return null;
        }
    }

}
