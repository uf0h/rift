# rift
###### [getting started](#getting-started) | [configuration](#configuration) | [commands](#commands)
> Rift is a [fast](#fast) and simple cross-server queue system for [BungeeCord](https://github.com/SpigotMC/BungeeCord) using [Redis](https://redis.io/).

:warning:  Rift currently only supports [LuckPerms](https://github.com/lucko/LuckPerms) as the leading and most featureful permissions plugin.

### Current Features
- Synchronized cross-server queues.
- Hub player load balancing.
- Whitelisting.

### Structure
##### `bungee`: 
> A [BungeeCord](https://github.com/SpigotMC/BungeeCord) plugin; Manages communication between `hub` and `destination` servers.
##### `destination`:  
> A [Bukkit](https://www.spigotmc.org/) plugin; Communicates with `bungee` and `hub` servers.
> <br>A destination server refers to each server where you want your players to be sent. 
##### `hub`: 
> A [Bukkit](https://www.spigotmc.org/) plugin; Communicates with `bungee` and `destination` servers.
> <br>A hub server refers to each hub server you have on your BungeeCord proxy.

### Getting Started
Clone and cd into rift directory
```console
git clone https://github.com/uf0h/rift.git && cd rift
```

Build rift
```console
mvn clean package install
```

Outputs jars are in `target/` respectively.

#### Configuration:
`bungee`:
```yaml
redis:
  host: "localhost"
  port: 6379
  auth:
    enabled: false
    password: "none"

messages:
  queue-position:
    - "&7You are in position &a%pos%&8/&a%total% &7in the queue for %server%&7."
    - "&7Purchase a &d&nrank&7 on our store for a &a&nhighter&7 priority."
  queue-paused: "&7The queue for %server% &7is currently paused."
```

`destination`:
```yaml
redis:
  host: "localhost"
  port: 6379
  auth:
    enabled: false
    password: "none"

server-name: "factions-fire"
```

`hub`:
```yaml
redis:
  host: "localhost"
  port: 6379
  auth:
    enabled: false
    password: "none"

server-name: "hub-1"
```

#### Queue Priorities
Rift uses permission based queue priorities.

`rift.priority.<number-between-0-and-100>`: 100 being the highest priority and 0 being the lowest.

`rift.bypass`: To bypass the queue and directly get sent to the server.

#### Commands

##### Bungee:
`/brift toggle <queue>`: Stops the queue from sending players, but still allows players to join the queue.

`/brift (set)displayname <queue> <name>`: Set and view the display name of the queue for chat messages.

`/bwhitelist`: Replica of bukkit whitelist command.

##### Destinations:
`/hub`: Sends player to the least populated hub.

`/stop`: Sends all players to the least populated hubs then proceeds to stop the server.

`/rift huball`: Sends all players to the least populated hubs.

##### Hubs:
`/joinqueue <queue>`: Join a queue.

`/leavequeue`: Leave current queue.

### Contact
If you need help reach out on Discord (ufo#9531).
