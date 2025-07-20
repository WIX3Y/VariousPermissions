package wix3y.variousPermissions.handlers;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import wix3y.variousPermissions.VariousPermissions;

import java.util.Arrays;
import java.util.List;

public class PlayerBlockBreakHandler implements Listener {

    public PlayerBlockBreakHandler(VariousPermissions plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Check if player has permission to break blocks with the specified tool
     *
     * @param event the player block break event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

        Material material = itemInMainHand.getType();
        if (!isTool(material)) {
            return;
        }

        // get the permission (with item model support) for the tool used
        String permission = "variouspermissions." + material.name().toLowerCase();
        if (itemInMainHand.hasItemMeta() && itemInMainHand.getItemMeta().hasItemModel()) {
            permission = "variouspermissions." + itemInMainHand.getItemMeta().getItemModel().getKey().toLowerCase();
        }

        // check if the player has permission to use the tool
        if (player.hasPermission(permission)) {
            return;
        }

        event.setCancelled(true);
        player.sendMessage(MiniMessage.miniMessage().deserialize("<red>You do not have permission to use this tool."));
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
    }

    /**
     * Check if player mined a custom ore with a tool of to low tier
     *
     * @param event the player block break event
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerBlockMine(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        Material blockMaterial = event.getBlock().getType();
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

        Material material = itemInMainHand.getType();
        if (!isTool(material)) {
            return;
        }

        // get the tool material (with item model support) for the tool used
        String toolMaterial = material.name().toLowerCase();
        if (!toolMaterial.endsWith("pickaxe")) {
            // non-pickaxes do not yield drops
            return;
        }
        if (itemInMainHand.hasItemMeta() && itemInMainHand.getItemMeta().hasItemModel()) {
            toolMaterial = itemInMainHand.getItemMeta().getItemModel().getKey().toLowerCase();
        }

        if (highEnoughToolTier(blockMaterial, toolMaterial)) {
            return;
        }

        event.setDropItems(false);
    }

    private boolean highEnoughToolTier(Material block, String tool) {
        // tools with to low tier for the block
        List<String> EmeraldTools = Arrays.asList("wooden_pickaxe", "stone_pickaxe", "copper_pickaxe", "iron_pickaxe", "golden_pickaxe", "reinforced_gold_pickaxe", "diamond_pickaxe", "netherite_pickaxe", "platinum_pickaxe");
        List<String> CoalTools = Arrays.asList("wooden_pickaxe", "stone_pickaxe", "copper_pickaxe", "iron_pickaxe", "golden_pickaxe", "reinforced_gold_pickaxe", "diamond_pickaxe", "netherite_pickaxe", "crystal_pickaxe");
        List<String> GoldTools = Arrays.asList("wooden_pickaxe", "stone_pickaxe", "copper_pickaxe", "iron_pickaxe", "golden_pickaxe", "reinforced_gold_pickaxe", "diamond_pickaxe", "netherite_pickaxe");
        List<String> LapisTools = Arrays.asList("wooden_pickaxe", "stone_pickaxe", "copper_pickaxe", "iron_pickaxe", "golden_pickaxe", "reinforced_gold_pickaxe", "diamond_pickaxe");

        if (block.equals(Material.EMERALD_ORE)) {
            return !EmeraldTools.contains(tool);
        }
        else if (block.equals(Material.DEEPSLATE_COAL_ORE)) {
            return !CoalTools.contains(tool);

        }
        else if (block.equals(Material.NETHER_GOLD_ORE)) {
            return !GoldTools.contains(tool);

        }
        else if (block.equals(Material.LAPIS_ORE)) {
            return !LapisTools.contains(tool);

        }
        return true;
    }

    private boolean isTool(Material material) {
        return material.name().endsWith("_PICKAXE") ||
                material.name().endsWith("_AXE") ||
                material.name().endsWith("_SHOVEL") ||
                material.name().endsWith("_HOE") ||
                material.name().endsWith("_SWORD");
    }
}