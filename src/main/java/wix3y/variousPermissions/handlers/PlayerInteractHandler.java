package wix3y.variousPermissions.handlers;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import wix3y.variousPermissions.VariousPermissions;

public class PlayerInteractHandler implements Listener {

    public PlayerInteractHandler(VariousPermissions plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Check if player has permission to create stripped log/wood, farmland or path blocks using specified tool
     *
     * @param event the player interact event
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

        String materialName = itemInMainHand.getType().name();
        Material blockMaterial = event.getClickedBlock().getType();
        if (materialName.endsWith("_AXE")) {
            if (blockMaterial.name().startsWith("STRIPPED_") || !(blockMaterial.name().endsWith("_LOG") || blockMaterial.name().endsWith("_WOOD"))) {
                return;
            }
        }
        else if (materialName.endsWith("_SHOVEL")) {
            if (!(blockMaterial == Material.DIRT || blockMaterial == Material.GRASS_BLOCK || blockMaterial == Material.ROOTED_DIRT || blockMaterial == Material.COARSE_DIRT || blockMaterial == Material.MYCELIUM || blockMaterial == Material.PODZOL)) {
                return;
            }
        }
        else if (materialName.endsWith("_HOE")) {
            if (!(blockMaterial == Material.DIRT || blockMaterial == Material.GRASS_BLOCK || blockMaterial == Material.ROOTED_DIRT || blockMaterial == Material.COARSE_DIRT || blockMaterial == Material.DIRT_PATH)) {
                return;
            }
        }
        else if (materialName.equals("BRUSH")) {
            if (!(blockMaterial == Material.SUSPICIOUS_GRAVEL || blockMaterial == Material.SUSPICIOUS_SAND)) {
                return;
            }
        }
        else {
            return;
        }

        // get the permission (with item model support) for the tool used
        String permission = "variouspermissions." + materialName.toLowerCase();
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
}