package net.ixios.advancedthaumaturgy.tileentities;

import net.ixios.advancedthaumaturgy.blocks.BlockCreativeNode;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.common.tiles.TileNode;

public class TileCreativeNode extends TileNode
{

	public static final AspectList aspects = new AspectList().add(Aspect.AIR, 100)
                                                             .add(Aspect.EARTH, 100)
                                                             .add(Aspect.FIRE, 100)
                                                             .add(Aspect.WATER, 100)
                                                             .add(Aspect.ORDER, 100)
                                                             .add(Aspect.ENTROPY, 100);
	
	public TileCreativeNode()
	{
		setAspects(null);
		setNodeType(NodeType.PURE);
		setNodeModifier(NodeModifier.BRIGHT);
	}

	@Override
	public boolean takeFromContainer(AspectList list) { return true; }

	@Override
	public boolean takeFromContainer(Aspect aspect, int amount) { return true; }

	@Override
	public void setAspects(AspectList a) 
	{
		super.setAspects(aspects.copy());
	}
	
	@Override
	public Aspect takeRandomPrimalFromSource()
	{
		Aspect[] primals = getAspects().getPrimalAspects();
		Aspect asp = primals[this.worldObj.rand.nextInt(primals.length)];
		if (asp != null) {
			return asp;
		}
		return null;
	}
	
}
