package net.ixios.advancedthaumaturgy.items;

import cpw.mods.fml.common.registry.GameRegistry;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;

public class ItemEndstoneChunk extends Item
{
	private IRecipe recipe;

	public ItemEndstoneChunk()
    {
	    super();
	    setUnlocalizedName("at.endstonechunk");
	    setTextureName("endstonechunk");
    }
	
	public IRecipe getRecipe()
	{
		return recipe;
	}
	
	@Override
	public void registerIcons(IIconRegister ir)
	{
	    itemIcon = ir.registerIcon("advthaum:endstonechunk");
	}

	public void register()
	{
		GameRegistry.registerItem(this, "endstonechunk");
		setCreativeTab(AdvThaum.tabAdvThaum);
		
		ItemStack endstone = new ItemStack(Blocks.end_stone);
		ItemStack emerald = new ItemStack(Items.emerald);
		
		ItemStack[] ingredients = new ItemStack[]{ emerald, endstone, emerald, 
													endstone, TCItems.anyshard, endstone, 
													emerald, endstone, emerald };
		recipe = new ShapedRecipes(3, 3, ingredients, new ItemStack(this));
		GameRegistry.addRecipe(recipe);
		
		//GameRegistry.addRecipe(new ItemStack(this, 4, 0), new Object[] 
		//		{ "ESE", "SCS", "ESE", 'S', endstone, 'C', TCItems.anyshard, 'E', Items.emerald });
		
	}
}
