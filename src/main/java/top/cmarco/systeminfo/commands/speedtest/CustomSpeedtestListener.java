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

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.cmarco.systeminfo.plugin.SystemInfo;
import top.cmarco.systeminfo.utils.Utils;

/**
 * A custom listener for handling speedtest events.
 */
public final class CustomSpeedtestListener implements ISpeedTestListener {

    private final CommandSender sender;
    private final SpeedTestSocket speedTestSocket;
    private final SystemInfo plugin;

    CustomSpeedtestListener(CommandSender sender, SpeedTestSocket speedTestSocket, SystemInfo plugin) {
        this.sender = sender;
        this.speedTestSocket = speedTestSocket;
        this.plugin = plugin;
    }

    @Override
    public void onCompletion(SpeedTestReport speedTestReport) {
        sender.sendMessage(Utils.color("&7» &7Mode: &a" + speedTestReport.getSpeedTestMode().name()));
        sender.sendMessage(Utils.color("&7» &7Result: &a" + Utils.formatData(speedTestReport.getTransferRateOctet().longValue()) + "/s" + " &7or &a" +
                Utils.formatDataBits(speedTestReport.getTransferRateOctet().longValue()) + "/s"));
        speedTestSocket.removeSpeedTestListener(this);
        speedTestSocket.shutdownAndWait();
    }

    private float lastPercent = -1f;

    @Override
    public void onProgress(float v, SpeedTestReport speedTestReport) {
        long startTime = speedTestReport.getStartTime();
        float percent = speedTestReport.getProgressPercent();

        if (Math.floor(percent - lastPercent) < 0.101f) {
            return;
        }

        if (!((System.currentTimeMillis() - startTime) % 2500 == 0)) {
            return;
        }

        StringBuilder barBuilder = new StringBuilder("&7[");
        for (int i = 0; i < 100; i+=10) {
            barBuilder.append(i < percent ? "&a|" : "&c|");
        }
        barBuilder.append("&7]");
        sender.sendMessage(Utils.color("&7| &aDownload Progress: &e" + barBuilder + "  " + String.format("%.1f", percent) + "%"));

    }

    @Override
    public void onError(SpeedTestError speedTestError, String s) {
        sender.sendMessage(Utils.color("&7» &cYour machine network is not configured properly."));
        sender.sendMessage(Utils.color("&7» &cThe plugin was not able to perform a speedtest, contact your host or check your system firewall."));
        sender.sendMessage(Utils.color("&7» &cError Code: &4&l" + speedTestError.name()));
        speedTestSocket.shutdownAndWait();
    }
}
