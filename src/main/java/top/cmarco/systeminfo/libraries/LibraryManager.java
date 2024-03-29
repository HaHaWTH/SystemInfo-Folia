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

package top.cmarco.systeminfo.libraries;

import com.alessiodp.libby.BukkitLibraryManager;
import com.alessiodp.libby.Library;
import org.jetbrains.annotations.NotNull;
import top.cmarco.systeminfo.plugin.SystemInfo;

/**
 * Class used to load libraries required by this plugin.
 */
public final class LibraryManager {

    private final SystemInfo plugin;
    private final BukkitLibraryManager libraryManager;

    /**
     * Constructor for the LibraryManager class.
     * @param plugin The instance of this plugin.
     */
    public LibraryManager(@NotNull SystemInfo plugin) {
        this.plugin = plugin;
        this.libraryManager = new BukkitLibraryManager(plugin);
        this.libraryManager.addRepository("https://maven.aliyun.com/repository/public/");
    }

    /**
     * Load jspeedtest (and required compile dependencies).
     * Library responsible for speed-test.
     */
    public void loadSpeedtestLibraries() {

        Library jSpeedTest = Library.builder()
                .groupId("fr{}bmartel")
                .artifactId("jspeedtest")
                .version("1.32.1")
                .build();

        Library httpEndec = Library.builder()
                .groupId("fr{}bmartel")
                .artifactId("http-endec")
                .version("1.04")
                .build();

        Library apacheCommonsNet = Library.builder()
                .groupId("commons-net")
                .artifactId("commons-net")
                .version("3.10.0")
                .build();

        this.libraryManager.loadLibraries(apacheCommonsNet, httpEndec, jSpeedTest);
    }

    /**
     * Load oshi-core library (and required compile dependencies).
     * Library responsible for providing hardware values.
     */
    public void loadOshiLibraries() {

        final Library oshiCore = Library.builder()
                .groupId("com{}github{}oshi")
                .artifactId("oshi-core")
                .version("6.4.12")
                .build();

        final Library jna = Library.builder()
                .groupId("net{}java{}dev{}jna")
                .artifactId("jna")
                .version("5.14.0")
                .build();

        final Library jnaPlatform = Library.builder()
                .groupId("net{}java{}dev{}jna")
                .artifactId("jna-platform")
                .version("5.14.0")
                .build();

        final Library slf4jApi = Library.builder()
                .groupId("org{}slf4j")
                .artifactId("slf4j-api")
                .version("2.0.12")
                .build();

        this.libraryManager.loadLibraries(slf4jApi, jna, jnaPlatform, oshiCore);
    }
}
