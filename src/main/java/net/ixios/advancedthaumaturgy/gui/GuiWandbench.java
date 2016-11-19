package net.ixios.advancedthaumaturgy.gui;

import net.ixios.advancedthaumaturgy.tileentities.TileWandbench;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiWandbench extends GuiContainer
{
	private ResourceLocation texture = new ResourceLocation("advthaum", "textures/gui/wandbench.png");
	
	public static final int id = 1;
	
	public GuiWandbench(EntityPlayer player, TileWandbench wandbench)
	{
		super(new ContainerWandbench(player, wandbench));
		
		this.xSize = 176;
		this.ySize = 166;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
	{
		drawTexture(texture, guiLeft, guiTop, xSize, ySize, xSize / 256.0f, ySize / 256.0f);
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
