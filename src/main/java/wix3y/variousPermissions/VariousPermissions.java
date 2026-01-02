package wix3y.variousPermissions;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import wix3y.variousPermissions.commands.GetPermission;
import wix3y.variousPermissions.commands.TakeBook;
import wix3y.variousPermissions.commands.TakeItem;
import wix3y.variousPermissions.handlers.*;
import wix3y.variousPermissions.placeholders.PapiVPExpansion;

public final class VariousPermissions extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        new PlayerBeeHarvestHandler(this);
        new PlayerBlockBreakHandler(this);
        new PlayerAttackHandler(this);
        new PlayerInteractHandler(this);
        new PlayerFishHandler(this);

        getCommand("vppermission").setExecutor(new GetPermission());
        getCommand("vptakeitem").setExecutor(new TakeItem());
        getCommand("vptakebook").setExecutor(new TakeBook());

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PapiVPExpansion().register();
        }

        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(MiniMessage.miniMessage().deserialize("    <gradient:#9999BB:#EEEEFF:#9999BB>Various Permissions</gradient>"));
        Bukkit.getConsoleSender().sendMessage(MiniMessage.miniMessage().deserialize("          <gray>v1.0.0"));
        Bukkit.getConsoleSender().sendMessage(MiniMessage.miniMessage().deserialize("          <green>Enabled"));
        Bukkit.getConsoleSender().sendMessage("");
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(MiniMessage.miniMessage().deserialize("    <gradient:#9999BB:#EEEEFF:#9999BB>Various Permissions</gradient>"));
        Bukkit.getConsoleSender().sendMessage(MiniMessage.miniMessage().deserialize("          <gray>v1.0.0"));
        Bukkit.getConsoleSender().sendMessage(MiniMessage.miniMessage().deserialize("         <red>Disabled"));
        Bukkit.getConsoleSender().sendMessage("");
    }
}
