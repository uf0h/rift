package me.ufo.rift.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import com.google.common.io.ByteStreams;
import io.lettuce.core.RedisURI;
import me.ufo.rift.Rift;
import me.ufo.rift.queues.RiftQueue;
import me.ufo.rift.util.FastUUID;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class RiftConfig {

  private final Rift plugin;
  private final Configuration config;
  private final RedisURI credentials;

  // Messages
  private final String queuePosition;
  private final String queuePaused;
  private final BaseComponent[] whitelisted;

  public RiftConfig(final Rift plugin) throws IOException {
    this.plugin = plugin;

    if (!plugin.getDataFolder().exists()) {
      plugin.getDataFolder().mkdir();
    }

    final File file = new File(plugin.getDataFolder(), "config.yml");
    if (!file.exists()) {
      file.createNewFile();
      try (final InputStream in = plugin.getResourceAsStream("config.yml");
           final OutputStream out = new FileOutputStream(file)) {

        ByteStreams.copy(in, out);
      }
    }

    this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);

    // TODO: cleanup
    final Configuration queueSection = this.config.getSection("queues");
    final Collection<String> queues = queueSection.getKeys();
    if (!queues.isEmpty()) {
      for (final String queue : queues) {
        final RiftQueue riftQueue = new RiftQueue(queue);
        final boolean queuing = this.config.getBoolean("queues." + queue + ".queuing", true);
        final String displayName = this.config.getString("queues." + queue + ".display-name");
        if (displayName != null) {
          riftQueue.setDisplayName(displayName);
        }

        riftQueue.setQueuing(queuing);

        if (plugin.debug()) {
          plugin.info("New queue created for: {" + queue + "}");
        }
      }
    }

    this.credentials = new RedisURI(
      this.config.getString("redis.host"),
      this.config.getInt("redis.port"),
      Duration.ofSeconds(30)
    );

    if (this.config.getBoolean("redis.auth.enabled")) {
      this.credentials.setPassword(this.config.getString("redis.auth.password"));
    }

    this.queuePosition = this.config.getStringList("messages.queue-position")
      .stream().map(s -> ChatColor.translateAlternateColorCodes('&', s))
      .collect(Collectors.joining("\n"));

    this.queuePaused =
      ChatColor.translateAlternateColorCodes('&',
                                             this.config.getString("messages.queue-paused"));

    final ComponentBuilder whitelist = new ComponentBuilder();

    final List<String> message = config.getStringList("whitelist.message");

    for (final String s : message) {
      whitelist.append(ChatColor.translateAlternateColorCodes('&', s));
    }

    this.whitelisted = whitelist.create();
  }

  public boolean isWhitelisted() {
    return config.getBoolean("whitelist.enabled", false);
  }

  public Map<UUID, String> getWhitelistedPlayers() {
    final Configuration section = config.getSection("whitelist.players");

    if (section == null) {
      return Collections.emptyMap();
    }

    final Map<UUID, String> out = new HashMap<>();
    for (final String name : section.getKeys()) {
      out.put(FastUUID.fromString(section.getString(name)), name);
    }

    return out;
  }

  public void saveWhitelistedPlayers() {
    final Map<UUID, String> players = this.plugin.getWhitelistedPlayers();

    config.set("whitelist.players", null);
    for (final Map.Entry<UUID, String> entry : players.entrySet()) {
      config.set("whitelist.players." + entry.getValue(), FastUUID.toString(entry.getKey()));
    }

    try {
      ConfigurationProvider.getProvider(YamlConfiguration.class).save(
        this.config,
        new File(this.plugin.getDataFolder(), "config.yml")
      );
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void setWhitelisted(final boolean whitelisted) {
    config.set("whitelist.enabled", whitelisted);

    try {
      ConfigurationProvider.getProvider(YamlConfiguration.class).save(
        this.config,
        new File(this.plugin.getDataFolder(), "config.yml")
      );
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public BaseComponent[] getWhitelistedMessage() {
    return whitelisted;
  }

  public String getQueuePaused() {
    return this.queuePaused;
  }

  public String getQueuePosition() {
    return this.queuePosition;
  }

  public void saveQueue(final RiftQueue queue) {
    final Configuration queueSection = this.config.getSection("queues");

    this.config.set("queues." + queue.getName() + ".queuing", queue.isQueuing());
    this.config.set("queues." + queue.getName() + ".display-name", queue.getDisplayName());

    try {
      ConfigurationProvider.getProvider(YamlConfiguration.class).save(
        this.config,
        new File(this.plugin.getDataFolder(), "config.yml")
      );
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public RedisURI credentials() {
    return this.credentials;
  }

}
