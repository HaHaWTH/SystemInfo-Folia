/*
 *     SystemInfo - The Master of Server Hardware
 *     Copyright © 2024 CMarco
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.cmarco.systeminfo.plugin;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import top.cmarco.systeminfo.api.SystemInfoPlaceholderExtension;
import top.cmarco.systeminfo.commands.register.CommandManager;
import top.cmarco.systeminfo.oshi.SystemValues;
import top.cmarco.systeminfo.utils.Utils;

import java.time.LocalDateTime;
import java.util.logging.Logger;

/**
 * The main class of the SystemInfo Spigot plugin, responsible for initializing and managing the plugin's features.
 * This plugin provides information about the server's system and hardware.
 */
public final class SystemInfo extends JavaPlugin {

    public static SystemInfo INSTANCE = null;
    private final LocalDateTime startupTime = LocalDateTime.now(); // Timestamp when the plugin was loaded.
    private final PluginManager pluginManager = Bukkit.getPluginManager(); // The Spigot PluginManager instance.
    private CommandManager commandManager; // The custom CommandManager for handling plugin commands.
    private SystemInfoPlaceholderExtension systemInfoExtension; // PlaceholderAPI extension for custom placeholders.
    private SystemValues systemValues; // Manager for system information values.

    @Override
    public void onDisable() {
        // empty implementation.
        // necessary for beta-1.7.3 JavaPlugin
    }

    /**
     * Called when the plugin is enabled. It initializes various components and registers listeners.
     */
    @Override
    public void onEnable() {
        INSTANCE = this;
        loadValues();
        loadCommands();
        loadAPI();
        setupMetrics();
    }

    private void setupMetrics() {
        final Metrics metrics = new Metrics(this, 5610);
    }

    public Logger getLogger() {
        return Bukkit.getLogger();
    }

    /**
     * Checks if the bukkit version contains OSHI.
     *
     * @param plugin The Plugin.
     * @return True if at least Bukkit 1.17.
     */
    private static boolean checkVersion(@NotNull Plugin plugin) {
        return true;
    }

    /**
     * Initializes and updates the SystemValues.
     */
    private void loadValues() {
        systemValues = new SystemValues(getLogger());
        systemValues.updateValues();
    }

    /**
     * Initializes and registers the PlaceholderAPI extension if PlaceholderAPI is available.
     * Otherwise, logs a warning message.
     */
    private void loadAPI() {
        if (pluginManager.isPluginEnabled("PlaceholderAPI")) {
            systemInfoExtension = new SystemInfoPlaceholderExtension(this);
            systemInfoExtension.register();
            this.getLogger().info("Successfully Registered SystemInfo with PlaceholderAPI.");
        } else {
            this.getLogger().info("Could not find PlaceholderAPI, placeholders won't be available.");
        }
    }

    /**
     * Initializes and registers custom commands using the CommandManager.
     * If there's an issue with accessing the command map, the plugin will be disabled.
     */
    private void loadCommands() {
        commandManager = new CommandManager(this);
        commandManager.createInstances();
        commandManager.registerAll();
    }

    // Getters for class members

    /**
     * @return Gets the timestamp this plugin was loaded at.
     */
    @NotNull
    public LocalDateTime getStartupTime() {
        return startupTime;
    }

    /**
     * @return Gets the server plugin manager.
     */
    @NotNull
    public PluginManager getPluginManager() {
        return pluginManager;
    }

    /**
     * @return Gets the server command manager.
     */
    @NotNull
    public CommandManager getCommandManager() {
        return commandManager;
    }

    /**
     * @return Gets the PlaceholderAPI extension.
     */
    @NotNull
    public SystemInfoPlaceholderExtension getSystemInfoExtension() {
        return systemInfoExtension;
    }

    /**
     * @return Gets the system values class.
     */
    @NotNull
    public SystemValues getSystemValues() {
        return systemValues;
    }
}
