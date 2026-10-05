# Surf Moderation Tools

**Surf Moderation Tools** is a Minecraft plugin designed to help supporters and moderators work more efficiently. It provides quick administrative and support functions directly in-game.

## ⚡ Commands

### Player Management

- **`/rotate <Player>`**  
  🔄 Rotate a player without teleporting them.  
  - Permission: `surf.moderation.tools.command.rotation`

- **`/freeze <Player> <time<s,m,h,d,w>>`**  
  ❄️ Freeze a player for a specific duration.  
  - Permission: `surf.moderation.tools.command.freeze`

- **`/unfreeze <Player>`**  
  ☀️ Unfreeze a player.  
  - Permission: `surf.moderation.tools.command.unfreeze`
  
- **`/freezelist`**  
  📋 View a list of currently frozen players.  
  - Permission: `surf.moderation.tools.command.freeze`

- **`/pingPlayer <Player> <WithMessage>`**  
  📍 Ping a player with an auditory indicator and an optional message.  
  - Permission: `surf.moderation.tools.command.pingPlayer`

- **`/faq send <FAQ> [Player]`**  
  📄 Send the answer to a frequently asked question.  
  - Permission: `surf.moderation.tools.command.faq.send`
  - If no player is specified, the FAQ is broadcast to everyone on the server.
  - If a player is specified, only that player receives the FAQ **and gets pinged** with a notification sound.
  - The command expects the FAQ **key** (for example `rulebook`, `how-to-open-ticket`). Use tab-completion to see all available keys.

- **`/faq info <FAQ>`**  
  Show an FAQ only to yourself.
  - Permission: `surf.moderation.tools.command.faq.send`

- **`/openplayerinpanel`**  
Open a player's profile in the Panel for quick access to moderation tools.
  - Permission: `surf.moderation.tools.command.openplayerinpanel`

### Admin

- **`/surfmodtools setMessageCooldown <time(ms)>`**  
  ⚙️ Set the FAQ message cooldown.

- **`/surfmodtools reload`**  
  ⚙️ Reload the plugin config and the FAQs without restarting the server.

## ❓ FAQ System

FAQs are no longer stored in the plugin config. They live in the database of the
[surf-discord](https://github.com/SLNE-Development/surf-discord) microservice and are shared with Discord.

- Each FAQ has a key, a question, a long text (Discord) and a **short text (Minecraft)**. The plugin shows the short text; links in it are clickable.
- Only FAQs with `active_minecraft = true` are available in-game.
- The plugin requests the FAQs from the microservice via Redis and caches them:
  - on server start,
  - on `/surfmodtools reload`,
  - automatically when the microservice reports changed FAQs (checked about once per minute) or when the microservice starts.
- If the microservice cannot be reached, the plugin keeps the FAQs it cached last.
- Every successful load is also written to `plugins/surf-moderation-tools/faq-cache.json`. If the server starts
  while the microservice is down, the FAQs are loaded from this file instead.
- Every `/faq send` reports a usage to the microservice, which stores it in `faq_usage` with source `MINECRAFT`
  (flushed every 30 seconds). `/faq info` is not counted.
- FAQs are created and edited in the database, not in-game.

The Redis message classes in `faq/redis/FaqRedisMessages.kt` exist in both repositories. surf-redis identifies
them by their fully-qualified class name, so they must keep the package `dev.slne.surf.moderation.tools.faq.redis`
and identical fields on both sides. Change both copies together.

## 🛠 Installation

1. Download the latest release from GitHub Releases.  
2. Ensure the following plugins are installed on the server:
   - Surf API (`surf-paper-api`)
   - Surf Bitmap Provider (`surf-bitmap-provider-paper`)
   - Surf Redis (`surf-redis-paper`) – **required**, the plugin will not start without it
3. Make sure **Java 25** is installed.  
4. Place the plugin in your server's `plugins` folder and restart the server.

## 🚀 Deployment Checklist (FAQ via microservice)

1. **Deploy the microservice first:** build and deploy the surf-discord version that contains the Minecraft FAQ
   request handler (`RedisRequestHandler.handleMinecraftFaqsRequest`).
2. **Same Redis:** the Minecraft server and the microservice must use the **same Redis instance**. The connection
   is configured in surf-redis / surf-api, not in this plugin.
3. **Install the plugin:** put `surf-moderation-tools-<version>-all.jar` into `plugins/` next to `surf-redis-paper`.
4. **Clean up the config:** remove the old `faqs:` block from `plugins/surf-moderation-tools/config.yml`; only
   `faqCooldown` is still used.
5. **Restart the server** and check the log for:
   ```
   Loaded X FAQs from the FAQ microservice.
   ```
   If it says `Failed to load FAQs from the FAQ microservice`, the microservice is not running or not connected
   to the same Redis. Fix that and run `/surfmodtools reload`.
6. **Test in-game:** `/faq info <key>`, `/faq send <key>`, `/faq send <key> <player>`.
7. **Test live updates:** change `active_minecraft` or a short text of an FAQ in the database. Within about a
   minute the server should reload the FAQs on its own.

## ⚙️ Configuration

- `faqCooldown` (ms): prevents multiple supporters from sending the same FAQ at the same time.

## 📜 License

This project is licensed under the **GNU General Public License v3.0 (GPL-3.0)**.

---

NOT AN OFFICIAL MINECRAFT PRODUCT. NOT APPROVED BY OR ASSOCIATED WITH MOJANG OR MICROSOFT.
