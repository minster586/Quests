package com.leonardobishop.quests.common.updater;

import com.leonardobishop.quests.common.plugin.Quests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

public class Updater {
 
    private static final int PROJECT_ID = 23696;
    private final String installedVersion;
    private final boolean enabled;
    private final Quests plugin;

    private String returnedVersion;
    private URL api;
    private boolean updateReady;
    private long lastCheck;

    public Updater(Quests plugin, String installedVersion, boolean enabled) {
        this.plugin = plugin;
        this.installedVersion = installedVersion;
        this.enabled = enabled;
        try {
            this.api = new URL(getApiUrl());
        } catch (MalformedURLException ignored) { }
    }

    public String getUpdateLink() {
        return "https://www.spigotmc.org/resources/" + PROJECT_ID;
    }

    public String getApiUrl() {
        return "https://api.spigotmc.org/legacy/update.php?resource=" + PROJECT_ID;
    }

    public String getInstalledVersion() {
        return installedVersion;
    }

    public String getReturnedVersion() {
        return returnedVersion;
    }

    public void check() {
        if (!enabled) {
            return;
        }
        // stop users from spamming the command and making needless requests
        if (lastCheck != 0 && TimeUnit.MINUTES.convert(System.currentTimeMillis() - lastCheck, TimeUnit.MILLISECONDS) < 10) {
            return;
        }
        try {
            lastCheck = System.currentTimeMillis();
            URLConnection con = api.openConnection();
            returnedVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
            if (!returnedVersion.equals(installedVersion)) {
                plugin.getQuestsLogger().info("A new version " + returnedVersion + " was found on Spigot (your version: " + installedVersion + "). Please update me! <3 - Link: " + getUpdateLink());
                updateReady = true;
            } else {
                updateReady = false;
            }
        } catch (IOException e) {
            plugin.getQuestsLogger().warning("Failed to check for updates. You can check manually at " + getUpdateLink());
            // probably offline
        }
    }

    public boolean isUpdateReady() {
        return updateReady;
    }

}