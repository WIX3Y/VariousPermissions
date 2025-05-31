package wix3y.variousPermissions.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Beehive;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import wix3y.variousPermissions.VariousPermissions;

import java.util.Random;

public class PlayerBeeHarvestHandler implements Listener {

    public PlayerBeeHarvestHandler(VariousPermissions plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Check if bee anger should be prevented for a player that harvests a bee nest or beehive
     *
     * @param event the player interact event
     */
    @EventHandler
    public void onPlayerBeeInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getClickedBlock() == null) {
            return;
        }

        // check that the player has permission to bypass bee anger
        Player player = event.getPlayer();
        if (!player.hasPermission("variouspermissions.nobeeanger")) {
            return;
        }

        // Check that clicked block is a bee nest or beehive
        Block block = event.getClickedBlock();
        Material material = block.getType();
        if (!(material == Material.BEE_NEST || material == Material.BEEHIVE)) {
            return;
        }

        // check that the block was clicked with shears or a glass bottle
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        Material itemInHandMaterial = itemInHand.getType();
        if (!(itemInHandMaterial == Material.SHEARS || itemInHandMaterial == Material.GLASS_BOTTLE)) {
            return;
        }

        // check that the honey level is high enough to be harvested
        if (block.getBlockData() instanceof Beehive beehive) {
            if (beehive.getHoneyLevel() != 5) {
                return;
            }

            // cancel event and simulate honey or honeycomb being taken from the bee nest or beehive
            event.setCancelled(true);
            beehive.setHoneyLevel(0);
            block.setBlockData(beehive);
            if (itemInHandMaterial == Material.GLASS_BOTTLE) {
                // remove 1 glass bottle
                ItemStack newItem = null;
                if (itemInHand.getAmount() > 1) {
                    itemInHand.setAmount(itemInHand.getAmount() - 1);
                    newItem = itemInHand;
                }
                player.getInventory().setItemInMainHand(newItem);
                // give player honey bottle
                player.give(new ItemStack(Material.HONEY_BOTTLE, 1));
            }
            else {
                // take shears durability damage
                if (itemInHand.getItemMeta() instanceof Damageable damageable) {
                    int unbreakingLevel = itemInHand.getEnchantmentLevel(Enchantment.UNBREAKING);
                    Random random = new Random();
                    // Unbreaking chance: 1 / (level + 1) to consume durability
                    boolean shouldDamage = (unbreakingLevel == 0) || (random.nextDouble() >= (1.0 / (unbreakingLevel + 1)));

                    if (shouldDamage) {
                        damageable.setDamage(damageable.getDamage() + 1);
                        if (damageable.getDamage() >= itemInHand.getType().getMaxDurability()) {
                            // Break the item
                            player.getInventory().setItemInMainHand(null);
                            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                        }
                        else {
                            itemInHand.setItemMeta(damageable);
                        }
                    }
                }
                // give player 3 honeycomb
                player.give(new ItemStack(Material.HONEYCOMB, 3));
            }
        }
    }
}