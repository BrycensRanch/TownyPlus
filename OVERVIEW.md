
<h1 id="townyplus" align="center">TownyPlus</h1>
<p align="center"><a href="https://github.com/BrycensRanch/TownyPlus/actions/workflows/gradle.yml?query=workflow%3ABuild"><img src="https://img.shields.io/github/actions/workflow/status/BrycensRanch/TownyPlus/gradle.yml?event=push&amp;style=for-the-badge" alt="Build Status"></a>
<a href="https://github.com/BrycensRanch/TownyPlus/releases/latest"><img src="https://img.shields.io/github/v/release/BrycensRanch/TownyPlus?include_prereleases&amp;label=release&amp;style=for-the-badge" alt="GitHub release (latest SemVer including pre-releases)"></a>
<img src="https://img.shields.io/modrinth/game-versions/townyplus?style=for-the-badge" alt="Modrinth Game Versions">
<img src="https://img.shields.io/modrinth/dt/townyplus?color=GREEN&amp;label=MODRINTH%20DOWNLOADS&amp;style=for-the-badge" alt="Modrinth Downloads"></p>
<p align="center"><a href="https://bstats.org/plugin/bukkit/TownyPlus/14161"><img src="https://img.shields.io/bstats/servers/14161?style=for-the-badge" alt="bstats Servers"></a>
<img src="https://img.shields.io/polymart/downloads/2057?color=GREEN&amp;label=POLYMART%20DOWNLOADS&amp;style=for-the-badge" alt="Polymart Downloads">
<img src="https://img.shields.io/polymart/version/2057?style=for-the-badge" alt="Polymart Version">
<img src="https://img.shields.io/polymart/rating/2057?label=POLYMART%20RATINGS&amp;style=for-the-badge" alt="Polymart Rating">
<a href="https://beta.curseforge.com/minecraft/bukkit-plugins/townyoverloaded"><img src="https://cf.way2muchnoise.eu/title/832361.svg?badge_style=for_the_badge" alt="CurseForge Project"></a>
<a href="https://beta.curseforge.com/minecraft/bukkit-plugins/townyoverloaded"><img src="https://cf.way2muchnoise.eu/832361.svg?badge_style=for_the_badge" alt="CurseForge Downloads"></a></p>
<p align="center"><a href="https://www.spigotmc.org/resources/townyplus.108295/"><img src="https://img.shields.io/spiget/downloads/108295?style=for-the-badge&amp;label=Spigot+Downloads" alt="Spiget Downloads"></a><a href="https://www.spigotmc.org/resources/townyplus.108295/"><img src="https://img.shields.io/spiget/rating/108295?style=for-the-badge&amp;label=Spigot+Rating" alt="Spiget Rating"></a>
<a href="https://codecov.io/gh/BrycensRanch/TownyPlus"><img src="https://img.shields.io/codecov/c/github/BrycensRanch/TownyPlus?style=for-the-badge" alt="codecov"></a>
<a href="https://commitizen.github.io/cz-cli/"><img src="https://img.shields.io/badge/commitizen-friendly-brightgreen.svg?style=for-the-badge" alt="Commitizen friendly"></a>
<a href="https://github.com/semantic-release/semantic-release"><img src="https://img.shields.io/badge/%20%20%F0%9F%93%A6%F0%9F%9A%80-semantic--release-e10079.svg?style=for-the-badge" alt="semantic-release"></a>
<a href="https://discord.gg/cX89RdaF32"><img src="https://img.shields.io/discord/557529166644510731?logo=discord&amp;style=for-the-badge" alt="Support Server"></a></p>


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