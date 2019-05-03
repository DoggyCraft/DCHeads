package dk.doggycraft.dcheads;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class HeadGUIManager implements Listener
{
	private Heads	plugin;
	private Inventory inv;
	
	HeadGUIManager(Heads p)
	{
		this.plugin = p;
	}
	
	public void load()
	{
		// Nothing to see here
	}
	
	public SortedMap<Integer, List<String>> newSortedMap(Integer from, Integer to, Map<Integer, List<String>> headMap) {
		// Convert to TreeMap
		TreeMap<Integer, List<String>> headTreeMap = new TreeMap<Integer, List<String>>(headMap);
		
		// Get the last other entries
		SortedMap<Integer, List<String>> headInventoryMap = headTreeMap.subMap(from, to);
		
		return headInventoryMap;
	}
	
	// Getting the next page, using current page, new search and yeaaaa
	public void nextPage(Integer currentPage, String searchKeyword, HumanEntity player)
	{
		plugin.logDebug("Nextpage... searching");
		Map<Integer, List<String>> headMap = plugin.getFreshCoalAPI().getHeadsOnSite(searchKeyword);
		plugin.logDebug("Search complete! HeadMap size: " + headMap.size());
		
		if ((headMap.size()-(45*currentPage)) <= 0) {
			plugin.logDebug("HeadMap size is 0 for current page, go back to 1");
			// Create inventory
			inv = createInventory(inv, 54, 1, newSortedMap(0, 45, headMap), searchKeyword);
		}
		else if (headMap.size() <= (45*(currentPage+1))) {
			plugin.logDebug("Rest of headmap can fit into a new page, create it");
			// Create inventory
			inv = createInventory(inv, 54, currentPage+1, newSortedMap((45*currentPage)+1, headMap.size(), headMap), searchKeyword);
		}
		else {
			plugin.logDebug("It can't fit, crop it and go to next page");
			// Create inventory
			inv = createInventory(inv, 54, currentPage+1, newSortedMap((45*currentPage)+1, (45*(currentPage+1)), headMap), searchKeyword);
		}
		player.closeInventory();
		player.openInventory(inv);
	}
	
	public void newHeadGUI(Player player, String searchWord) 
	{
		Map<Integer, List<String>> headMap = plugin.getFreshCoalAPI().getHeadsOnSite(searchWord);
		
		if (headMap == null) {
			player.sendMessage(ChatColor.RED + "Intet resultat!");
		}
		else if (headMap.isEmpty()) {
			player.sendMessage(ChatColor.RED + "Intet resultat!");
		}
		else {
			plugin.logDebug("HeadMap Size: " + headMap.size());
			// is headmap under, or equals to 54, if so, just create 1 page
			if (headMap.size() <= 54)
			{
				inv = createInventory(inv, 54, headMap, searchWord);
			}
			else {
				// Create inventory
				inv = createInventory(inv, 54, 1, newSortedMap(0, 45, headMap), searchWord);
			}
			player.openInventory(inv);
		}
	}
	
	public Inventory createInventory(Inventory inv, Integer slots, Integer page, Map<Integer, List<String>> headMap, String searchWord) {
		if (slots > 54) {
			plugin.logDebug("Over 54 slots defined...");
			return null;
		}
		if (headMap.size() > 45) {
			plugin.logDebug("Over 45 slots used in headsize...");
			return null;
		}
		inv = plugin.getServer().createInventory(null, slots, "DCHeads - Side nr. " + page + " - " + searchWord);
		// Reading the map
		plugin.logDebug("Trying to create items... HeadMap Size: " + headMap.size());
		
		Integer i = 0;
		for (Map.Entry<Integer, List<String>> entry : headMap.entrySet()) {
			List<String> head = entry.getValue();
			String headname = head.get(0);
			String headtexture = head.get(1);
			
			// Creating the items
			plugin.logDebug("Head added... HeadMap size: " + headMap.size() + ". Headname: " + headname);
			inv.setItem(i, plugin.getHeadsManager().getCustomHead(headname, headtexture));
			i++;
		}
		// Switching page head
		inv.setItem(53, plugin.getHeadsManager().getPlayerHead("Next Page", "MHF_ArrowRight"));
		
		// Return the inventory
		return inv;
	}
	
	public Inventory createInventory(Inventory inv, Integer slots, Map<Integer, List<String>> headMap, String searchWord) {
		if (slots > 54) {
			plugin.log("Over 54 slots defined...");
			return null;
		}
		if (headMap.size() > 54) {
			plugin.log("Over 54 slots used in headsize...");
			return null;
		}
		inv = plugin.getServer().createInventory(null, slots, "DCHeads - " + searchWord);
		// Reading the map
		for (int i = 0; i < headMap.size(); ++i) {
			List<String> head = headMap.get(i);
			String headname = head.get(0);
			String headtexture = head.get(1);
			
			// Creating the items
			inv.setItem(i, plugin.getHeadsManager().getCustomHead(headname, headtexture));
		}
		return inv;
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		String pageName = e.getInventory().getName();
		plugin.logDebug("Current inventory: " + pageName);
		if ((!e.getInventory().getName().contains("DCHeads - Side nr. "))) {
			return;
		}
		if (e.getCurrentItem() == null) {
			return;
		}
		else {
			if (e.getCurrentItem().getItemMeta() == null) {
				return;
			}
			else {
				if (e.getCurrentItem().getItemMeta().getDisplayName() == null) {
					return;
				}
			}
		}
		if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Next Page")) {
			String pageNumber = pageName.replaceFirst(".*?(\\d+).*", "$1");
			plugin.logDebug("Current pageNumber:" + pageNumber);
			Integer currentPage = Integer.parseInt(pageNumber);
			String searchWord = pageName.substring(pageName.lastIndexOf(" ")+1);
			plugin.logDebug("Current searchWord:" + searchWord);
			nextPage(currentPage, searchWord, e.getWhoClicked());
		}
		else {
			return;
		}
	}
}