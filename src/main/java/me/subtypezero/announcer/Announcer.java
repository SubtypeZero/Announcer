package me.subtypezero.announcer;

import com.google.inject.Inject;
import me.subtypezero.announcer.command.CommandAnnounce;
import me.subtypezero.announcer.command.Permissions;
import me.subtypezero.announcer.config.ConfigManager;
import me.subtypezero.announcer.config.type.AnnouncementConfig;
import me.subtypezero.announcer.config.type.GlobalConfig;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Plugin(id = PluginInfo.ID, name = PluginInfo.NAME, version = PluginInfo.VERSION, description = PluginInfo.DESCRIPTION, authors = PluginInfo.AUTHORS)
public class Announcer {
	private static Announcer instance;

	@Inject
	private Logger logger;

	@Inject
	private Game game;

	@Inject
	@ConfigDir(sharedRoot = false)
	private Path configDir;

	@Inject
	@DefaultConfig(sharedRoot = false)
	private ConfigurationLoader<CommentedConfigurationNode> configLoader;
	private ConfigManager configManager;
	private GlobalConfig defaultConfig;
	private AnnouncementConfig config;

	@Listener
	public void onGameConstruction(GameConstructionEvent event) {
		instance = this;
	}

	@Listener
	public void onPreInitialization(GamePreInitializationEvent event) {
		defaultConfig = new GlobalConfig();
		configManager = new ConfigManager(configLoader);
		config = defaultConfig.getAnnouncementConfig();
		configManager.save();
	}

	@Listener
	public void onServerStart(GameStartedServerEvent event) {
		instance = this;
		registerTasks();
		registerCommands();
	}

	@Listener
	public void onReload(GameReloadEvent event) {
		reloadConfiguration();
	}

	public void reloadConfiguration() {
		// Load Plugin Config
		configManager = new ConfigManager(configLoader);
		config = defaultConfig.getAnnouncementConfig();
		// Reload Tasks
		cancelTasks();
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
		if (config.isEnabled()) {
			Sponge.getScheduler().createTaskBuilder()
					.name("announcer.message.broadcast")
					.execute(new AnnouncerThread(this))
					.delay(config.getInterval(), TimeUnit.SECONDS)
					.interval(config.getInterval(), TimeUnit.SECONDS)
					.async()
					.submit(this);
		}
	}

	private void cancelTasks() {
		Sponge.getScheduler().getTasksByName("announcer.message.broadcast").forEach(Task::cancel);
	}

	public void announce(int index) {
		announce(config.getMessages().get(index - 1));
	}

	public void announce(String line) {
		String[] messages = line.split("&n");

		for (String message : messages) {
			if (message.startsWith("/")) {
				game.getCommandManager().process(game.getServer().getConsole(), message.substring(1));
			} else if (game.getServer().getOnlinePlayers().size() > 0) {
				Text messageToSend = toText(String.format("%s%s", getPrefix(), message));
				for (Player player : game.getServer().getOnlinePlayers()) {
					if (player.hasPermission(Permissions.MESSAGE_RECEIVER)) {
						player.sendMessage(messageToSend);
					}
				}
			}
		}
	}

	private Text toText(String str) {
		return TextSerializers.FORMATTING_CODE.deserialize(str);
	}

	public String getPrefix() {
		return config.getPrefix();
	}

	public void setPrefix(String prefix) {
		config.setPrefix(prefix);
		configManager.save();
	}

	public int getInterval() {
		return config.getInterval();
	}

	public void setInterval(int interval) {
		config.setInterval(interval);
		configManager.save();
		cancelTasks();
		registerTasks();
	}

	public void addAnnouncement(String message) {
		List<String> messages = config.getMessages();
		messages.add(message);
		config.setMessages(messages);
		configManager.save();
	}

	public String getMessage(int index) {
		return config.getMessages().get(index);
	}

	public List<String> getMessages() {
		return config.getMessages();
	}

	public void clearMessages() {
		config.setMessages(new ArrayList<>());
		configManager.save();
	}

	public void removeMessage(int index) {
		List<String> messages = config.getMessages();
		messages.remove(index);
		config.setMessages(messages);
		configManager.save();
	}

	public boolean isAnnouncerEnabled() {
		return config.isEnabled();
	}

	public void setAnnouncerEnabled(boolean enabled) {
		config.setEnabled(enabled);
		configManager.save();
		cancelTasks();
		registerTasks();
	}

	public boolean isRandom() {
		return config.isRandom();
	}

	public void setRandom(boolean random) {
		config.setRandom(random);
		configManager.save();
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
		return this.configManager;
	}
}