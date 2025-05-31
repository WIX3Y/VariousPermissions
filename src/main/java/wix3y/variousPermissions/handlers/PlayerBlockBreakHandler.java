package wix3y.variousPermissions.handlers;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import wix3y.variousPermissions.VariousPermissions;

public class PlayerBlockBreakHandler implements Listener {

    public PlayerBlockBreakHandler(VariousPermissions plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Check if player has permission to break blocks with the specified tool
     *
     * @param event the player block break event
     */
    @EventHandler
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

    private boolean isTool(Material material) {
        return material.name().endsWith("_PICKAXE") ||
                material.name().endsWith("_AXE") ||
                material.name().endsWith("_SHOVEL") ||
                material.name().endsWith("_HOE") ||
                material.name().endsWith("_SWORD");
    }
}