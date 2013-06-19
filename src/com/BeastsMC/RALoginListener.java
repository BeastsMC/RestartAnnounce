package com.BeastsMC;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class RALoginListener implements Listener {
	private final RestartAnnounce plugin;
	public RALoginListener(RestartAnnounce main) {
		plugin = main;
	}
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent playerjoin) {
		if(plugin.restart != null && plugin.restartScheduled) {
			Player p = playerjoin.getPlayer();
			p.setScoreboard(plugin.restart.getScoreboard());
		}
	}
}
