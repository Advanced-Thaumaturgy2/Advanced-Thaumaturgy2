package net.ixios.advancedthaumaturgy.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategoryList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigResearch;
import thaumcraft.common.lib.research.ResearchManager;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.tileentities.TileNodeModifier.Operation;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

public class ATServerCommand implements ICommand
{
	
	@Override
	public int compareTo(Object arg0)
	{
		return 0;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] list)
	{
		ArrayList<String> options = new ArrayList<String>();
		options.add("research");
		return options;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		return ((EntityPlayer)sender).capabilities.isCreativeMode || Utilities.isOp(sender.getCommandSenderName());
	}

	@Override
	public List getCommandAliases()
	{
		return null;
	}

	@Override
	public String getCommandName()
	{
		return "at";
	}

	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return "/at <option> <parameters>";
	}

	@Override
	public boolean isUsernameIndex(String[] arg0, int arg1)
	{
		return false;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] params)
	{
		if (!(sender instanceof EntityPlayer))
		{
			sender.addChatMessage(new ChatComponentTranslation("command.unavailable"));
			return;
		}
		
		EntityPlayer player = (EntityPlayer)sender;
		
		String cmd = params[0].toLowerCase();
		
		if (cmd.equals("debug"))
		{
			AdvThaum.debug = !AdvThaum.debug;
			player.addChatMessage(new ChatComponentTranslation("command.debug", AdvThaum.debug));
		}
		else if (cmd.equals("test"))
		{
			ResearchItem ri = Utilities.findResearch("ESSENTIAENGINE");
			if (ri != null)
				player.addChatMessage(new ChatComponentText(ri.displayColumn + ":" + ri.displayRow));
		}
		else if (cmd.equals("research"))
		{
			if (params.length < 2)
			{
				showHelp(player);
				return;
			}

			String option = params[1].toLowerCase();
			String which = params[2].toLowerCase();
			
			if (option.equals("add"))
			{
				for (String categorykey : ResearchCategories.researchCategories.keySet())
                {
                    ResearchCategoryList cat = ResearchCategories.researchCategories.get(categorykey);
                    player.addChatMessage(new ChatComponentTranslation("command.searching" , categorykey));
                    
                    for (ResearchItem ri : cat.research.values())
                    {       
                    	if (!ri.key.equalsIgnoreCase(which))
                    		continue;
                    	
	                    if(!ResearchManager.isResearchComplete(player.getDisplayName(), ri.key))
	                    {
	                        Thaumcraft.proxy.getResearchManager().completeResearch(player, ri.key);
	                        player.addChatMessage(new ChatComponentTranslation("command.addresearch",ri.getName()));
	                    }
	                    else
	                        player.addChatMessage(new ChatComponentTranslation("command.researchcomplete",ri.key));
                    }
                }
	                
			}
			else if (option.equals("remove"))
			{
				if (Utilities.removeResearch(player, which))
					player.addChatMessage(new ChatComponentTranslation("command.researchremoved"));
				else
					player.addChatMessage(new ChatComponentTranslation("command.researchnotfound", which ));
			}
			else
			{
				showHelp(player);
			}
		}
	}

	private static void showHelp(ICommandSender sender)
	{
		sender.addChatMessage(new ChatComponentTranslation("command.help"));
	}
}
