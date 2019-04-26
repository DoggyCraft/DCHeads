package main.java.dk.doggycraft.dcheads;

import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands
{
	private Heads	plugin	= null;

	Commands(Heads p)
	{
		this.plugin = p;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		Player player = null;
		plugin.logDebug("we're in Commands");
		plugin.logDebug("cmd.getname is: " + cmd.getName());

		if ((sender instanceof Player))
		{
			player = (Player) sender;
		}

		if (player == null)
		{
			plugin.logDebug("player is null");
			if ((cmd.getName().equalsIgnoreCase("heads")) || (cmd.getName().equalsIgnoreCase("gethead")))
			{
				if (args.length == 1)
				{
					if(args[0].equalsIgnoreCase("reload"))
					{
						plugin.reloadSettings();
						this.plugin.log(this.plugin.getDescription().getFullName() + ": Genindlæst konfiguration.");

						return true;
					}
				}
			}

			return true;
		}
		plugin.logDebug("not a console");
		if ((cmd.getName().equalsIgnoreCase("heads")) || (cmd.getName().equalsIgnoreCase("gethead")))
		{
			plugin.logDebug("ok, so one of the commands were heads or gethead");
			plugin.logDebug("args length is: " + args.length);
			if (args.length == 0)
			{
				plugin.logDebug("no args");
				commandHelp(sender);
				return true;
			}
			if (args.length == 1)
			{
				if (args[0].equalsIgnoreCase("reload"))
				{
					if ((!player.isOp()) && (!player.hasPermission("heads.reload")))
					{
						return false;
					}

					this.plugin.reloadSettings();
					sender.sendMessage(ChatColor.YELLOW + this.plugin.getDescription().getFullName() + ":" + ChatColor.AQUA + " Genindlæst konfiguration.");
					return true;
				}
				if (args[0].equalsIgnoreCase("help"))
				{
					if ((!player.isOp()) && (!player.hasPermission("heads.list")))
					{
						return false;
					}

					commandList(sender);

					return true;
				}
			}
			if (args.length == 2)
			{
				if (args[0].equalsIgnoreCase("code"))
				{
					if ((!player.isOp()) && (!player.hasPermission("heads.gethead")))
					{
						return false;
					}

					commandHead(sender, args[1]);
					return true;
				}
				if (args[0].equalsIgnoreCase("search"))
				{
					if ((!player.isOp()) && (!player.hasPermission("heads.gethead")))
					{
						return false;
					}

					commandSearchHead(sender, args[1]);
					return true;
				}
			}
			else
			{

				if (args.length > 3)
				{
					sender.sendMessage(ChatColor.RED + "For mange argumenter! Tjek /heads help");
					return true;
				}
			}
		}
		return true;
	}

	private boolean commandHelp(CommandSender sender)
	{
		sender.sendMessage(ChatColor.YELLOW + "---------------- " + plugin.getDescription().getFullName() + " ----------------");
		sender.sendMessage(ChatColor.AQUA + "Lavet af DoggyCraft");
		sender.sendMessage(ChatColor.AQUA + "");
		sender.sendMessage(ChatColor.AQUA + "Brug " + ChatColor.WHITE + "/heads help" + ChatColor.AQUA + ", for at få en liste over kommandoer");

		return true;
	}

	private boolean commandList(CommandSender sender)
	{
		sender.sendMessage(ChatColor.YELLOW + "---------------- " + this.plugin.getDescription().getFullName() + " ----------------");
		sender.sendMessage(ChatColor.AQUA + "/heads" + ChatColor.WHITE + " - Info om pluginnet");
		if ((sender.isOp()) || (sender.hasPermission("heads.gethead")))
		{
			sender.sendMessage(ChatColor.AQUA + "/heads code <base64 værdi på hoved>" + ChatColor.WHITE + " - Får et hoved ved at bruge dets base64 værdi");
			sender.sendMessage(ChatColor.AQUA + "/heads search <søgeord>" + ChatColor.WHITE + " - Får en liste af hoveder ved at søge");
		}
		if ((sender.isOp()) || (sender.hasPermission("heads.reload")))
		{
			sender.sendMessage(ChatColor.AQUA + "/heads reload" + ChatColor.WHITE + " - Genindlæser DCHeads systemet");
		}

		return true;
	}
	
	private boolean commandHead(CommandSender sender, String headBase64)
	{
		Player player = (Player)sender;
		
		if ((sender.isOp()) || (sender.hasPermission("heads.gethead")))
		{
			plugin.getHeadsManager().giveHeadToPlayer(player, headBase64);
			sender.sendMessage(ChatColor.BLUE + "Her er dit hoved!");
		}

		return true;
	}

	private boolean commandSearchHead(CommandSender sender, String searchKeyword)
	{
		Player player = (Player)sender;
		
		if ((sender.isOp()) || (sender.hasPermission("heads.gethead")))
		{
			// Get the headmap with search keyword
			if (searchKeyword.length() < 3)
			{
				sender.sendMessage(ChatColor.RED + "Brug venligst 3 eller flere bogstaver i din søgning...");
			}
			//sender.sendMessage(ChatColor.BLUE + "Finder hoveder med dit søgeord...");
			Map<Integer, List<String>> headMap = plugin.getFreshCoalAPI().getHeadsOnSite(searchKeyword);
			
			plugin.getHeadGUIManager().newHeadGUI(headMap, searchKeyword, player);
			//sender.sendMessage(ChatColor.BLUE + "Her er dine hoveder!");
		}

		return true;
	}
}
