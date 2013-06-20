package com.BeastsMC;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class RestartAnnounce extends JavaPlugin {
	public Logger log;
	public boolean restartScheduled = false;
	public ScheduledRestart restart = null;
	public void onEnable() {
		log = this.getLogger();
		getServer().getPluginManager().registerEvents(new RALoginListener(this), this);
		getCommand("sr").setExecutor(this);
		getCommand("cancelrestart").setExecutor(this);
	}
	public boolean onCommand(CommandSender sender, Command command, String cmd, String[] args) {
		if(cmd.equalsIgnoreCase("cancelrestart")) {
			if(restartScheduled) {
				cancelRestart();
				restartScheduled = false;
				log.info("Cancelled the scheduled restart");
				sender.sendMessage(ChatColor.BLUE + "Cancelled the scheduled restart");
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "No server restart scheduled");
				return true;
			}
		} else {
			if(restartScheduled) {
				sender.sendMessage(ChatColor.RED + "Server restart already scheduled");
				return true;
			}
			if(args.length > 0) {
				Integer seconds;
				try {
					seconds = Integer.parseInt(args[0]);
				} catch(NumberFormatException e) {
					sender.sendMessage(ChatColor.RED + "'"  + args[0] +"' is not a number.");
					return false;
				}
				restart = new ScheduledRestart(this, seconds);
				restartScheduled = true;
				log.info("Scheduled restart to occur in " + seconds + " seconds");
				getServer().broadcastMessage(ChatColor.BLUE + "A restas has been scheduled to occur in " + seconds + " seconds");
				return true;
			}
			return false;
		}
	}
	private void cancelRestart() {
		restart.destroy();
		restart = null;
	}

}
