package me.subtypezero.announcer;

import com.google.inject.Inject;
import me.subtypezero.announcer.command.CommandAnnounce;
import me.subtypezero.announcer.config.ConfigManager;
import me.subtypezero.announcer.config.type.GlobalConfig;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
	private GlobalConfig defaultConfig;

	private List<String> messages;
	private String prefix;
	private int interval;
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

	@Listener
	public void onReload(GameReloadEvent event) {
		reloadConfiguration();
	}

	public void reloadConfiguration() {
		// Load Plugin Config
		pluginConfigManager.load();
		// Reload Tasks
		Sponge.getScheduler().getTasksByName("announcer.message.broadcast").forEach(Task::cancel);
		registerTasks();
		// Reload Commands
		Sponge.getCommandManager().getOwnedBy(this).forEach(Sponge.getCommandManager()::removeMapping);
		CommandAnnounce.clearSubCommands();
		registerCommands();
	}

	private void registerCommands() {
		CommandAnnounce.register();
	}

	private void registerTasks() {
		if (getConfig().getAnnouncementConfig().isEnabled()) {
			Sponge.getScheduler().createTaskBuilder()
					.name("announcer.message.broadcast")
					.execute(this.thread)
					.interval(getConfig().getAnnouncementConfig().getInterval(), TimeUnit.SECONDS)
					.async()
					.submit(this);
		}
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

	public String getPrefix() {
		return this.prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
		getConfig().getAnnouncementConfig().setPrefix(prefix);
		getConfigManager().save();
	}

	public long getInterval() {
		return this.interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
		getConfig().getAnnouncementConfig().setInterval(interval);
		getConfigManager().save();

		// Restart the task
		task.cancel();
		registerTasks();
	}

	public void addAnnouncement(String message) {
		this.messages.add(message);
		getConfig().getAnnouncementConfig().setMessages(messages);
		getConfigManager().save();
	}

	public String getAnnouncement(int index) {
		return this.messages.get(index - 1);
	}

	public int getAnnouncementCount() {
		return this.messages.size();
	}

	public void removeAnnouncements() {
		this.messages.clear();
		getConfigManager().save();
	}

	public void removeAnnouncement(int index) {
		this.messages.remove(index - 1);
		getConfig().getAnnouncementConfig().setMessages(messages);
		getConfigManager().save();
	}

	public boolean isAnnouncerEnabled() {
		return this.enabled;
	}

	public void setAnnouncerEnabled(boolean enabled) {
		this.enabled = enabled;
		getConfig().getAnnouncementConfig().setEnabled(this.enabled);
		getConfigManager().save();
	}

	public boolean isRandom() {
		return this.random;
	}

	public void setRandom(boolean random) {
		this.random = random;
		getConfig().getAnnouncementConfig().setRandom(this.random);
		getConfigManager().save();
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

	public GlobalConfig getConfig() {
		return this.defaultConfig;
	}

	public ConfigManager getConfigManager() {
		return this.pluginConfigManager;
	}
}