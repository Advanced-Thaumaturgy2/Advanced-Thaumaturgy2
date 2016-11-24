package net.ixios.advancedthaumaturgy.gui;

import java.util.Map;

import net.ixios.advancedthaumaturgy.tileentities.TileWandbench;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.lib.UtilsFX;

public class GuiWandbench extends GuiContainer
{
	private ResourceLocation texture = new ResourceLocation("advthaum", "textures/gui/wandbench.png");
	
	public static final int id = 1;
	
	private TileWandbench wandbench;
	private EntityPlayer player;
	
	public GuiWandbench(EntityPlayer player, TileWandbench wandbench)
	{
		super(new ContainerWandbench(player, wandbench));
		
		this.wandbench = wandbench;
		this.player = player;
		
		this.xSize = 176;
		this.ySize = 166;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
	{
		drawTexture(texture, guiLeft, guiTop, xSize, ySize, xSize / 256.0f, ySize / 256.0f);
		
		Map<Aspect, Float> cost = wandbench.getRealCost(player);
		if (cost.size() > 0)
		{
			int pos = 0;
			for (Aspect a : cost.keySet())
			{
				UtilsFX.drawTag(guiLeft + 68 + pos++ * 16, guiTop + 56, a, cost.get(a), 0, zLevel);
			}
		}
	}
 
	private void drawTexture(ResourceLocation tex, int x, int y, int w, int h, float u, float v)
	{
		mc.renderEngine.bindTexture(tex);
		
		Tessellator tessellator = Tessellator.instance;
	    tessellator.startDrawingQuads();
	    tessellator.addVertexWithUV(x, y, zLevel, 0, 0);
	    tessellator.addVertexWithUV(x, y + h, zLevel, 0, v);
	    tessellator.addVertexWithUV(x + w, y + h, zLevel, u, v);
	    tessellator.addVertexWithUV(x + w, y, zLevel, u, 0);
	    tessellator.draw();
	}
}
