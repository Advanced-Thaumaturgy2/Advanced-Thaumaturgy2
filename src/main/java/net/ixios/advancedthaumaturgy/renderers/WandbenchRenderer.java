package net.ixios.advancedthaumaturgy.renderers;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import net.ixios.advancedthaumaturgy.tileentities.TileWandbench;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import thaumcraft.client.renderers.models.ModelArcaneWorkbench;
import thaumcraft.common.items.wands.ItemWandCasting;

public class WandbenchRenderer extends TileEntitySpecialRenderer implements IItemRenderer
{
	public static final ResourceLocation texture = new ResourceLocation("advthaum", "textures/blocks/wandbench.png");
	private ModelArcaneWorkbench model = new ModelArcaneWorkbench();
	
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) 
	{
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) 
	{
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) 
	{
		GL11.glPushMatrix();
        
    	if (type == ItemRenderType.INVENTORY)
    	{
            GL11.glRotatef((float)90.0f, (float)0.0f, (float)1.0f, (float)0.0f);
    		GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
    	}
        
        renderTileEntityAt(null, 0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glPopMatrix();
	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z,
			float p_147500_8_) 
	{
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(texture);
		
        GL11.glPushMatrix();
		    GL11.glTranslatef((float)((float)x + 0.5f), (float)((float)y + 1.0f), (float)((float)z + 0.5f));
		    GL11.glRotatef((float)180.0f, (float)1.0f, (float)0.0f, (float)0.0f);
		    GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
		    
		    model.renderAll();
        GL11.glPopMatrix();
        
        // Draw vis supply wand
        TileWandbench bench = (TileWandbench) te;
        if (bench != null && bench.getWorldObj() != null && bench.getStackInSlot(5) != null 
        		&& bench.getStackInSlot(5).getItem() instanceof ItemWandCasting) 
        {
            GL11.glPushMatrix();
	            GL11.glTranslatef((float)((float)x + 0.65f), (float)((float)y + 1.0625f), (float)((float)z + 0.25f));
	            GL11.glRotatef((float)90.0f, (float)1.0f, (float)0.0f, (float)0.0f);
	            GL11.glRotatef((float)20.0f, (float)0.0f, (float)0.0f, (float)1.0f);
	            
	            ItemStack is = bench.getStackInSlot(5).copy();
	            is.stackSize = 1;
	            EntityItem entityitem = new EntityItem(bench.getWorldObj(), 0.0, 0.0, 0.0, is);
	            entityitem.hoverStart = 0.0f;
	            RenderItem.renderInFrame = true;
	            RenderManager.instance.renderEntityWithPosYaw((Entity)entityitem, 0.0, 0.0, 0.0, 0.0f, 0.0f);
	            RenderItem.renderInFrame = false;
            GL11.glPopMatrix();
        }
	}

}
