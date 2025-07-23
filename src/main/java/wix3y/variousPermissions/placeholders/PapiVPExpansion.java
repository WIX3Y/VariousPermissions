package wix3y.variousPermissions.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PapiVPExpansion extends PlaceholderExpansion {

    /**
     * Plugin's placeholder identifier
     *
     * @return the plugins placeholder identifier
     */
    @Override
    public @NotNull String getIdentifier() {
        return "VP";
    }

    /**
     * Plugin's author
     *
     * @return the plugins author
     */
    @Override
    public @NotNull String getAuthor() {
        return "WIX3Y";
    }

    /**
     * Plugin version
     *
     * @return the plugins version
     */
    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    /**
     * Plugin's placeholder parsing
     *
     * @param player the player to parse for
     * @param params the placeholder
     * @return the parsed placeholder
     */
    @Override @Nullable
    public String onPlaceholderRequest(@Nullable Player player, @NotNull String params) {
        if (player == null) {
            return null;
        }

        if (params.startsWith("has_item_amount_")) {
            String[] parameters = params.substring(16).split("_");
            if (parameters.length != 4) {
                return null;
            }

            Material material = Material.getMaterial(parameters[0].toUpperCase());
            if (material == null) {
                return null;
            }

            int amount;
            try {
                amount = Integer.parseInt(parameters[3]);
            } catch (NumberFormatException e) {
                return null;
            }
            if (amount <= 0) {
                return "true";
            }

            NamespacedKey itemModel = null;
            if (!parameters[1].equals("none") && !parameters[2].equals("none")) {
                itemModel = new NamespacedKey(parameters[1].toLowerCase(), parameters[2].toLowerCase());
            }

            int count = 0;
            for (ItemStack item : player.getInventory().getContents()) {
                if (item == null || item.getType() == Material.AIR) {
                    continue;
                }
                if (item.getType() != material) {
                    continue;
                }

                if (itemModel == null && (!item.hasItemMeta() || (item.hasItemMeta() && !item.getItemMeta().hasItemModel()))) {
                    // Matching item with no item model
                    count += item.getAmount();
                } else if (item.hasItemMeta() && item.getItemMeta().hasItemModel()) {
                    if (item.getItemMeta().getItemModel().equals(itemModel)) {
                        // Matching item with item model
                        count += item.getAmount();
                    }
                }
            }

            if (count >= amount) {
                return "true";
            }
            return "false";
        }

        return null;
    }

    /**
     * Plugin placeholders persist over reloads
     *
     * @return true
     */
    @Override
    public boolean persist() {
        return true;
    }
}