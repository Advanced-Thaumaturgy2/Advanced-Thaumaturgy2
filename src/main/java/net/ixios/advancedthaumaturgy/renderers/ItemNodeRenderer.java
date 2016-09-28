package net.ixios.advancedthaumaturgy.renderers;

import net.ixios.advancedthaumaturgy.AdvThaum;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.tiles.TileNode;

public class ItemNodeRenderer implements IItemRenderer
{
	public static ResourceLocation texture = new ResourceLocation("thaumcraft", "textures/misc/nodes.png");
	public AspectList aspects;

    public ItemNodeRenderer()
    {
        aspects = (new AspectList()).add(Aspect.AIR, 40).add(Aspect.FIRE, 40).add(Aspect.EARTH, 40).add(Aspect.WATER, 40);
    }
    
    public ItemNodeRenderer(AspectList aspects)
    {
    	this.aspects = aspects;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        return item != null && item.getItem().equals(Item.getItemFromBlock(AdvThaum.CreativeNode)) && (item.getItemDamage() == 0 || item.getItemDamage() == 5);
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return helper != IItemRenderer.ItemRendererHelper.EQUIPPED_BLOCK;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
        if (type == ItemRenderType.ENTITY)
            GL11.glTranslatef(-0.5F, -0.25F, -0.5F);
        else if (type == net.minecraftforge.client.IItemRenderer.ItemRenderType.EQUIPPED && (data[1] instanceof EntityPlayer))
            GL11.glTranslatef(0.0F, 0.0F, -0.5F);
        TileNode tjf = new TileNode();
        tjf.setAspects(aspects);
        tjf.setNodeType(NodeType.NORMAL);
        tjf.blockType = ConfigBlocks.blockAiry;
        tjf.blockMetadata = 0;
        GL11.glPushMatrix();
        GL11.glTranslated(0.5D, 0.5D, 0.5D);
        GL11.glScaled(2D, 2D, 2D);
        renderItemNode(tjf);
        GL11.glRotatef(90F, 0.0F, 1.0F, 0.0F);
        renderItemNode(tjf);
        GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
        renderItemNode(tjf);
        GL11.glPopMatrix();
        GL11.glEnable(32826);
    }

    public static void renderItemNode(INode node)
    {
        if(node.getAspects().size() > 0)
        {
            EntityLivingBase viewer = Minecraft.getMinecraft().renderViewEntity;
            float alpha = 0.5F;

            if(node.getNodeModifier() != null)
            {
                switch(node.getNodeModifier())
                {
                case BRIGHT: // '\001'
                    alpha *= 1.5F;
                    break;

                case PALE: // '\002'
                    alpha *= 0.66F;
                    break;

                case FADING: // '\003'
                    alpha *= MathHelper.sin((float)viewer.ticksExisted / 3F) * 0.25F + 0.33F;
                    break;
                }
            }
            GL11.glPushMatrix();
            GL11.glAlphaFunc(516, 0.003921569F);
            GL11.glDepthMask(false);
            GL11.glDisable(2884);
            
            long nt = System.nanoTime();
            float bscale = 0.25F;
            
            GL11.glPushMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
            
  	      	UtilsFX.bindTexture(texture);
  	      	int frames = 32;
  	      	int i = (int)((nt / 0x2625a00L + 1L) % (long)frames);
            
  	      	int count = 0;
            float scale = 0.0F;
            float average = 0.0F;
            
            for (Aspect aspect : node.getAspects().getAspects())
            {
                if(aspect.getBlend() == 771)
                    alpha = (float)((double)alpha * 1.5D);
                average += node.getAspects().getAmount(aspect);
                GL11.glPushMatrix();
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, aspect.getBlend());
                scale = MathHelper.sin((float)((Entity) (viewer)).ticksExisted / (14.0F - (float)count)) * bscale + bscale * 2.0F;
                scale = 0.2F + scale * ((float)node.getAspects().getAmount(aspect) / 50.0F);
                UtilsFX.renderAnimatedQuadStrip(scale, alpha / (float)node.getAspects().size(), frames, 0, i, 0.0F, aspect.getColor());
                GL11.glDisable(3042);
                GL11.glPopMatrix();
                count++;
                if(aspect.getBlend() == 771)
                    alpha = (float)((double)alpha / 1.5D);
            }

            average /= node.getAspects().size();
            GL11.glPushMatrix();
            GL11.glEnable(3042);
            i = (int)((nt / 0x2625a00L + 1L) % (long)frames);
            scale = 0.1F + average / 150F;
            int strip = 1;
            
  	      	switch (node.getNodeType()) 
  	      	{
  	      	case NORMAL: // 1
  	      		GL11.glBlendFunc(770, 1);
  	      		break;
  	      	case UNSTABLE: // 2
  	      		GL11.glBlendFunc(770, 1);
  	      		strip = 6;
  	      		break;
  	      	case DARK: // 3
  	      		GL11.glBlendFunc(770, 771);
  	      		strip = 2;
  	      		break;
  	      	case TAINTED: // 4
  	      		GL11.glBlendFunc(770, 771);
  	      		strip = 5;
  	      		break;
  	      	case PURE: // 5
  	      		GL11.glBlendFunc(770, 1);
  	      		strip = 4;
  	      		break;
  	      	case HUNGRY: // 6
  	      		scale *= 0.75F;
  	      		GL11.glBlendFunc(770, 1);
  	        	strip = 3;
  	      	}
            
            GL11.glColor4f(1.0F, 0.0F, 1.0F, alpha);
            UtilsFX.renderAnimatedQuadStrip(scale, alpha, frames, strip, i, 0.0F, 0xffffff);
            
            GL11.glDisable(3042);
            GL11.glPopMatrix();
            GL11.glPopMatrix();
            GL11.glEnable(2884);
            GL11.glDepthMask(true);
            GL11.glAlphaFunc(516, 0.1F);
            GL11.glPopMatrix();
        }
    }

}
