package com.BeastsMC;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class RestartAnnounce extends JavaPlugin {
	public Logger log;
	public boolean restartScheduled = false;
	public ScheduledRestart restart = null;
	public IntervalRestart intervalRestart = null;
	public List<String> shutdownCommands = new ArrayList<String>();
	public List<String> scheduleMessages = new ArrayList<String>();
	public List<String> shutdownMessages = new ArrayList<String>();
	public void onEnable() {
		log = this.getLogger();
		getServer().getPluginManager().registerEvents(new RALoginListener(this), this);
		getCommand("sr").setExecutor(this);
		getCommand("cancelrestart").setExecutor(this);
		saveDefaultConfig();
		loadConfig();
	}
	public boolean onCommand(CommandSender sender, Command command, String cmd, String[] args) {
		if(cmd.equalsIgnoreCase("cancelrestart")) {
			if(sender.hasPermission("restartannounce.admin")) {
				if(restartScheduled) {
					cancelRestart();
					restartScheduled = false;
					log.info("Cancelled the scheduled restart");
					sender.sendMessage(ChatColor.BLUE + "Cancelled the scheduled restart");
				} else {
					sender.sendMessage(ChatColor.RED + "No server restart scheduled");
				}
			} else {
				sender.sendMessage("You do not have permission to use this command!");
			}
			return true;
		} else {
			if(args.length > 0) {
				if(args[0].equalsIgnoreCase("reload")) {
					if(sender.hasPermission("restartannounce.reload")) {
						sender.sendMessage(ChatColor.AQUA + "Reloading...");
						reload();
						sender.sendMessage(ChatColor.AQUA + "Done reloading!");
						return true;
					} else {
						sender.sendMessage("You do not have permission to use this command!");
						return true;
					}

				} else if(args[0].equalsIgnoreCase("time")) {
					if(sender.hasPermission("restartannounce.time")) {
						if(intervalRestart!=null) {
							sender.sendMessage(ChatColor.AQUA + "Time until restart: " + intervalRestart.getFormatedTime());
						} else if(restart!=null) {
							sender.sendMessage(ChatColor.AQUA + "Time until restart: " + restart.getFormatedTime());
						} else {
							sender.sendMessage(ChatColor.AQUA + "There is currently no planned restarts.");
						}
					} else {
						sender.sendMessage("You do not have permission to use this command!");
					}
					return true;
				} else {
					if(sender.hasPermission("restartannounce.admin")) {
						if(restartScheduled) {
							sender.sendMessage(ChatColor.RED + "Server restart already scheduled");
							return true;
						}
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
						for(String message: scheduleMessages) {
							getServer().broadcastMessage(message.replace('&', ChatColor.COLOR_CHAR).replaceAll("%time%", seconds.toString()));
						}
						return true;
					} else {
						sender.sendMessage("You do not have permission to use this command!");
						return true;
					}
				}
			}
			return false;
		}
	}
	private void cancelRestart() {
		restart.destroy();
		restart = null;
	}
	private void loadConfig() {
		FileConfiguration fConf = getConfig();
		try {
			fConf.load(new File(getDataFolder(), "config.yml"));
			if(fConf.getInt("config-version")<2) {
				Configuration newConf = fConf.getDefaults();
				fConf.set("broadcast-on-schedule", newConf.getList("broadcast-on-schedule"));
				fConf.set("broadcast-before-shutdown", newConf.getList("broadcast-before-shutdown"));
				fConf.set("config-version", 2);
				fConf.save(new File(getDataFolder(), "config.yml"));
			}
			if(fConf.getBoolean("restart.enableInterval")) {
				String rawInterval = fConf.getString("restart.interval");
				String unit = rawInterval.substring(rawInterval.length() - 1);
				Integer rawIntervalValue = Integer.parseInt(rawInterval.substring(0, rawInterval.length() - 1));
				Integer intervalSeconds;
				if(unit.equalsIgnoreCase("s")) {
					intervalSeconds = rawIntervalValue;
				} else if(unit.equalsIgnoreCase("m")) {
					intervalSeconds = rawIntervalValue*60;
				} else if(unit.equalsIgnoreCase("h")) {
					intervalSeconds = rawIntervalValue*3600;
				} else {
					log.warning("Unit for interval is invalid: " + unit);
					return;
				}
				intervalRestart = new IntervalRestart(this, intervalSeconds); 
			}
			shutdownCommands = fConf.getStringList("restart.commands");
			scheduleMessages = fConf.getStringList("broadcast-on-schedule");
			shutdownMessages = fConf.getStringList("broadcast-before-shutdown");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	private void reload() {
		if(intervalRestart!=null) {
			intervalRestart.cancel();
		}
		if(restartScheduled==true) {
			restart.destroy();
			restartScheduled = false;
		}
		loadConfig();
	}

}
