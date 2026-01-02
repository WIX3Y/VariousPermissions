package wix3y.variousPermissions.commands;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.jetbrains.annotations.NotNull;

public class TakeBook implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("variouspermissions.takebook")) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>You do not have permission to use this command"));
            return true;
        }

        if (args.length < 4) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>/takebook <player> <enchantment> <level> <amount>"));
            return true;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Invalid player name"));
            return true;
        }

        Enchantment enchantment;
        try {
            NamespacedKey enchantmentKey = new NamespacedKey("minecraft", args[1].toLowerCase());
            Registry<@NotNull Enchantment> enchantmentRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
            enchantment = enchantmentRegistry.get(enchantmentKey);

        }
        catch (Exception e) {
            enchantment = null;
        }
        if (enchantment == null) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Invalid enchantment"));
            return true;
        }

        int level;
        try {
            level = Integer.parseInt(args[2]);
        }
        catch (NumberFormatException e) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Invalid level, level must be an integer."));
            return true;
        }
        if (level < 1 || level > 255) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Invalid level, leve must be between 1 and 255."));
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[3]);
        }
        catch (NumberFormatException e) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Invalid amount, amount must be an integer."));
            return true;
        }
        if (amount <= 0) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Invalid amount, amount must be > 0."));
            return true;
        }

        boolean success = false;
        ItemStack[] content = player.getInventory().getContents();
        for (int i=0; i<content.length; i++) {
            ItemStack item = content[i];
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }
            if (item.getType() != Material.ENCHANTED_BOOK) {
                continue;
            }

            if (item.hasItemMeta() && item.getItemMeta() instanceof EnchantmentStorageMeta enchantmentStorageMeta) {
                if (enchantmentStorageMeta.getStoredEnchants().size() != 1) {
                    continue;
                }

                if (enchantmentStorageMeta.hasStoredEnchant(enchantment) && enchantmentStorageMeta.getStoredEnchantLevel(enchantment) == level) {
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
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray>Took " + args[3] + " enchanted book(s), enchanted with " + args[1] + " " + args[2] + " from player " + args[0]));
        }
        else {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>WARNING!!! Failed to take " + args[3] + " enchanted book(s), enchanted with " + args[1] + " " + args[2] + " from player " + args[0]));
        }
        return true;
    }
}