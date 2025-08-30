package wix3y.variousPermissions.handlers;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import wix3y.variousPermissions.VariousPermissions;

public class PlayerFishHandler implements Listener {

    public PlayerFishHandler(VariousPermissions plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Check if player has permission to use a fishing rod
     *
     * @param event the player interact event
     */
    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

        String materialName = itemInMainHand.getType().name();

        // get the permission (WITHOUT item model support) for the tool used (should always be fishing rod)
        String permission = "variouspermissions." + materialName.toLowerCase();

        // check if the player has permission to use the tool
        if (player.hasPermission(permission)) {
            return;
        }

        event.setCancelled(true);
        player.sendMessage(MiniMessage.miniMessage().deserialize("<red>You do not have permission to use this tool."));
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
    }
}