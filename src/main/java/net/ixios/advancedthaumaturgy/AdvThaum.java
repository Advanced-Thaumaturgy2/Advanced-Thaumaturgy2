package net.ixios.advancedthaumaturgy;

import cpw.mods.fml.common.*;
import net.ixios.advancedthaumaturgy.blocks.*;
import net.ixios.advancedthaumaturgy.compat.computercraft.ComputerCraft;
import net.ixios.advancedthaumaturgy.misc.*;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import net.ixios.advancedthaumaturgy.items.ItemAeroSphere;
import net.ixios.advancedthaumaturgy.items.ItemArcaneCrystal;
import net.ixios.advancedthaumaturgy.items.ItemEndstoneChunk;
import net.ixios.advancedthaumaturgy.items.ItemEtherealJar;
import net.ixios.advancedthaumaturgy.items.ItemFocusVoidCage;
import net.ixios.advancedthaumaturgy.items.ItemInfusedThaumium;
import net.ixios.advancedthaumaturgy.items.ItemMercurialRod;
import net.ixios.advancedthaumaturgy.items.ItemMercurialRodBase;
import net.ixios.advancedthaumaturgy.items.ItemMercurialWand;
import net.ixios.advancedthaumaturgy.network.PacketStartNodeModification;
import net.ixios.advancedthaumaturgy.proxies.CommonProxy;
import net.ixios.advancedthaumaturgy.tileentities.TileEssentiaEngine;
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
	dependencies="required-after:Thaumcraft;after:ThaumicHorizons;after:ThaumicExploration;after:thaumicbases;after:ForbiddenMagic;after:ThaumicTinkerer;after:ComputerCraft;after:OpenComputers@[1.2.0,)",
	acceptedMinecraftVersions="1.7.10")
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
	public static BlockCrystalHolder CrystalHolder;
	public static BlockMicrolithMultiblock MicrolithMultiBlock;
	public static BlockMicrolithModelMultiblock MicrolithModelMultiBlock;
	public static BlockWandbench Wandbench;

	public static SimpleNetworkWrapper channel;
	
	public static Logger logger;
	
	public static boolean debug = false;
	
	 @EventHandler
     public void preInit(FMLPreInitializationEvent event)
	 {
	     logger=event.getModLog();

	     NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);
	     channel = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
	     channel.registerMessage(PacketStartNodeModification.Handler.class, PacketStartNodeModification.class, 1, Side.SERVER);

	     config = new Configuration(event.getSuggestedConfigurationFile());
		 ConfigData.loadConfig(config);

	     

	     
	     ////////////////////////////////////////////////////////
	 	     
	     if (ConfigData.enableAltarDeployer)
	    	 AltarDeployer = new BlockAltarDeployer();
	     
	     if (ConfigData.enableInfusedThaumium)
	    	 InfusedThaumium = new ItemInfusedThaumium();
	     
	     if (ConfigData.enableNodeModifier)
	    	 NodeModifier = new BlockNodeModifier(Material.ground);
	     
	     if (ConfigData.enableThaumicFertilizer)
	    	 ThaumicFertilizer = new BlockThaumicFertilizer(Material.ground);
	     
	     if (ConfigData.enableCreativeNode)
	    	 CreativeNode = new BlockCreativeNode();
	     
	     if (ConfigData.enableThaumicVulcanizer)
	    	 ThaumicVulcanizer = new BlockThaumicVulcanizer( Material.ground);
	     
	     if (ConfigData.enableEtherealJar)
	     {
	    	 EtherealJar = new BlockEtherealJar();
	    	 itemEtherealJar = new ItemEtherealJar();
	     }

	     if (ConfigData.enableMicrolith)
		 {
			 Microlith = new BlockMicrolith(Material.ground);
			 CrystalHolder=new BlockCrystalHolder();
			 MicrolithMultiBlock=new BlockMicrolithMultiblock();
			 MicrolithModelMultiBlock=new BlockMicrolithModelMultiblock();
		 }
	      
	     if (ConfigData.enableVoidCage)
	    	 FocusVoidCage = new ItemFocusVoidCage();
	     
	     if (ConfigData.enableAeroSphere)
	    	 AeroSphere = new ItemAeroSphere();
	     
	     if (ConfigData.enableWandUpgrades)
	     {
	    	 ArcaneCrystal = new ItemArcaneCrystal();
	    	 EndstoneChunk = new ItemEndstoneChunk();
	     }
		
	     Placeholder = new BlockPlaceholder(Material.air);
		 
	     if (ConfigData.enableEssentiaEngine)
	     {
	    	 AdvThaum.EssentiaEngine = new BlockEssentiaEngine( Material.rock);
	    	 TileEssentiaEngine.loadConfig();
	     }
	
	     if (ConfigData.enableWandbench)
	    	 Wandbench = new BlockWandbench();
	  
	     LanguageRegistry.instance().addStringLocalization("itemGroup.advthaum", "en_US", "Advanced Thaumaturgy");
	     LanguageRegistry.instance().addStringLocalization("tc.research_category.ADVTHAUM", "en_US", "Advanced Thaumaturgy");
	     
	     MinecraftForge.EVENT_BUS.register(new ATEventHandler());
	     
	     MinecraftForge.EVENT_BUS.register(new ArcingDamageManager());
	     
	     ForgeChunkManager.setForcedChunkLoadingCallback(instance, new ChunkLoadingClass());
	    
     }
	
	 private void registerStuff()
	 {
		if (AdvThaum.EssentiaEngine != null)
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

		 if(CrystalHolder!=null)
		 	CrystalHolder.register();

		 if(MicrolithMultiBlock!=null)
		 	MicrolithMultiBlock.register();
		 if(MicrolithModelMultiBlock!=null)
		 	MicrolithModelMultiBlock.register();
		 
		 if (Wandbench != null)
			 Wandbench.register();
	 }
	 
	 public static void log(String text)
	 {
	     logger.info(FMLCommonHandler.instance().getEffectiveSide().toString() + " " + text);
	 }
	 
	 @EventHandler
     public void load(FMLInitializationEvent event) 
     {
		if(Loader.isModLoaded("ComputerCraft"))
		{
			log("Loading Computer craft integration");
			ComputerCraft.InitCC();
		}
     }


    
	 @EventHandler  
     public void postInit(FMLPostInitializationEvent event) 
     {
		 
		 ResearchCategories.registerCategory("ADVTHAUM",
				 new ResourceLocation("thaumcraft", "textures/items/thaumonomiconcheat.png"),
				 new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png"));
		 	 

	    if (ConfigData.enableMercurialCore)
	     {
	    	int capacity = 500;
	    	for (WandRod rod : WandRod.rods.values())
	    		capacity = Math.max(capacity, rod.getCapacity());
		     
	    	 MercurialRodBase = new ItemMercurialRodBase();
	    	 MercurialRod = new ItemMercurialRod(capacity);
	    	 
	    	 if (ConfigData.enableMercurialWand)
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
		 
		 if (ConfigData.addExchangeToEggs)
		 {
			 AspectList list = ThaumcraftApiHelper.getObjectAspects(new ItemStack(Items.egg));
			 if (!list.aspects.containsKey(Aspect.EXCHANGE))
			 {
				list.add(Aspect.EXCHANGE, 1); 
				 ThaumcraftApi.registerObjectTag(new ItemStack(Items.egg), new int[]{}, list);
			 }
		 }
		 
		 if (ConfigData.addUndeadToBones)
		 {
			 AspectList list = ThaumcraftApiHelper.getObjectAspects(new ItemStack(Items.bone));
			 if (!list.aspects.containsKey(Aspect.UNDEAD))
			 {
				 list.add(Aspect.UNDEAD, 1);
				 ThaumcraftApi.registerObjectTag(new ItemStack(Items.bone), new int[]{}, list);
			 }
		 }
		 
		 LanguageRegistry.instance().addStringLocalization("tc.research_name.TESTBUILD", "en_US",  "Test Build Notes");
		 ResearchItem ri = new ResearchItem("TESTBUILD", "ADVTHAUM", new AspectList(), 0, -2, 0, new ItemStack(CreativeNode));
		 
		 ri.setAutoUnlock();
		 ri.setRound();
		 
		 ri.setPages(new ResearchPage("This build is for testing only.  You should NOT be using this on a live server / map.  Doing so will likely kill your world save.\nAny Research with an unset localized name (eg at.research.something.name) is likely something I haven't quite finished but it will be in the public release build.\n\n- Lycaon"));
		 
		 ri.registerResearchItem();
		 
	     config.save();
		 
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

