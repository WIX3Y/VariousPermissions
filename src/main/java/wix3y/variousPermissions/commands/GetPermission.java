package wix3y.variousPermissions.commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GetPermission implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<dark_gray>[<gradient:#9999BB:#EEEEFF:#9999BB>Various Permissions</gradient>]</dark_gray> <gray>>> <red>Only players can use this command."));
            return true;
        }

        String permission = null;
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if (isPermissionItem(itemInMainHand.getType())) {
            permission = "variouspermissions." + itemInMainHand.getType().name().toLowerCase();
            if (itemInMainHand.hasItemMeta() && itemInMainHand.getItemMeta().hasItemModel()) {
                permission = "variouspermissions." + itemInMainHand.getItemMeta().getItemModel().getKey().toLowerCase();
            }
        }

        if (permission == null) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<dark_gray>[<gradient:#9999BB:#EEEEFF:#9999BB>Various Permissions</gradient>]</dark_gray> <gray>>> <red>No permission exists for held item."));
        }
        else {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<dark_gray>[<gradient:#9999BB:#EEEEFF:#9999BB>Various Permissions</gradient>]</dark_gray> <gray>>> " + permission));
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