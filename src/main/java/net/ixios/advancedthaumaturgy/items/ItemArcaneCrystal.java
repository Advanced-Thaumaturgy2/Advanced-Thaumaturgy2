package net.ixios.advancedthaumaturgy.items;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.misc.ATResearchItem;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.StatCollector;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.config.ConfigResearch;

public class ItemArcaneCrystal extends Item
{
	public ItemStack base 		= new ItemStack(this, 1, 0);
    public ItemStack recharge 	= new ItemStack(this, 1, 1);
    public ItemStack compound	= new ItemStack(this, 1, 2);
    public ItemStack multiplier = new ItemStack(this, 1, 3);
    public ItemStack stabilizer = new ItemStack(this, 1, 4);
    public ItemStack discount	= new ItemStack(this, 1, 5);
    public ItemStack potency	= new ItemStack(this, 1, 6);
    
    public ItemStack dissipator = new ItemStack(this, 1, 20);
    public ItemStack watchful	= new ItemStack(this, 1, 21);
    public ItemStack healing	= new ItemStack(this, 1, 22);
    public ItemStack lightning	= new ItemStack(this, 1, 23);
    public ItemStack fire		= new ItemStack(this, 1, 24);
    public ItemStack frost		= new ItemStack(this, 1, 25);

	public enum Upgrades
	{
		None(0),
		Recharge(1),
		CompoundDrain(2),
		MultiplyDrain(4),
		Stabilizer(8),
		Discount(16),
		Potent(32);
			
		private int flag;
		Upgrades(int flag)
		{
			this.flag = flag;
		}
		public int getFlag()
		{
			return flag;
		}
		public static Upgrades parse(int flag)
		{
			for (Upgrades u : Upgrades.values())
				if (flag == u.getFlag())
					return u;
			return None;
		}
	}
	
	public ItemArcaneCrystal()
    {
	    super();
	    setHasSubtypes(true);
	    setUnlocalizedName("at.arcanecrystal");
    }
	
	public Upgrades getUpgradeFromStack(ItemStack stack)
	{
		int dmg = stack.getItemDamage();
		if (dmg == 0)
			return Upgrades.None;
		return Upgrades.parse(1 << (dmg - 1));
	}
	
	public boolean isWandUpgrade(ItemStack stack)
	{
		return stack.getItemDamage() >= 0 && stack.getItemDamage() < 20;
	}

	public void register()
	{
		GameRegistry.registerItem(this, "upgradecrystal");
		setCreativeTab(AdvThaum.tabAdvThaum);

		GameRegistry.addSmelting(new ItemStack(AdvThaum.EndstoneChunk), new ItemStack(this, 1, 0), 0);
		
		ATResearchItem ri = new ATResearchItem("ARCANECRYSTAL", "ADVTHAUM",
				new AspectList().add(Aspect.CRYSTAL, 10).add(Aspect.MAGIC, 10).add(Aspect.ORDER, 10),
				-4, 2, 5,
				new ItemStack(this, 1, 0));
				 
		ri.setTitle("at.research.arcanecrystal.title");
		ri.setInfo("at.research.arcanecrystal.desc");
		
		ri.setParents("MERCURIALWAND");
		
		ri.setPages(new ResearchPage("at.research.arcanecrystal.pg1"), new ResearchPage(AdvThaum.EndstoneChunk.getRecipe()),
			 new ResearchPage(new ItemStack(AdvThaum.EndstoneChunk)));
		
		ri.setStub();
		ri.setConcealed();
		
		ri.registerResearchItem();
		
		// these are wand upgrade researches
		
		registerRechargeUpgrade();
		registerDrainMultiplier();
		registerCompoundDrain();
		registerVisDiscount();
		registerStabilizer();
		registerPotency();
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubItems(Item par1, CreativeTabs tab, List list)
	{
		list.add(base);
		list.add(recharge);
		list.add(compound);
		list.add(multiplier);
		list.add(discount);
		list.add(stabilizer);
		list.add(potency);
		
		list.add(dissipator);
		list.add(watchful);
		list.add(healing);
		list.add(lightning);
		list.add(fire);
		list.add(frost);
	}
	
	@Override
	public void registerIcons(IIconRegister ir)
	{
	    itemIcon = ir.registerIcon("advthaum:wandupgrade");
	}
	
	@Override
	public int getColorFromItemStack(ItemStack stack, int par2)
	{
	    switch (stack.getItemDamage())
	    {
	    	case 0:
	    		return 0xFFFFFFFF;
	    	case 1:
	    		return 0xFFFF0000;
	    	case 2:
	    		return 0xFF00FFFF;
	    	case 3:
	    		return 0xFF0000FF;
	    	case 4:
	    		return 0xFFFF00FF;
	    	case 5:
	    		return 0xFF00FFFF;
	    	case 6:
	    		return 0xFF00FF00;
	    		
	    	case 20: // flux dissipator crystal
	    		return 0xFF800080;
	    	case 21: // watchful crystal
	    		return 0xFF0000FF;
	    	case 22: // heal
	    		return Aspect.HEAL.getColor();
	    	case 23: // lightning
	    		return Aspect.AIR.getColor();
	    	case 24: // fire
	    		return Aspect.FIRE.getColor();
	    	case 25: // frost
	    		return Aspect.COLD.getColor();
	    	default:
	    		return 0xFFFFFFFF;
	    }
	}


	@Override
	public String getItemStackDisplayName(ItemStack stack)
	{
	    return StatCollector.translateToLocal("item.at.arcanecrystal." + stack.getItemDamage() + ".name");
	}
	
	private void registerRechargeUpgrade()
	{
		InfusionRecipe recipe = ThaumcraftApi.addInfusionCraftingRecipe("UPGRADERECHARGE", recharge, 5,
				new AspectList().add(Aspect.EARTH, 32).add(Aspect.FIRE,  32).add(Aspect.AIR, 32).add(Aspect.WATER, 32).add(Aspect.ORDER, 32).add(Aspect.ENTROPY, 32).add(Aspect.EXCHANGE, 32),
				base, new ItemStack[] { ConfigItems.WAND_ROD_BLAZE.getItem(), ConfigItems.WAND_ROD_BONE.getItem(),
															 ConfigItems.WAND_ROD_ICE.getItem(), ConfigItems.WAND_ROD_OBSIDIAN.getItem(),
															 ConfigItems.WAND_ROD_QUARTZ.getItem(), ConfigItems.WAND_ROD_REED.getItem() });

		Object[] tag = new Object[2];
		tag[0] = "upgrade";
		tag[1] = new NBTTagInt( Upgrades.Recharge.getFlag());

		 InfusionRecipe upgrade = ThaumcraftApi.addInfusionCraftingRecipe("UPGRADERECHARGE", tag, 4,
				 new AspectList().add(Aspect.MAGIC, 8), ItemMercurialWand.AnyWand,
				 new ItemStack[] { recharge });
		ConfigResearch.recipes.put("WANDUPGRADEREHARGE", recipe);
		 
		ATResearchItem ri = new ATResearchItem("UPGRADERECHARGE", "ADVTHAUM",
					new AspectList().add(Aspect.AIR, 20).add(Aspect.FIRE, 20).add(Aspect.WATER, 20).add(Aspect.EARTH, 20).add(Aspect.ORDER, 20).add(Aspect.ENTROPY, 20),
					//-2, 0, 5,
					-3, 0, 5,
					recharge);
					 
		 ri.setTitle("item.at.arcanecrystal." + recharge.getItemDamage() + ".name");
		 ri.setInfo("at.research.rechargeupgrade.desc");
		 
		 ri.setPages(new ResearchPage("at.research.rechargeupgrade.pg1"), new ResearchPage(recipe), new ResearchPage(upgrade));
		 
		 ri.setParents("ARCANECRYSTAL", "INFUSION", "ROD_reed", "ROD_blaze", "ROD_obsidian", "ROD_ice", "ROD_quartz", "ROD_bone");
		 		 
		 ri.setStub();
		 ri.setSecondary();
		 ri.setConcealed();
		 
		 ri.registerResearchItem();
		
	}
	
	private void registerDrainMultiplier()
	{
		ItemStack blaze = new ItemStack(Items.blaze_powder);
		ItemStack tear = new ItemStack(Items.ghast_tear);
		ItemStack cream = new ItemStack(Items.magma_cream);
		ItemStack poisonpotato = new ItemStack(Items.poisonous_potato);
		
		ItemStack potato = new ItemStack(Items.potato);
		ItemStack eye = new ItemStack(Items.spider_eye);
		
		ItemStack pearl = new ItemStack(Items.ender_pearl);
		
		GameRegistry.addRecipe(new ItemStack(Items.poisonous_potato), new Object[] { "EEE", "EPE", "EEE", 'E', eye, 'P', potato });
		
		InfusionRecipe recipe = ThaumcraftApi.addInfusionCraftingRecipe("UPGRADEDRAIN", multiplier, 5,
				new AspectList().add(Aspect.ARMOR, 10).add(Aspect.DEATH,  10).add(Aspect.PLANT, 10).add(Aspect.COLD, 10).add(Aspect.SLIME, 10).add(Aspect.SLIME, 10),
				base, new ItemStack[] { blaze, pearl, tear, pearl, cream, pearl, poisonpotato, pearl });

		
		
		
		Object[] tag = new Object[2];
		tag[0] = "upgrade";
		tag[1] = new NBTTagInt(Upgrades.MultiplyDrain.getFlag());
		InfusionRecipe upgrade = ThaumcraftApi.addInfusionCraftingRecipe("UPGRADEDRAIN", tag, 4, 
				 new AspectList().add(Aspect.MAGIC, 8), ItemMercurialWand.AnyWand, 
				 new ItemStack[] { multiplier });
		 ConfigResearch.recipes.put("WANDUPGRADEDRAIN", recipe);
		 
		 // drain multiplier
		ATResearchItem ri = new ATResearchItem("UPGRADEDRAIN", "ADVTHAUM",
					recipe.getAspects(),
					//-6, 0, 5,
					-6, 1, 5,
					multiplier);
					 
		 ri.setTitle("item.at.arcanecrystal." + multiplier.getItemDamage() + ".name");
		 ri.setInfo("at.research.upgradedrain.desc");
		 
		 ri.setParents("ARCANECRYSTAL", "INFUSION", "DISTILESSENTIA");
		 
		 ri.setPages(new ResearchPage("at.research.upgradedrain.pg1"), new ResearchPage(recipe), new ResearchPage(upgrade));
		 
		 ri.setStub();
		 ri.setSecondary();
		 ri.setConcealed();
		 
		 ri.registerResearchItem();
		 
	}

	private void registerCompoundDrain()
	{
		InfusionRecipe recipe = ThaumcraftApi.addInfusionCraftingRecipe("UPGRADECOMPOUND", compound, 5,
				new AspectList().add(Aspect.SLIME, 16).add(Aspect.EXCHANGE,  16),
				base, new ItemStack[] { TCItems.arcanefurance, TCItems.arcanefurance, TCItems.arcanefurance, TCItems.arcanefurance });
		
		Object[] tag = new Object[2];
		tag[0] = "upgrade";
		tag[1] = new NBTTagInt(Upgrades.CompoundDrain.getFlag());
		 InfusionRecipe upgrade = ThaumcraftApi.addInfusionCraftingRecipe("UPGRADECOMPOUND", tag, 4, 
				 new AspectList().add(Aspect.MAGIC, 8), ItemMercurialWand.AnyWand, 
				 new ItemStack[] { compound });
		 
		 ConfigResearch.recipes.put("WANDUPGRADECOMPOUND", recipe);
		 
		 
		ATResearchItem ri = new ATResearchItem("UPGRADECOMPOUND", "ADVTHAUM",
					new AspectList().add(Aspect.BEAST, 32).add(Aspect.DARKNESS,  32).add(Aspect.CLOTH, 16).add(Aspect.FLESH, 16).add(Aspect.POISON, 8).add(Aspect.SLIME, 8),
					//-4, -1, 5,
					-5, 0, 5,
					compound);
					 
		 ri.setTitle("item.at.arcanecrystal." + compound.getItemDamage() + ".name");
		 ri.setInfo("at.research.upgradecompound.desc");
		 
		 ri.setParents("ARCANECRYSTAL", "INFUSION");
		 
		 ri.setPages(new ResearchPage("at.research.upgradecompound.pg1"), new ResearchPage(recipe), new ResearchPage(upgrade));
		 
		 ri.setStub();
		 ri.setSecondary();
		 ri.setConcealed();
		 
		 ri.registerResearchItem();
		
	}

	private void registerVisDiscount()
	{
		InfusionRecipe recipe = ThaumcraftApi.addInfusionCraftingRecipe("UPGRADEDISCOUNT", discount, 5,
				new AspectList().add(Aspect.MAGIC, 10).add(Aspect.ELDRITCH,  10).add(Aspect.AURA, 10).add(Aspect.ENERGY, 10).add(Aspect.ENTROPY, 10),
				base, new ItemStack [] { TCItems.cloth, TCItems.aircluster, TCItems.cloth, TCItems.watercluster,
										 TCItems.cloth, TCItems.firecluster, TCItems.cloth, TCItems.earthcluster,
										 TCItems.cloth, TCItems.ordocluster, TCItems.cloth, TCItems.entropycluster });
		

		Object[] tag = new Object[2];
		tag[0] = "upgrade";
		tag[1] = new NBTTagInt(Upgrades.Discount.getFlag());
		 InfusionRecipe upgrade = ThaumcraftApi.addInfusionCraftingRecipe("UPGRADEDISCOUNT", tag, 4, 
				 new AspectList().add(Aspect.MAGIC, 8), ItemMercurialWand.AnyWand, 
				 new ItemStack[] { discount });
		 
		 ConfigResearch.recipes.put("WANDUPGRADEDISCOUNT", recipe);
		 
		ATResearchItem ri = new ATResearchItem("UPGRADEDISCOUNT", "ADVTHAUM",
					recipe.getAspects(),
					//-2, 4, 5,
					-3, 4, 5,
					discount);
					 
		ri.setTitle("item.at.arcanecrystal." + discount.getItemDamage() + ".name");
		ri.setInfo("at.research.upgradediscount.desc");
		
		ri.setParents("ARCANECRYSTAL", "INFUSION", "ENCHFABRIC");
		
		ri.setPages(new ResearchPage("at.research.upgradediscount.pg1"), new ResearchPage(recipe), new ResearchPage(upgrade));
		
		ri.setStub();
		ri.setSecondary();
		ri.setConcealed();
		
		ri.registerResearchItem();
		 
	}

	private void registerStabilizer()
	{
		InfusionRecipe recipe = ThaumcraftApi.addInfusionCraftingRecipe("UPGRADESTABILIZER", stabilizer, 5,
				new AspectList().add(Aspect.ORDER, 128).add(Aspect.MAGIC,  64),
				base, new ItemStack [] { TCItems.ordocluster, TCItems.ordocluster, TCItems.ordocluster, TCItems.ordocluster,
															  TCItems.ordocluster, TCItems.ordocluster, TCItems.ordocluster, TCItems.ordocluster});

		Object[] tag = new Object[2];
		tag[0] = "upgrade";
		tag[1] = new NBTTagInt(Upgrades.Stabilizer.getFlag());
		InfusionRecipe upgrade = ThaumcraftApi.addInfusionCraftingRecipe("UPGRADESTABILIZER", tag, 4, 
				 new AspectList().add(Aspect.MAGIC, 8), ItemMercurialWand.AnyWand, 
				 new ItemStack[] { stabilizer });
		 
		 ConfigResearch.recipes.put("WANDUPGRADESTABILIZER", recipe);
		 
		ATResearchItem ri = new ATResearchItem("UPGRADESTABILIZER", "ADVTHAUM",
					new AspectList().add(Aspect.ORDER, 16).add(Aspect.MAGIC, 16),
					//-4, 5, 5,
					-5, 4, 5,
					stabilizer);
					 
		 ri.setTitle("item.at.arcanecrystal." + stabilizer.getItemDamage() + ".name");
		 ri.setInfo("at.research.upgradestabilizer.desc");
		 
		 ri.setParents("ARCANECRYSTAL", "INFUSION");
		 
		 ri.setPages(new ResearchPage("at.research.upgradestabilizer.pg1"), new ResearchPage(recipe), new ResearchPage(upgrade));
		 
		 ri.setStub();
		 ri.setSecondary();
		 ri.setConcealed();
		 
		 ri.registerResearchItem();
		 
	}
	
	private void registerPotency()
	{
		InfusionRecipe recipe = ThaumcraftApi.addInfusionCraftingRecipe("UPGRADEPOTENCY", potency, 5,
				new AspectList().add(Aspect.ORDER, 128).add(Aspect.MAGIC,  64),
				base, new ItemStack [] { TCItems.ordocluster, TCItems.ordocluster, TCItems.ordocluster, TCItems.ordocluster });

		Object[] tag = new Object[2];
		tag[0] = "upgrade";
		tag[1] = new NBTTagInt(Upgrades.Potent.getFlag());
		 InfusionRecipe upgrade = ThaumcraftApi.addInfusionCraftingRecipe("UPGRADEPOTENCY", tag, 4, 
				 new AspectList().add(Aspect.MAGIC, 8), ItemMercurialWand.AnyWand, 
				 new ItemStack[] { potency });
		 
		 ConfigResearch.recipes.put("WANDUPGRADEPOTENCY", recipe);
		 
		ATResearchItem ri = new ATResearchItem("UPGRADEPOTENCY", "ADVTHAUM",
					new AspectList().add(Aspect.ENERGY, 32),
					//-6, 4, 5,
					-6, 3, 5,
					potency);
					 
		 ri.setTitle("item.at.arcanecrystal." + potency.getItemDamage() + ".name");
		 ri.setInfo("at.research.upgradepotency.desc");
		 
		 ri.setParents("ARCANECRYSTAL", "INFUSION");
		 
		 ri.setPages(new ResearchPage("at.research.upgradepotency.pg1"), new ResearchPage(recipe), new ResearchPage(upgrade));
		 
		 ri.setStub();
		 ri.setSecondary();
		 ri.setConcealed();
		 
		 ri.registerResearchItem();
		 
	}
}
