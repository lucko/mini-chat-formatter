<h1 align="center">
	<img
		alt="mini-chat-formatter"
		src=".github/assets/banner.png">
</h1>

<h3 align="center">
  A simple chat format plugin for Hytale.
</h3>

## Features

* Supports [MiniMessage](https://docs.papermc.io/adventure/minimessage/format/) formatting tags e.g. `<red>hello!</red>` or `<#ff5555>hello!</#ff5555>`.
* Supports legacy Minecraft-style formatting codes e.g. `&cThis is red text`.
* Supports [PlaceholderAPI](https://placeholderapi.com/downloads/) placeholders e.g. `%player_world%`.
* Supports [LuckPerms](https://luckperms.net) prefixes, suffixes and meta data e.g. `<prefix>` or `<suffix>`.

### Examples

The format is designed to be simple and easy to use.

#### Format: `<yellow><username></yellow>: <message>`
![](.github/assets/examples/example1.png)

#### Format: `<prefix><username><suffix>: <message>`
![](.github/assets/examples/example2.png)

#### Format: `<#aaaaaa>(%player_biome%)</#aaaaaa> <prefix><username><suffix>: <message>`
![](.github/assets/examples/example3.png)

mini-chat-formatter is available to download from:
* [CurseForge](https://www.curseforge.com/hytale/mods/mini-chat-formatter)
* [Jenkins](https://ci.lucko.me/job/mini-chat-formatter/)

## Configuration

There is one configuration property: `"Format"`. This is the format that will be used for all chat messages.

You can use the `/minichatformatter reload` command to apply changes to the format without restarting the server (see the Commands section below for more details).

### Example Formats

| Format                                                   | Example               | Description                                                  |
|----------------------------------------------------------|-----------------------|--------------------------------------------------------------|
| `<username>: <message>`                                  | lucko: hello!         | The default format, matches what Hytale does by default.     |
| `<bold><username></bold>: <message>`                     | **lucko**: hello!     | Give all players a bold username                             |
| `<prefix><username><suffix>: <message>`                  | [ADMIN] lucko: hello! | (LuckPerms) Use prefixes and suffixes.                       |
| `<prefix><username><suffix>: <meta:chat_color><message>` | [ADMIN] lucko: hello! | (LuckPerms) Use prefixes, suffixes and meta.                 |
| `(%player_world%) <username>: <message>`                 | (world) lucko: hello  | (PlaceholderAPI) Show each player's world before their name. |

### Placeholders

#### Builtin Tags

* `<username>` - The player's username.
* `<message>` - The chat message that the player sent.

#### MiniMessage Tags

* `<red>` - Makes the text red.
* `<bold>` - Makes the text bold.
* `<#FF0000>` - Makes the text a custom color (in this case, bright red). 

... and **many** more! See the [MiniMessage documentation](https://docs.papermc.io/adventure/minimessage/format/) for a full list of supported tags.

#### LuckPerms Tags

* `<prefix>` - The player's LuckPerms prefix.
* `<suffix>` - The player's LuckPerms suffix.
* `<meta:key>` - The value of the specified LuckPerms meta key for the player.

The prefix, suffix and meta values can themselves contain MiniMessage tags, so you can use this to give players custom colors or styles based on their LuckPerms data. For example, you could set a player's prefix to `<red>[ADMIN]</red>` to give them a red "[ADMIN]" tag before their name in chat.

#### PlaceholderAPI Placeholders

You can use any PlaceholderAPI placeholder in the `%placeholder_name%` format.

See the [PlaceholderAPI documentation](https://wiki.placeholderapi.com/users/placeholder-list/hytale/) for a full list of available placeholders.

Placeholder values can themselves contain MiniMessage tags. For example, if you have a placeholder that returns `<red>hello</red>`, the word "hello" will be displayed in red in chat.

## Commands
### `/minichatformatter reload`
* Reloads the configuration.
* Useful to apply changes to the format without restarting the server. 
* Requires permission `minichatformatter.reload`.

## Scope

This plugin is intentionally simple and basic.

We will consider feature requests via GitHub, but things we are unlikely to add to mini-chat-formatter include:
* channels (per-world, per-group, etc.)
* multi-server (sending messages across multiple servers)
* private messages
* proximity / chat radius
* custom UIs
* allowing players to set their own chat format (except via placeholders)
* moderation features (profanity filters, etc.)

If you need something different or more advanced, here are some other options:

* [HeroChat](https://github.com/heroslender/HeroChat) - also supports PlaceholderAPI, with various additional features and options
* [EliteEssentials](https://www.curseforge.com/hytale/mods/eliteessentials) or [EssentialsPlus](https://www.curseforge.com/hytale/mods/essentials-plus) - also supports formatting the chat with PlaceholderAPI, plus various other 'essential' functionality
* quite likely many more! Search on CurseForge :)

## License

MIT.
