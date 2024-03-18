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

package top.cmarco.systeminfo.commands.speedtest;

import fr.bmartel.speedtest.SpeedTestSocket;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import top.cmarco.systeminfo.commands.SystemInfoCommand;
import top.cmarco.systeminfo.plugin.SystemInfo;
import top.cmarco.systeminfo.utils.Utils;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The `CommandSpeedtest` class is a Spigot command that allows players with the appropriate permission to perform
 * a network speedtest using the "/speedtest" command.
 */
public final class CommandSpeedtest extends SystemInfoCommand {

    private final SpeedTestSocket speedTestSocket = new SpeedTestSocket();

    /**
     * Initializes a new instance of the `CommandSpeedtest` class.
     *
     * @param systemInfo The `SystemInfo` instance associated with this command.
     */
    public CommandSpeedtest(@NotNull SystemInfo systemInfo) {
        super(systemInfo, "speedtest",
                "Perform a network speedtest",
                "/<command> <GBs>",
                Collections.emptyList());
    }

    /**
     * Performs a network speedtest and provides results to the sender.
     *
     * @param sender The command sender.
     * @param size   The size of the test file to download (in GBs).
     */
    private void performSpeedtest(CommandSender sender, int size) {
        sender.sendMessage(Utils.color("&2« &7Speedtest &2»"));

        SpeedTestSocket speedTestSocket = new SpeedTestSocket();

        speedTestSocket.addSpeedTestListener(new CustomSpeedtestListener(sender, speedTestSocket, systemInfo));

        // Start the download test
        speedTestSocket.startDownload(String.format("https://testfile.org/file-%dGB", size));
    }

    /**
     * A regular expression pattern to match numbers.
     */
    private final static Pattern NUMBER_PATTERN = Pattern.compile("^[0-9]+$");

    /**
     * Executes the "/speedtest" command with the provided arguments.
     *
     * @param commandSender The command sender.
     * @param s             The command name.
     * @param strings       The command arguments.
     * @return True if the command was executed successfully; otherwise, false.
     */
    @Override
    public boolean execute(CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
        if (commandSender.isOp()) {
            if (strings.length == 0) {
                performSpeedtest(commandSender, 1);
                return true;
            } else if (strings.length == 1) {
                final String fSize = strings[0];
                final Matcher matcher = NUMBER_PATTERN.matcher(fSize);
                if (matcher.matches()) {
                    final int fSizeNumber = Integer.parseInt(fSize);
                    if (fSizeNumber == 1 || fSizeNumber == 10 || fSizeNumber == 100) {
                        performSpeedtest(commandSender, fSizeNumber);
                    } else {
                        commandSender.sendMessage(Utils.color("&7» &cYou used a file size that is too big or invalid!"));
                        commandSender.sendMessage(Utils.color("  &7The only available file sizes are:"));
                        for (int i = 0; i < 3; i++) {
                            commandSender.sendMessage(Utils.color(String.format("  &7- &c%d&7GB", (int) Math.pow(10, i))));
                        }
                    }
                } else {
                    commandSender.sendMessage(Utils.color("&7» &cYou did not use a number as the download size argument!"));
                    return true;
                }
            }
        }
        return false;
    }
}
