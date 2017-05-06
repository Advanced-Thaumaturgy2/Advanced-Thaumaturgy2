package net.ixios.advancedthaumaturgy.gui;

import java.awt.Color;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringTranslate;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Rectangle;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.blocks.BlockNodeModifier;
import net.ixios.advancedthaumaturgy.tileentities.TileNodeModifier;
import net.ixios.advancedthaumaturgy.tileentities.TileNodeModifier.Operation;
import net.ixios.advancedthaumaturgy.tileentities.TileNodeModifier.Requirements;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.MinecraftForgeClient;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemWispEssence;
import thaumcraft.common.tiles.TileJarNode;

public class GuiNodeModifier extends GuiContainer
{
	private static RenderItem itemRenderer = new RenderItem();
	private ResourceLocation texture = new ResourceLocation("advthaum", "textures/gui/nodegui.png");
	private ResourceLocation buttonup = new ResourceLocation("advthaum", "textures/gui/button-up.png");
	//private ResourceLocation buttondown = new ResourceLocation("advthaum", "textures/gui/button-down.png");
	
	private static ItemStack node = null;
	
	public static final int id = 0;
	private EntityPlayer player = null;
	private World world;
	private int blockX, blockY, blockZ;
	private int selectedop = -1;
	private Rectangle buttonpos = new Rectangle(0, 0, 1, 1);
	private TileNodeModifier nm = null;
	
	public GuiNodeModifier(EntityPlayer plr, int x, int y, int z)
	{
		super(new ContainerNodeModifier(plr));
		this.player = plr;
		this.world = player.worldObj;
		this.blockX = x;
		this.blockY = y;
		this.blockZ = z;
		this.width = 250;
		this.height = 150;
		this.xSize = 250;
		this.ySize = 150;
		
		if (node == null)
			if(AdvThaum.CreativeNode!=null)
				node = new ItemStack(AdvThaum.CreativeNode);
		
		IItemRenderer r = MinecraftForgeClient.getItemRenderer(node, ItemRenderType.INVENTORY);
		if (r == null) {
			if (AdvThaum.ArcaneCrystal != null)
				node = new ItemStack(AdvThaum.ArcaneCrystal);
			else {
				node = new ItemStack(ConfigItems.itemResource, 1, 3);
			}
		}
		
		nm = (TileNodeModifier)world.getTileEntity(blockX, blockY, blockZ);
		
		BlockNodeModifier.refreshAvailableOperations(world, x, y, z);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
	{
		drawTexture(texture, guiLeft, guiTop, xSize, ySize, 1f, 0.75f);
		if (!(((TileNodeModifier)(world.getTileEntity(blockX,  blockY,  blockZ))).isActive()))
			drawButton();
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
	
	private void drawButton()
	{
		buttonpos.setBounds((guiLeft + xSize) - 60, (guiTop + ySize) - 20, 50, 20);
		drawTexture(buttonup, buttonpos.getX(), buttonpos.getY(), 0, 0, 1, 1);
		drawString(mc.fontRenderer, StatCollector.translateToLocal("at.nodeModifier.start"), buttonpos.getX() +  20, buttonpos.getY() + 4, (selectedop == -1 ? Color.gray.getRGB() : Color.white.getRGB()));
		
		itemRenderer.renderItemAndEffectIntoGUI(super.mc.fontRenderer, super.mc.renderEngine, node, buttonpos.getX() + 5, buttonpos.getY());
		
	}
	
	@Override
	public void drawScreen(int x, int y, float f)
	{

		super.drawScreen(x,  y,  f);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y)
	{
		super.drawGuiContainerForegroundLayer(x, y);
		
		if (nm == null)
			return;
		
		TileEntity te = world.getTileEntity(blockX, blockY + 1, blockZ);
		
		if (te == null)
		{

			fontRendererObj.drawSplitString(StatCollector.translateToLocal("at.nodeModifier.beginText"), 5, 5, 150, Color.WHITE.getRGB());
			return;
		}
		else if (!(te instanceof TileJarNode))
		{
			fontRendererObj.drawSplitString(StatCollector.translateToLocal("at.nodeModifier.invalidBeginText"), 20, ySize - 45, xSize - 30, Color.WHITE.getRGB());
			return;
		}
		
		if (nm.availableOperations.size() > 0)
		{
			for (int i = 0; i < nm.availableOperations.size(); i++)
			{
				Operation op = nm.availableOperations.get(i);
				drawString(fontRendererObj,  (i == selectedop ? "\u00a7n" : "") + StatCollector.translateToLocal(op.toString()), 20, 15 + (i * 12), Color.WHITE.getRGB());
			}
		}
	
		if (selectedop != -1 && selectedop < nm.availableOperations.size())
		{
			Operation op = nm.availableOperations.get(selectedop);
			Requirements reqs = nm.getRequirements(op);
			
			String todraw = "";
			todraw=StatCollector.translateToLocal(op.getDescription());
			
			// draw string
			fontRendererObj.drawSplitString(todraw, 20, ySize - 45, xSize - 30, Color.WHITE.getRGB());
			
			// draw required wisp items from right to left
			int xpos = xSize - 30;
			
			for (Aspect a : reqs.getEssenceArray())
			{
				
				// draw required wisp(s)
				int amt = reqs.getEssenceAmount(a);
				boolean match = ((amt & 0xFF) != 0);
				amt = (amt & 0x0F);
				
				ItemStack essence = new ItemStack(ConfigItems.itemWispEssence, reqs.getEssenceAmount(a), (match ? 0 : 32767));
				
				if (match)
					((ItemWispEssence)essence.getItem()).setAspects(essence, new AspectList().add(a, amt));
				
				itemRenderer.renderItemIntoGUI(super.mc.fontRenderer, super.mc.renderEngine, essence, xpos, 15);
				
				GL11.glPushMatrix();
	            GL11.glScalef(0.5F, 0.5F, 0.5F);
	            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	            String am = String.valueOf(reqs.getEssenceAmount(reqs.getCurrentEssence()));
	            int sw = mc.fontRenderer.getStringWidth(am);
	            mc.fontRenderer.drawString(am, (32 - sw) + xpos * 2, (32 - mc.fontRenderer.FONT_HEIGHT) + 15 * 2, 0xffffff);
	            GL11.glPopMatrix();
				
				int MouseX = (Mouse.getEventX() * this.width / this.mc.displayWidth) - guiLeft;
				int MouseY = (this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1) - guiTop;
			        
				Rectangle rect = new Rectangle(xpos, 15, 16, 16);

				
				xpos -= 20;
			
			}
			// draw aspect cost
			for (Aspect a : reqs.getEssentiaArray())
			{
				UtilsFX.drawTag(xpos, 15, a, reqs.getEssentiaAmount(a), 0, 2F);
				xpos -= 20;
			}
         
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	protected void mouseClicked(int x, int y, int keycode)
	{
		if (((TileNodeModifier)(world.getTileEntity(blockX,  blockY,  blockZ))).isActive())
			return;
		
		if (buttonpos.contains(x, y))
		{
			if (selectedop == -1)
				return;
			
			Operation op = nm.availableOperations.get(selectedop);
			AdvThaum.proxy.startModification(nm, op);
			return;
		}
		
		int possible = (int)((y - 15 - guiTop) / 12);
		if (possible >= 0 && possible <= nm.availableOperations.size())
			selectedop = possible;

	}
}
