package me.subtypezero.announcer;

import com.google.inject.Inject;
import me.subtypezero.announcer.command.CommandAnnounce;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.ConfigManager;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

@Plugin(id = PluginInfo.ID, name = PluginInfo.NAME, version = PluginInfo.VERSION, description = PluginInfo.DESCRIPTION, authors = PluginInfo.AUTHORS)
public class Announcer {
	private static Announcer instance;
	private static Cause genericCause;

	@Inject
	private Logger logger;

	@Inject
	private Game game;

	@Inject
	@ConfigDir(sharedRoot = false)
	private Path configDir;

	@Inject
	@DefaultConfig(sharedRoot = false)
	private ConfigurationLoader<CommentedConfigurationNode> configManager;
	private ConfigManager pluginConfigManager;
	// private GlobalConfig defaultConfig;

	private List<String> messages;
	private String prefix;
	private long interval;
	private boolean enabled;
	private boolean random;
	private AnnouncerThread thread;
	private Task task;

	@Listener
	public void onGameConstruction(GameConstructionEvent event) {
		instance = this;
	}

	@Listener
	public void onPreInitialization(GamePreInitializationEvent event) {
		defaultConfig = new GlobalConfig();
		pluginConfigManager = new ConfigManager(configManager);
		pluginConfigManager.save();
	}

	@Listener
	public void onServerStart(GameStartedServerEvent event) {
		instance = this;
		genericCause = Cause.builder().named("plugin", this).build();
		registerCommands();
	}

	private void registerCommands() {
		CommandAnnounce.register();
	}

	private void createTask() {
		task = Task.builder().execute(this.thread).intervalTicks(this.interval * 20L).async().submit(this);
	}

	public void announce() {
		this.thread.run();
	}

	public void announce(int index) {
		announce(this.messages.get(index - 1));
	}

	public void announce(String line) {
		String[] messages = line.split("&n");

		for (String message : messages)
			if (message.startsWith("/")) {
				game.getCommandManager().process(game.getServer().getConsole(), message.substring(1));
			} else if (game.getServer().getOnlinePlayers().size() > 0) {
				Text messageToSend = ChatColorHelper.replaceColorCodes(String.format("%s%s", new Object[]{this.prefix, message}));
				for (Player player : game.getServer().getOnlinePlayers())
					if (player.hasPermission("announcer.receiver"))
						player.sendMessage(messageToSend);
			}
	}

	public void saveConfiguration() {
		getConfig().set("announcement.messages", this.messages);
		getConfig().set("announcement.interval", Long.valueOf(this.interval));
		getConfig().set("announcement.prefix", this.prefix);
		getConfig().set("announcement.enabled", Boolean.valueOf(this.enabled));
		getConfig().set("announcement.random", Boolean.valueOf(this.random));
		saveConfig();
	}

	public void reloadConfiguration() {
		reloadConfig();
		this.prefix = getConfig().getString("announcement.prefix", "&c[Announcement] ");
		this.messages = getConfig().getStringList("announcement.messages");
		this.interval = getConfig().getInt("announcement.interval", 1000);
		this.enabled = getConfig().getBoolean("announcement.enabled", true);
		this.random = getConfig().getBoolean("announcement.random", false);
	}

	public String getPrefix() {
		return this.prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
		saveConfig();
	}

	public long getInterval() {
		return this.interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
		saveConfiguration();

		// Restart the task
		task.cancel();
		createTask();
	}

	public void addAnnouncement(String message) {
		this.messages.add(message);
		saveConfiguration();
	}

	public String getAnnouncement(int index) {
		return this.messages.get(index - 1);
	}

	public int numberOfAnnouncements() {
		return this.messages.size();
	}

	public void removeAnnouncements() {
		this.messages.clear();
		saveConfiguration();
	}

	public void removeAnnouncement(int index) {
		this.messages.remove(index - 1);
		saveConfiguration();
	}

	public boolean isAnnouncerEnabled() {
		return this.enabled;
	}

	public void setAnnouncerEnabled(boolean enabled) {
		this.enabled = enabled;
		saveConfiguration();
	}

	public boolean isRandom() {
		return this.random;
	}

	public void setRandom(boolean random) {
		this.random = random;
		saveConfiguration();
	}

	public static Announcer getInstance() {
		return instance;
	}

	public Logger getLogger() {
		return logger;
	}

	public Game getGame() {
		return game;
	}
}