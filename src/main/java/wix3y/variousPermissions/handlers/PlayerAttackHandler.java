package wix3y.variousPermissions.handlers;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import wix3y.variousPermissions.VariousPermissions;

public class PlayerAttackHandler implements Listener {

    public PlayerAttackHandler(VariousPermissions plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Check if player has permission to use the specified melee weapon
     *
     * @param event the entity damage by entity event
     */
    @EventHandler
    public void onPlayerMeleeAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) {
            return;
        }

        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

        Material material = itemInMainHand.getType();
        if (!isMeleeWeapon(material)) {
            return;
        }

        // get the permission (with item model support) for the tool used
        String permission = "variouspermissions." + material.name().toLowerCase();
        if (itemInMainHand.hasItemMeta() && itemInMainHand.getItemMeta().hasItemModel()) {
            permission = "variouspermissions." + itemInMainHand.getItemMeta().getItemModel().getKey().toLowerCase();
        }

        // check if the player has permission to use the weapon
        if (player.hasPermission(permission)) {
            return;
        }

        event.setCancelled(true);
        player.sendMessage(MiniMessage.miniMessage().deserialize("<red>You do not have permission to use this weapon."));
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
    }

    /**
     * Check if player has permission to use the specified ranged weapon
     *
     * @param event the entity shoot bow event
     */
    @EventHandler
    public void onPlayerRangedAttack(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        ItemStack weapon = event.getBow();
        if (weapon == null) {
            return;
        }

        Material material = weapon.getType();
        // get the permission (with item model support) for the tool used
        String permission = "variouspermissions." + material.name().toLowerCase();
        if (weapon.hasItemMeta() && weapon.getItemMeta().hasItemModel()) {
            permission = "variouspermissions." + weapon.getItemMeta().getItemModel().getKey().toLowerCase();
        }

        // check if the player has permission to use the weapon
        if (player.hasPermission(permission)) {
            return;
        }

        event.setCancelled(true);
        player.sendMessage(MiniMessage.miniMessage().deserialize("<red>You do not have permission to use this weapon."));
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
    }

    /**
     * Check if player has permission to use a trident
     *
     * @param event the player launch projectile event
     */
    @EventHandler
    public void onPlayerRangedAttack(PlayerLaunchProjectileEvent event) {
        if (!(event.getProjectile().getType().equals(EntityType.TRIDENT))) {
            return;
        }

        // get the permission (with item model support) for the tool used
        String permission = "variouspermissions.trident";
        Player player = event.getPlayer();
        // check if the player has permission to use the trident
        if (player.hasPermission(permission)) {
            return;
        }

        event.setCancelled(true);
        player.sendMessage(MiniMessage.miniMessage().deserialize("<red>You do not have permission to use this weapon."));
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
    }

    private boolean isMeleeWeapon(Material material) {
        return material.name().endsWith("_PICKAXE") ||
                material.name().endsWith("_AXE") ||
                material.name().endsWith("_SHOVEL") ||
                material.name().endsWith("_HOE") ||
                material.name().endsWith("_SWORD") ||
                material == Material.TRIDENT;
    }
}