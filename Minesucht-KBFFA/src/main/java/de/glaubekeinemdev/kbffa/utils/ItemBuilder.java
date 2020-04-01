package de.glaubekeinemdev.kbffa.utils;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Lukas Münch (GlaubeKeinemDev) on Mär 2020
 */
public class ItemBuilder {

    private ItemStack stack;

    public ItemBuilder( Material material, int anzahl, int subid, String displayname) {
        short neuesubid = (short)subid;
        stack = new ItemStack(material, anzahl, neuesubid);
        ItemMeta m = stack.getItemMeta();
        m.setDisplayName(displayname);
        stack.setItemMeta(m);
    }

    public ItemBuilder(String skullowner, int anzahl, String displayname) {
        short neuesubid = (short)3;
        stack = new ItemStack( Material.SKULL_ITEM, anzahl, neuesubid);
        SkullMeta m = ( SkullMeta ) stack.getItemMeta();
        m.setDisplayName(displayname);
        m.setOwner(skullowner);
        stack.setItemMeta(m);
    }


    public ItemBuilder addUnsafeEnchantment( Enchantment enchantment, int level) {
        this.stack.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder addEnchantments( Map<Enchantment, Integer> enchants) {
        enchants.forEach( ( enchantment, integer ) -> this.stack.addUnsafeEnchantment(enchantment, integer) );
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        this.stack.addEnchantment(enchantment, level);
        return this;
    }


    public static ItemStack createItem( Material material, int anzahl, int subid, String displayname)
    {
        short neuesubid = (short)subid;
        ItemStack i = new ItemStack(material, anzahl, neuesubid);
        ItemMeta m = i.getItemMeta();
        m.setDisplayName(displayname);
        i.setItemMeta(m);

        return i;
    }

    public ItemBuilder addColor( Color color){
        LeatherArmorMeta itemMeta = (LeatherArmorMeta)stack.getItemMeta();
        itemMeta.setColor(color);
        stack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder addLore(String lore) {
        ItemMeta meta = this.stack.getItemMeta();
        if (!meta.hasLore()) {
            List<String> newLore = new ArrayList<>();
            newLore.add(lore);
            meta.setLore(newLore);
        } else {
            List<String> itemLore = meta.getLore();
            itemLore.add(lore);
            meta.setLore(itemLore);
        }
        this.stack.setItemMeta(meta);
        return this;
    }

    public ItemStack create() {
        return this.stack;
    }

}
