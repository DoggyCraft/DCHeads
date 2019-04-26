package main.java.dk.doggycraft.dcheads;

import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class HeadGUIManager
{
	private Heads	plugin;
	
	HeadGUIManager(Heads p)
	{
		this.plugin = p;
	}
	
	public void load()
	{
		// Nothing to see here
	}
	
	public void newHeadGUI(Map<Integer, List<String>> headMap, String searchKeyword, Player player) 
	{
		// Tried to make sure inventory size was just about a little over the amount of heads, and divisible by 9... did not work. Could be fixed later and added more pages
		/*int headMapSize = 0;
		int itemsPerRow = 9;
		int inventorySize = 0;
		
		plugin.logDebug("Testing inventory size... Size of headMap: " + headMap.size());
		for (headMapSize = headMap.size(); (headMapSize/itemsPerRow)%1 < 0; headMapSize++)
		{
			inventorySize = headMapSize;
		}
		plugin.logDebug("Done testing inventory size, current: " + Integer.toString(inventorySize));*/
		
		Inventory inv = plugin.getServer().createInventory(null, 54, "Søgning på: " + searchKeyword);
		
		// Reading the map
		for (int i = 0; i < headMap.size(); ++i) {
			List<String> head = headMap.get(i);
			String headname = head.get(0);
			String headtexture = head.get(1);
			
			inv.setItem(i, plugin.getHeadsManager().getCustomHeadWithName(headname, headtexture));
		}
		
		player.openInventory(inv);
	}

}