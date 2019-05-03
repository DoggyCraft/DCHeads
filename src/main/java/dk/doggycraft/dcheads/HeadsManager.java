package dk.doggycraft.dcheads;

import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

public class HeadsManager
{
	private Heads	plugin;
	
	HeadsManager(Heads p)
	{
		this.plugin = p;
	}
	
	public void load()
	{
		// Nothing to see here
	}
	
	public ItemStack getCustomHead(byte[] headBase64)
	{
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        PropertyMap propertyMap = profile.getProperties();
        if (propertyMap == null) {
            throw new IllegalStateException("Profile doesn't contain a property map");
        }
        plugin.logDebug("Generating Custom Head - Current base64 is: " + headBase64);
        byte[] encodedData = headBase64;
        propertyMap.put("textures", new Property("textures", new String(encodedData)));
        ItemStack head = new ItemStack(Material.LEGACY_SKULL_ITEM, 1, (short) 3);
        ItemMeta headMeta = head.getItemMeta();
        Class<?> headMetaClass = headMeta.getClass();
        Reflections.getField(headMetaClass, "profile", GameProfile.class).set(headMeta, profile);
        head.setItemMeta(headMeta);

		return head;
	}
	
	public ItemStack getCustomHead(String name, String texture)
	{
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        PropertyMap propertyMap = profile.getProperties();
        if (propertyMap == null) {
            throw new IllegalStateException("Profile doesn't contain a property map");
        }
        plugin.logDebug("Generating Custom Head - Current texture is: " + texture + " Current headname is: " + name);
        propertyMap.put("textures", new Property("textures", texture));
        ItemStack head = new ItemStack(Material.LEGACY_SKULL_ITEM, 1, (short) 3);
        ItemMeta headMeta = head.getItemMeta();
        Class<?> headMetaClass = headMeta.getClass();
        Reflections.getField(headMetaClass, "profile", GameProfile.class).set(headMeta, profile);
        headMeta.setDisplayName(name);
        head.setItemMeta(headMeta);

		return head;
	}
	
	public ItemStack getPlayerHead(String name, String username)
	{
        plugin.logDebug("Generating PlayerHead - Current headname is: " + username);
        ItemStack head = new ItemStack(Material.LEGACY_SKULL_ITEM, 1, (short) 3);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        skullMeta.setOwner(username);
        head.setItemMeta(skullMeta);
        ItemMeta headMeta = head.getItemMeta();
        headMeta.setDisplayName(name);
        head.setItemMeta(headMeta);

		return head;
	}
	
	public boolean giveHeadToPlayer(Player player, String head)
	{
		byte[] headTexture = head.getBytes();
		player.getInventory().addItem(getCustomHead(headTexture));
		return true;
	}
	
	public boolean giveHeadToPlayer(Player player, ItemStack head)
	{
		player.getInventory().addItem(head);
		return true;
	}

}
