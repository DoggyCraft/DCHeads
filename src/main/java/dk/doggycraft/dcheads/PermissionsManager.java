package dk.doggycraft.dcheads;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.permission.Permission;

public class PermissionsManager
{
	private String			pluginName			= "null";
	private Heads			plugin;
	private Permission 		vaultPermission;
	
	public PermissionsManager(Heads p)
	{
		this.plugin = p;
		
		if (p.vaultEnabled) {
			RegisteredServiceProvider<Permission> permissionProvider = plugin.getServer().getServicesManager().getRegistration(Permission.class);
			vaultPermission = permissionProvider.getProvider();
		}
	}

	public void load()
	{
		// Nothing to see here
	}

	public Plugin getPlugin()
	{
		return plugin;
	}

	public String getPermissionPluginName()
	{
		return pluginName;
	}

	public boolean hasPermission(Player player, String node)
	{
		if (this.plugin.vaultEnabled) {
			return vaultPermission.has(player, node);
		}
		return false;
	}

	public String getGroup(String playerName)
	{
		if (this.plugin.vaultEnabled) {
			return vaultPermission.getPrimaryGroup(plugin.getServer().getPlayer(playerName));
		}
		return "";
	}

	public void setGroup(String playerName, String groupName)
	{
		if (this.plugin.vaultEnabled) {
			Player player = plugin.getServer().getPlayer(playerName);
			vaultPermission.playerAddGroup(player, groupName);
		}
	}
}
