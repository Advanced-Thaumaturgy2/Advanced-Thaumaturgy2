package net.ixios.advancedthaumaturgy;

import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import net.ixios.advancedthaumaturgy.blocks.BlockAltarDeployer;
import net.ixios.advancedthaumaturgy.blocks.BlockCreativeNode;
import net.ixios.advancedthaumaturgy.blocks.BlockEssentiaEngine;
import net.ixios.advancedthaumaturgy.blocks.BlockEtherealJar;
import net.ixios.advancedthaumaturgy.blocks.BlockMicrolith;
import net.ixios.advancedthaumaturgy.blocks.BlockNodeModifier;
import net.ixios.advancedthaumaturgy.blocks.BlockPlaceholder;
import net.ixios.advancedthaumaturgy.blocks.BlockThaumicFertilizer;
import net.ixios.advancedthaumaturgy.blocks.BlockThaumicVulcanizer;
import net.ixios.advancedthaumaturgy.compat.energy.BCCompatChecker;
import net.ixios.advancedthaumaturgy.compat.energy.EnergyCompatBase;
import net.ixios.advancedthaumaturgy.items.ItemAeroSphere;
import net.ixios.advancedthaumaturgy.items.ItemArcaneCrystal;
import net.ixios.advancedthaumaturgy.items.ItemEndstoneChunk;
import net.ixios.advancedthaumaturgy.items.ItemEtherealJar;
import net.ixios.advancedthaumaturgy.items.ItemFocusVoidCage;
import net.ixios.advancedthaumaturgy.items.ItemInfusedThaumium;
import net.ixios.advancedthaumaturgy.items.ItemMercurialRod;
import net.ixios.advancedthaumaturgy.items.ItemMercurialRodBase;
import net.ixios.advancedthaumaturgy.items.ItemMercurialWand;
import net.ixios.advancedthaumaturgy.misc.ATCreativeTab;
import net.ixios.advancedthaumaturgy.misc.ATEventHandler;
import net.ixios.advancedthaumaturgy.misc.ATServerCommand;
import net.ixios.advancedthaumaturgy.misc.ArcingDamageManager;
import net.ixios.advancedthaumaturgy.misc.ChunkLoadingClass;
import net.ixios.advancedthaumaturgy.network.PacketStartNodeModification;
import net.ixios.advancedthaumaturgy.proxies.CommonProxy;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.api.wands.WandRod;
import thaumcraft.api.wands.WandTriggerRegistry;
import thaumcraft.common.Thaumcraft;

@Mod(modid=AdvThaum.MODID, version="2.0", name="Advanced Thaumaturgy",
	dependencies="required-after:Thaumcraft", acceptedMinecraftVersions="1.7.10")
public class AdvThaum 
{
	public final static String MODID = "AdvancedThaumaturgy";

	@Instance
	public static AdvThaum instance;
	
	@SidedProxy(clientSide="net.ixios.advancedthaumaturgy.proxies.ClientProxy",
				serverSide="net.ixios.advancedthaumaturgy.proxies.CommonProxy")
	public static CommonProxy proxy;
	
	public static CreativeTabs tabAdvThaum = new ATCreativeTab("advthaum");
	public static Configuration config = null;
	
	// items
	public static ItemMercurialRod MercurialRod;
	public static ItemMercurialRodBase MercurialRodBase;
	public static ItemMercurialWand MercurialWand;
	public static ItemInfusedThaumium InfusedThaumium;
	//public static ItemThaumInkwell ThaumicInkwell;
	
	public static ItemFocusVoidCage FocusVoidCage;
	public static ItemEtherealJar itemEtherealJar;
	public static ItemAeroSphere AeroSphere;
	public static ItemArcaneCrystal ArcaneCrystal;
	public static ItemEndstoneChunk EndstoneChunk;
	
	// blocks
	public static BlockNodeModifier NodeModifier;
	public static BlockThaumicFertilizer ThaumicFertilizer;
	public static BlockCreativeNode CreativeNode;
	public static BlockEssentiaEngine EssentiaEngine;
	public static BlockThaumicVulcanizer ThaumicVulcanizer;
	public static BlockPlaceholder Placeholder;
	public static BlockEtherealJar EtherealJar;
	public static BlockMicrolith Microlith;
	public static BlockAltarDeployer AltarDeployer;
	
	//public static RenderTickManager rendermanager = new RenderTickManager();

	public static SimpleNetworkWrapper channel;
	
	private static Logger logger;
	
	public static boolean debug = false;
	
	 @EventHandler
     public void preInit(FMLPreInitializationEvent event)
	 {
	     logger=event.getModLog();

	     NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);
	     channel = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
	     channel.registerMessage(PacketStartNodeModification.Handler.class, PacketStartNodeModification.class, 1, Side.SERVER);

	     config = new Configuration(event.getSuggestedConfigurationFile());
	     
	     config.load();
	     

	     
	     boolean useClassicTooltip = config.get("Feature Control", "classic_wand_tooltip", false).getBoolean(false);
	     
	     ////////////////////////////////////////////////////////
	 	     
	     if (config.get("Feature Control", "enable_altar_deployer", true).getBoolean(true))
	    	 AltarDeployer = new BlockAltarDeployer();
	     
	     if (config.get("Feature Control", "enable_infused_thaumium", true).getBoolean(true))
	    	 InfusedThaumium = new ItemInfusedThaumium();
	     
	     if (config.get("Feature Control", "enable_node_modifier", true).getBoolean(true))
	    	 NodeModifier = new BlockNodeModifier(Material.ground);
	     
	     if (config.get("Feature Control", "enable_fertilizer", true).getBoolean(true))
	    	 ThaumicFertilizer = new BlockThaumicFertilizer(Material.ground);
	     
	     if (config.get("Feature Control", "enable_creative_node", true).getBoolean(true))
	    	 CreativeNode = new BlockCreativeNode();
	     
	     if (config.get("Feature Control", "enable_vulcanizer", true).getBoolean(true))
	    	 ThaumicVulcanizer = new BlockThaumicVulcanizer( Material.ground);
	     
	     if (config.get("Feature Control", "enable_ethereal_jar", true).getBoolean(true))
	     {
	    	 EtherealJar = new BlockEtherealJar();
	    	 itemEtherealJar = new ItemEtherealJar();
	     }
	     
	     if (config.get("Feature Control", "enable_miniligh", true).getBoolean(true))
	    	 Microlith = new BlockMicrolith(Material.ground);
	      
	     if (config.get("Feature Control", "enable_focus_void_cage", true).getBoolean(true))
	    	 FocusVoidCage = new ItemFocusVoidCage();
	     
	     if (config.get("Feature Control", "enable_aerosphere", true).getBoolean(true))
	    	 AeroSphere = new ItemAeroSphere();
	     
	     if (config.get("Feature Control", "enable_wand_upgrades", true).getBoolean(true))
	     {
	    	 ArcaneCrystal = new ItemArcaneCrystal();
	    	 EndstoneChunk = new ItemEndstoneChunk();
	     }
	     	
	     if (AdvThaum.config.get("Feature Control", "enable_engine", true).getBoolean(true))
	    	 AdvThaum.EssentiaEngine = new BlockEssentiaEngine( Material.rock);
		
	     Placeholder = new BlockPlaceholder(Material.air);
	  
	     // these must be done before proxy.register
		 new BCCompatChecker().register();
	
		 if (config.get("Feature Control", "force_enable_essentia_engine", false).getBoolean(false))
			 EnergyCompatBase.forceEnable();
	
	  
	     LanguageRegistry.instance().addStringLocalization("itemGroup.advthaum", "en_US", "Advanced Thaumaturgy");
	     LanguageRegistry.instance().addStringLocalization("tc.research_category.ADVTHAUM", "en_US", "Advanced Thaumaturgy");
	     
	     MinecraftForge.EVENT_BUS.register(new ATEventHandler());
	     
	     MinecraftForge.EVENT_BUS.register(new ArcingDamageManager());
	     
	     ForgeChunkManager.setForcedChunkLoadingCallback(instance, new ChunkLoadingClass());
	    
     }
	
	 private void registerStuff()
	 {
		if (EnergyCompatBase.isPresent())
			AdvThaum.EssentiaEngine.register();
			
		 if (InfusedThaumium != null)
			 InfusedThaumium.register();
		 
		 if (NodeModifier != null)
			 NodeModifier.register();
		 
		 if (ThaumicFertilizer != null)
			 ThaumicFertilizer.register();
		 
		 if (CreativeNode != null)
			 CreativeNode.register();
		 
		 if (EtherealJar != null && itemEtherealJar != null)
			 EtherealJar.register();
		 
		 if (Microlith != null)
			 Microlith.register();
		  
		 if (FocusVoidCage != null)
			 FocusVoidCage.register();
		 
		 if (AeroSphere != null)
			 AeroSphere.register();
		 
		 if (ArcaneCrystal != null)
			 ArcaneCrystal.register();
		 
		 if (EndstoneChunk != null)
			 EndstoneChunk.register();

		 if (AltarDeployer != null)
			 AltarDeployer.register();
		 
	 }
	 
	 public static void log(String text)
	 {
	     logger.info(FMLCommonHandler.instance().getEffectiveSide().toString() + " " + text);
	 }
	 
	 @EventHandler
     public void load(FMLInitializationEvent event) 
     {
		 
     }
    
	 @EventHandler  
     public void postInit(FMLPostInitializationEvent event) 
     {
		 
		 ResearchCategories.registerCategory("ADVTHAUM",
				 new ResourceLocation("thaumcraft", "textures/items/thaumonomiconcheat.png"),
				 new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png"));
		 	 

	    if (config.get("Feature Control", "enable_mercurial_core", true).getBoolean(true))
	     {
	    	int capacity = 500;
	    	for (WandRod rod : WandRod.rods.values())
	    		capacity = Math.max(capacity, rod.getCapacity());
		     
	    	 MercurialRodBase = new ItemMercurialRodBase();
	    	 MercurialRod = new ItemMercurialRod(capacity);
	    	 
	    	 if (config.get("Feature Control", "enable_mercurial_wand", true).getBoolean(true))
	    		 MercurialWand = new ItemMercurialWand();
	     }
		    
		 if (MercurialRodBase != null)
			 MercurialRodBase.register();
		
		 if (MercurialWand != null)
			 MercurialWand.register();

	     registerStuff();
	     proxy.register();
		 
		 //ThaumicInkwell.register();
		 //ThaumicVulcanizer.register();
		 
		 // enable activating node in a jar by wanding the top wood slabs
		 WandTriggerRegistry.registerWandBlockTrigger(Thaumcraft.proxy.wandManager, 4, Blocks.wooden_slab, -1);
		 //WandTriggerRegistry.registerWandBlockTrigger(Thaumcraft.proxy.wandManager, 5, Block.obsidian.blockID, -1);
		 
		 if (config.get("Feature Control", "add_permutatio_to_eggs", true).getBoolean(true))
		 {
			 AspectList list = ThaumcraftApiHelper.getObjectAspects(new ItemStack(Items.egg));
			 if (!list.aspects.containsKey(Aspect.EXCHANGE))
			 {
				list.add(Aspect.EXCHANGE, 1); 
				 ThaumcraftApi.registerObjectTag(new ItemStack(Items.egg), new int[]{}, list);
			 }
		 }
		 
		 if (config.get("Feature Control", "add_exanimus_to_bones", true).getBoolean(true))
		 {
			 AspectList list = ThaumcraftApiHelper.getObjectAspects(new ItemStack(Items.bone));
			 if (!list.aspects.containsKey(Aspect.UNDEAD))
			 {
				 list.add(Aspect.UNDEAD, 1);
				 ThaumcraftApi.registerObjectTag(new ItemStack(Items.bone), new int[]{}, list);
			 }
		 }
			 
		 config.save();
		 
		 LanguageRegistry.instance().addStringLocalization("tc.research_name.TESTBUILD", "en_US",  "Test Build Notes");
		 ResearchItem ri = new ResearchItem("TESTBUILD", "ADVTHAUM", new AspectList(), 0, -2, 0, new ItemStack(CreativeNode));
		 
		 ri.setAutoUnlock();
		 ri.setRound();
		 
		 ri.setPages(new ResearchPage("This build is for testing only.  You should NOT be using this on a live server / map.  Doing so will likely kill your world save.\nAny Research with an unset localized name (eg at.research.something.name) is likely something I haven't quite finished but it will be in the public release build.\n\n- Lycaon"));
		 
		 ri.registerResearchItem();
		 
     }
	 
	 @EventHandler
	 public void serverLoad(FMLServerStartingEvent event)
	 {
		 event.registerServerCommand(new ATServerCommand());
	 }
	 
	 @EventHandler
	 public void serverStarted(FMLServerStartingEvent event)
	 {
		 proxy.loadData();
	 }
	 
	 @EventHandler 
	 public void serverStopping(FMLServerStoppingEvent event)
	 {
		 proxy.saveData();	
	 }
}

