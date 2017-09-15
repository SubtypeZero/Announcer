# Announcer
Deckerz's Bukkit Plugin, ported to sponge by SubtypeZero

### Screenshot
![Screenhot](https://i.imgur.com/KUkzTr7.png)

### Configuration
```
Announcement {
    # The list of announcements to broadcast
    Messages=[
        "Use /acc for information about configuring this plugin.",
        "Created by Deckerz, ported to Sponge by SubtypeZero"
    ]
    # Whether announcements should be enabled or disabled.
    enabled=false
    # The time interval (in seconds) between announcements.
    interval=60
    # The prefix to display while broadcasting announcements.
    prefix="&8[&cInfo&8]&6 "
    # Whether announcements should be random or sequential.
    random=false
}
Config-Version=1
```

### Permissions
[Permissions](https://github.com/SubtypeZero/Announcer/blob/master/src/main/java/me/subtypezero/announcer/command/Permissions.java)