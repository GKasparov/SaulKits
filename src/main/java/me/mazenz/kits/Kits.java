package me.mazenz.kits;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.logging.Logger;

public final class Kits extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;

    @Override
    public void onEnable() {
        try {
            getConfig().options().copyDefaults(true);
            saveConfig();
            genPerms();
            Objects.requireNonNull(getCommand("kits")).setExecutor(new KitsCommand(this));
            Objects.requireNonNull(getCommand("kit")).setExecutor(new KitCommand(this));

            if (getConfig().getBoolean("vaultEnabled")) {
                if (!setupEconomy()) {
                    log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
                    getServer().getPluginManager().disablePlugin(this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp == null) {
            return false;
        }

        econ = rsp.getProvider();
        return true;
    }

    @Override
    public void onDisable() {
        System.out.println("Kits Stopping");
    }

    public void genPerms() throws IOException {
        try (FileWriter permissions = new FileWriter("./permissions.yml")) {
            for (String kitName : Objects.requireNonNull(getConfig().getConfigurationSection("kits")).getKeys(false)) {
                String permissionsContent = Files.readString(Path.of("./permissions.yml"), StandardCharsets.UTF_8);

                if (!permissionsContent.contains(kitName)) {
                    permissions.write(System.getProperty("line.separator"));
                    permissions.write("kits.kit." + kitName + ":");
                    permissions.write(System.getProperty("line.separator"));
                    permissions.write("       default: op");
                    permissions.write(System.getProperty("line.separator"));
                }
            }
        }
        System.out.println("[Kits] Permissions loaded");
    }
}
