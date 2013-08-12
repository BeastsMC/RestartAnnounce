package com.BeastsMC;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class ScheduledRestart extends BukkitRunnable {
	private Integer secondsUntilRestart;
	private final RestartAnnounce plugin;
	private final Objective objective;
	private final Scoreboard board;
	public ScheduledRestart(RestartAnnounce main, Integer seconds) {
		plugin = main;
		secondsUntilRestart = seconds;
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		board = manager.getNewScoreboard();
		objective = board.registerNewObjective("restarttime", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName("Time Until Restart");
		for(Player online : Bukkit.getOnlinePlayers()) {
			online.setScoreboard(board);
		}
		this.runTaskTimer(plugin, 20, 20);
	}
	@Override
	public void run() {
		secondsUntilRestart--;
		if(secondsUntilRestart==0) {
			plugin.log.info("Executing shutdown commands");
			for(String command : plugin.shutdownCommands) {
				plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
			}
			plugin.log.info("Running shut down");
			Bukkit.shutdown();
			this.cancel();
		} else if(secondsUntilRestart==3) {
			for(String message : plugin.shutdownMessages) {
				plugin.getServer().broadcastMessage(message.replace('&', ChatColor.COLOR_CHAR)
						.replaceAll("%time%", secondsUntilRestart.toString()));
			}
		}
		this.updateDisplays();
	}
	private void updateDisplays() {
		OfflinePlayer restartLine;
		Score score;
		if(secondsUntilRestart==59) {
			restartLine = Bukkit.getOfflinePlayer(ChatColor.RED + "Minutes:");
			board.resetScores(restartLine);
		}
		if(secondsUntilRestart>59) {
			restartLine = Bukkit.getOfflinePlayer(ChatColor.RED + "Minutes:");
			Integer minutesToRestart = secondsUntilRestart/60;
			score = objective.getScore(restartLine);
			score.setScore(minutesToRestart);

		} else {
			restartLine = Bukkit.getOfflinePlayer(ChatColor.RED + "Seconds:");
			score = objective.getScore(restartLine);
			score.setScore(secondsUntilRestart);
		}
	}
	public void destroy() {
		this.cancel();
		objective.unregister();
	}
	public Scoreboard getScoreboard() {
		return board;
	}
	public String getFormatedTime() {
		if(secondsUntilRestart<60) {
			return secondsUntilRestart + " seconds";
		} else if(secondsUntilRestart<3600) {
			return (double)Math.round(secondsUntilRestart/60.0 * 100) / 100 + " minutes";
		} else {
			return (double)Math.round(secondsUntilRestart/3600.0 * 100) / 100 + " hours";
		}
	}
}
