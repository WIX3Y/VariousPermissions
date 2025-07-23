package wix3y.variousPermissions.commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TakeItem implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("variouspermissions.takeitem")) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>You do not have permission to use this command"));
            return true;
        }

        if (args.length < 5) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>/takeitem <player> <material> <item-model-namespace> <item-model-key> <amount>"));
            return true;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Invalid player name"));
            return true;
        }

        Material material = Material.getMaterial(args[1].toUpperCase());
        if (material == null) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Invalid material"));
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[4]);
        } catch (NumberFormatException e) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Invalid amount, amount must be an integer."));
            return true;
        }
        if (amount <= 0) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Invalid amount, amount must be > 0."));
            return true;
        }

        NamespacedKey itemModel = null;
        if (!args[2].equals("none") && !args[3].equals("none")) {
            itemModel = new NamespacedKey(args[2].toLowerCase(), args[3].toLowerCase());
        }

        boolean success = false;

        ItemStack[] content = player.getInventory().getContents();
        for (int i=0; i<content.length; i++) {
            ItemStack item = content[i];
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }
            if (item.getType() != material) {
                continue;
            }

            if (itemModel == null && (!item.hasItemMeta() || (item.hasItemMeta() && !item.getItemMeta().hasItemModel()))) {
                // Matching item with no item model
                if (item.getAmount() > amount) {
                    item.setAmount(item.getAmount() - amount);
                    amount = 0;
                }
                else {
                    amount -= item.getAmount();
                    player.getInventory().setItem(i, null);
                }

                if (amount <= 0) {
                    success = true;
                    break;
                }
            }
            else if (item.hasItemMeta() && item.getItemMeta().hasItemModel()) {
                if (item.getItemMeta().getItemModel().equals(itemModel)) {
                    // Matching item with item model
                    if (item.getAmount() > amount) {
                        item.setAmount(item.getAmount() - amount);
                        amount = 0;
                    }
                    else {
                        amount -= item.getAmount();
                        player.getInventory().setItem(i, null);
                    }

                    if (amount <= 0) {
                        success = true;
                        break;
                    }
                }
            }
        }

        if (success) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray>Took " + args[4] + " item(s) of material " + args[1] + " with item model " + args[2] + ":" + args[3] + " from player " + args[0]));
        }
        else {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>WARNING!!! Failed to take " + args[4] + " item(s) of material " + args[1] + " with item model " + args[2] + ":" + args[3] + " from player " + args[0]));
        }
        return true;
    }

    private boolean isPermissionItem(Material material) {
        return material.name().endsWith("_PICKAXE") ||
                material.name().endsWith("_AXE") ||
                material.name().endsWith("_SHOVEL") ||
                material.name().endsWith("_HOE") ||
                material.name().endsWith("_SWORD") ||
                material == Material.TRIDENT ||
                material == Material.BOW ||
                material == Material.BRUSH ||
                material == Material.CROSSBOW;
    }
}