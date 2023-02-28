# TownyPlus

[![Build Status](https://img.shields.io/github/workflow/status/BrycensRanch/TownyPlus/Build/master?style=for-the-badge)](../../actions?query=workflow%3ABuild)
[![GitHub release (latest SemVer including pre-releases)](https://img.shields.io/github/v/release/BrycensRanch/TownyPlus?include_prereleases&label=release&style=for-the-badge)](../../releases)
![Modrinth Game Versions](https://img.shields.io/modrinth/game-versions/townyplus?style=for-the-badge)
![Modrinth Downloads](https://img.shields.io/modrinth/dt/townyplus?color=GREEN&label=MODRINTH%20DOWNLOADS&style=for-the-badge)

[![bstats Servers](https://img.shields.io/bstats/servers/14161?style=for-the-badge)](https://bstats.org/plugin/bukkit/TownyPlus/14161)
![Polymart Downloads](https://img.shields.io/polymart/downloads/2057?color=GREEN&label=POLYMART%20DOWNLOADS&style=for-the-badge)
![Polymart Version](https://img.shields.io/polymart/version/2057?style=for-the-badge)
![Polymart Rating](https://img.shields.io/polymart/rating/2057?label=POLYMART%20RATINGS&style=for-the-badge)

[//]: # ([![Spiget Downloads]&#40;https://img.shields.io/spiget/downloads/79903?style=for-the-badge&#41;]&#40;https://www.spigotmc.org/resources/splugintemplate.79903/&#41;)

[//]: # ([![Spiget Rating]&#40;https://img.shields.io/spiget/rating/79903?style=for-the-badge&#41;]&#40;https://www.spigotmc.org/resources/splugintemplate.79903/&#41;)
[![codecov](https://img.shields.io/codecov/c/github/BrycensRanch/TownyPlus?style=for-the-badge)](https://codecov.io/gh/BrycensRanch/TownyPlus)
[![Commitizen friendly](https://img.shields.io/badge/commitizen-friendly-brightgreen.svg?style=for-the-badge)](https://commitizen.github.io/cz-cli/)
[![semantic-release](https://img.shields.io/badge/%20%20%F0%9F%93%A6%F0%9F%9A%80-semantic--release-e10079.svg?style=for-the-badge)](https://github.com/semantic-release/semantic-release)
[![Support Server](https://img.shields.io/discord/816686637849378857?logo=Easy%20SMP&style=for-the-badge)](https://discord.gg/cX89RdaF32)

[//]: # (> View the plugin on [SpigotMC]&#40;https://github.com/Silthus/minecraft-server-template&#41;, [PolymartMC]&#40;&#41;, [Bukkit]&#40;&#41;)

## Features

TownyPlus was built to be new and modern. All these features here are meant to show that. 😎

* **Bridagier** Support, which means autocomplete for [Geyser](https://geysermc.org) players 🎮
* Attempts to send DiscordSRV messages to MC and vice versa (VERY EXPERIMENTAL) 📝
* Provides a Javalin (Powered by Jetty, I can't turn off their stupid logging) powered REST API for other plugins to use 📡
* [VentureChat](https://www.spigotmc.org/resources/venturechat.771/) support for listening for messages with
  ProtocolLib (WORK IN PROGRESS)
* [TownyChat](https://github.com/TownyAdvanced/TownyChat) support for listening for messages, no extra dependencies needed (WORK IN PROGRESS)
* [Update checker](https://github.com/JEFF-Media-GbR/Spigot-UpdateChecker) that is beautiful
* Lot of this plugin is WIP, but, I need to get it out there so I can get feedback and help from the community!
* Metrics so you can track how many other amazing people are using this plugin
* Automatic updates to Bukkit via GitHub Actions, never miss an update again
* Automatic updates to Polymart via GitHub Actions, never miss an update again

This plugin utilizes bstats plugin metrics system. the following information is collected and sent to bstats.org unless opted out:

- A unique identifier
- The server's version of Java
- Whether the server is in offline or online mode
- Plugin's version
- Server's version
- OS version/name and architecture
- core count for the CPU
- number of players online
- Metrics version
- Which Chat, Economy, and Permission hook is in use.

## Dump 
This plugin has a dump command that will generate a dump with all the information needed to help you with your issue.
To use this command, simply run `/townyplus dump` and it will generate a file in the `plugins/TownyPlus/dumps` folder. Uploading the dump online currently isn't supported, yet.

## Support

Help and Support:
If you need help with TownyPlus, there are multiple ways to contact me. If you wish to stay on SpigotMC, you can post a
message on the discussion page or send me a direct message. You can also post an issue on the GitHub page for this
project. However, The fastest way to get support is through my Discord server. You can join by clicking the image below:
[![Support Server](https://raw.githubusercontent.com/BrycensRanch/TownyPlus/master/assets/discordbanner.png)](https://discord.com/invite/Yx755gU)

### Extra Information

Please do not use a review to leave bug reports or errors. I am not able to help you through a review. If you need help
please use the contact methods provided above. I know that won't stop some of you though.