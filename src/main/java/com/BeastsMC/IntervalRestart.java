package com.BeastsMC;

import org.bukkit.scheduler.BukkitRunnable;

public class IntervalRestart extends BukkitRunnable {
    private Integer secondsUntilRestart;
    private final RestartAnnounce plugin;

    public IntervalRestart(RestartAnnounce main, Integer seconds) {
        plugin = main;
        secondsUntilRestart = seconds;
        this.runTaskTimer(plugin, 20, 20);
    }

    @Override
    public void run() {
        if (secondsUntilRestart <= 3600) {
            plugin.restart = new ScheduledRestart(plugin, secondsUntilRestart);
            plugin.restartScheduled = true;
            plugin.intervalRestart = null;
            this.cancel();
        }
        secondsUntilRestart--;
    }

    public String getFormatedTime() {
        if (secondsUntilRestart < 60) {
            return secondsUntilRestart + " seconds";
        } else if (secondsUntilRestart < 3600) {
            return (double) Math.round(secondsUntilRestart / 60 * 1000) / 1000 + " minutes";
        } else {
            return (double) Math.round(secondsUntilRestart / 3600 * 1000) / 1000 + " hours";
        }
    }
}
