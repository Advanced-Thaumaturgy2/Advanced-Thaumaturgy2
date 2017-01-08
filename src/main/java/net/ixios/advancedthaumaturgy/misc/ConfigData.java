package net.ixios.advancedthaumaturgy.misc;

import net.minecraftforge.common.config.Configuration;

/**
 * Created by katsw on 17/11/2016.
 */
public class ConfigData {


    public static boolean useClassicTooltip;
    public static boolean enableAltarDeployer;
    public static boolean enableInfusedThaumium;
    public static boolean enableNodeModifier;
    public static boolean enableThaumicFertilizer;
    public static boolean enableCreativeNode;
    public static boolean enableThaumicVulcanizer;
    public static boolean enableEtherealJar;
    public static boolean enableMicrolith;
    public static boolean enableVoidCage;
    public static boolean enableAeroSphere;
    public static boolean enableWandUpgrades;
    public static boolean enableEssentiaEngine;
    public static boolean enableMercurialCore;
    public static boolean enableMercurialWand;
    public static boolean enableWandbench;
    public static boolean addExchangeToEggs;
    public static boolean addUndeadToBones;

    public static void loadConfig(Configuration config)
    {
        config.load();
        useClassicTooltip=config.get("Feature Control", "classic_wand_tooltip", false,"Use the classic tooltip").getBoolean(false);
        enableAltarDeployer=config.get("Feature Control", "enable_altar_deployer", true,"Allow the creative mode altar deployer").getBoolean(true);
        //enableInfusedThaumium=config.get("Feature Control", "enable_infused_thaumium", true,"Allows infused Thaumium").getBoolean(true);
        enableInfusedThaumium = false;
        enableNodeModifier=config.get("Feature Control", "enable_node_modifier", true,"Allows the Node modifier block").getBoolean(true);
        enableThaumicFertilizer=config.get("Feature Control", "enable_fertilizer", true, "Allows the Thaumic Fertilizer block").getBoolean(true);
        enableCreativeNode=config.get("Feature Control", "enable_creative_node", true, "Allows use of the creative node in creative mode").getBoolean(true);
        //enableThaumicVulcanizer=config.get("Feature Control", "enable_vulcanizer", true,"Allows use of the Thaumic Vulcanizer").getBoolean(true);
        enableThaumicVulcanizer = false;
        enableEtherealJar=config.get("Feature Control", "enable_ethereal_jar", true, "Allows use of the ethereal jar").getBoolean(true);
        enableMicrolith=config.get("Feature Control", "enable_minilith", true, "Allows use of the miniliths").getBoolean(true);
        enableVoidCage=config.get("Feature Control", "enable_focus_void_cage", true, "Allows use of the Void Cage focus").getBoolean(true);
        //enableAeroSphere=config.get("Feature Control", "enable_aerosphere", true, "Allows use of the Aerosphere").getBoolean(true);
        enableAeroSphere = false;
        enableWandUpgrades=config.get("Feature Control", "enable_wand_upgrades", true, "Allows upgrading wands").getBoolean(true);
        enableEssentiaEngine= config.get("Feature Control", "enable_engine", true, "Allows use of the essentia engine to convert essentia to RF").getBoolean(true);
        enableMercurialCore=config.get("Feature Control", "enable_mercurial_core", true, "Allows use of the mercurial core").getBoolean(true);
        enableMercurialWand=config.get("Feature Control", "enable_mercurial_wand", true, "Allows use of the mercurial wand").getBoolean(true);
        enableWandbench = config.get("Feature Control", "enable_wandbench", true, "Allows use of the wandbench").getBoolean(true);
        
        addExchangeToEggs=config.get("Feature Control", "add_permutatio_to_eggs", true, "Adds Permutatio to eggs if not there already").getBoolean(true);
        addUndeadToBones=config.get("Feature Control", "add_exanimus_to_bones", true, "Adds Exanimus to bones if not there already").getBoolean(true);
    }
}
