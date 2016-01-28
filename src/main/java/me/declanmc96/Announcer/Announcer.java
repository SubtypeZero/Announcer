package me.declanmc96.Announcer;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.declanmc96.Announcer.metrics.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import me.thundergemios10.updater.Updater;
import me.thundergemios10.updater.Updater.UpdateResult;
import me.thundergemios10.updater.Updater.UpdateType;

public class Announcer
  extends JavaPlugin
  implements Listener
{
  protected List<String> announcementMessages;
  protected String announcementPrefix;
  protected long announcementInterval;
  protected boolean enabled;
  protected boolean random;
  private AnnouncerThread announcerThread;
  private Logger logger;
  public static boolean update = false;
  public static String name = "";
  public static long size = 0L;
  
  public Announcer()
  {
    this.announcerThread = new AnnouncerThread(this);
  }
  
  public void onEnable()
  {
    this.logger = getServer().getLogger();
    try
    {
      Metrics metrics = new Metrics(this);
      metrics.start();
    }
    catch (IOException localIOException) {}
    if (getConfig().getBoolean("announcement.check-for-updates"))
    {
      Updater updater = new Updater(this, 40864, getFile(), Updater.UpdateType.NO_DOWNLOAD, false);
      update = updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE;
      if ((update) && (getConfig().getBoolean("announcement.auto-update"))) {
        Updater localUpdater1 = new Updater(this, 40864, getFile(), Updater.UpdateType.NO_VERSION_CHECK, true);
      } else if (update) {
        getServer().getLogger().log(Level.WARNING, "There is an update for announcer!");
      }
    }
    if (!new File(getDataFolder(), "config.yml").exists()) {
      saveDefaultConfig();
    }
    reloadConfiguration();
    
    BukkitScheduler scheduler = getServer().getScheduler();
    scheduler.scheduleSyncRepeatingTask(this, this.announcerThread, this.announcementInterval * 20L, this.announcementInterval * 20L);
    
    AnnouncerCommandExecutor announcerCommandExecutor = new AnnouncerCommandExecutor(this);
    getCommand("announce").setExecutor(announcerCommandExecutor);
    getCommand("announcer").setExecutor(announcerCommandExecutor);
    getCommand("acc").setExecutor(announcerCommandExecutor);
    
    this.logger.info(String.format("%s is enabled!\n", new Object[] { getDescription().getFullName() }));
  }
  
  public void onDisable()
  {
    this.logger.info(String.format("%s is disabled!\n", new Object[] { getDescription().getFullName() }));
  }
  
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event)
  {
    Player player = event.getPlayer();
    if ((player.hasPermission("announcer.admin")) && (update))
    {
      player.sendMessage("An update is available: " + name + "(" + size + " bytes");
      player.sendMessage("Type /acc update if you would like to update.");
    }
  }
  
  public void announce()
  {
    this.announcerThread.run();
  }
  
  public void announce(int index)
  {
    announce((String)this.announcementMessages.get(index - 1));
  }
  
  public void announce(String line)
  {
    String[] messages = line.split("&n");
    for (String message : messages) {
      if (message.startsWith("/"))
      {
        getServer().dispatchCommand(getServer().getConsoleSender(), message.substring(1));
      }
      else if (getServer().getOnlinePlayers().size() > 0)
      {
        String messageToSend = ChatColorHelper.replaceColorCodes(String.format("%s%s", new Object[] { this.announcementPrefix, message }));
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
          if (player.hasPermission("announcer.receiver"))
          {
            if (messageToSend.contains("[player]")) {
              messageToSend = messageToSend.replace("[player]", player.getDisplayName());
            }
            if (messageToSend.contains("[online]")) {
              messageToSend = messageToSend.replace("[online]", getServer().getOnlinePlayers().size() + "/" + getServer().getMaxPlayers());
            }
            if (messageToSend.contains("[health]"))
            {
              double hp2 = player.getHealth();
              hp2 /= 2.0D;
              double hp4 = player.getMaxHealth();
              hp4 /= 2.0D;
              messageToSend = messageToSend.replace("[health]", hp2 + "/" + hp4);
            }
            if (!messageToSend.isEmpty()) {
              player.sendMessage(messageToSend);
            }
          }
        }
      }
    }
  }
  
  public void saveConfiguration()
  {
    getConfig().set("announcement.messages", this.announcementMessages);
    getConfig().set("announcement.interval", Long.valueOf(this.announcementInterval));
    getConfig().set("announcement.prefix", this.announcementPrefix);
    getConfig().set("announcement.enabled", Boolean.valueOf(this.enabled));
    getConfig().set("announcement.random", Boolean.valueOf(this.random));
    saveConfig();
  }
  
  public void reloadConfiguration()
  {
    reloadConfig();
    this.announcementPrefix = getConfig().getString("announcement.prefix", "&c[Announcement] ");
    this.announcementMessages = getConfig().getStringList("announcement.messages");
    this.announcementInterval = getConfig().getInt("announcement.interval", 1000);
    this.enabled = getConfig().getBoolean("announcement.enabled", true);
    this.random = getConfig().getBoolean("announcement.random", false);
  }
  
  public String getAnnouncementPrefix()
  {
    return this.announcementPrefix;
  }
  
  public void setAnnouncementPrefix(String announcementPrefix)
  {
    this.announcementPrefix = announcementPrefix;
    saveConfig();
  }
  
  public long getAnnouncementInterval()
  {
    return this.announcementInterval;
  }
  
  public void setAnnouncementInterval(long announcementInterval)
  {
    this.announcementInterval = announcementInterval;
    saveConfiguration();
    
    BukkitScheduler scheduler = getServer().getScheduler();
    scheduler.cancelTasks(this);
    scheduler
      .scheduleSyncRepeatingTask(this, this.announcerThread, announcementInterval * 20L, announcementInterval * 20L);
  }
  
  public void addAnnouncement(String message)
  {
    this.announcementMessages.add(message);
    saveConfiguration();
  }
  
  public String getAnnouncement(int index)
  {
    return (String)this.announcementMessages.get(index - 1);
  }
  
  public int numberOfAnnouncements()
  {
    return this.announcementMessages.size();
  }
  
  public void removeAnnouncements()
  {
    this.announcementMessages.clear();
    saveConfiguration();
  }
  
  public void removeAnnouncement(int index)
  {
    this.announcementMessages.remove(index - 1);
    saveConfiguration();
  }
  
  public boolean isAnnouncerEnabled()
  {
    return this.enabled;
  }
  
  public void setAnnouncerEnabled(boolean enabled)
  {
    this.enabled = enabled;
    saveConfiguration();
  }
  
  public boolean isRandom()
  {
    return this.random;
  }
  
  public void setRandom(boolean random)
  {
    this.random = random;
    saveConfiguration();
  }
  
  public void update()
  {
    Updater updater = new Updater(this, 40864, getFile(), Updater.UpdateType.NO_VERSION_CHECK, true);
  }
}
