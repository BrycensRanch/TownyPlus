# Commands

## About
All commands are ran asynchronusly by default, meaning they will not freeze the server while they are running. This is done by using the Bukkit scheduler to run the commands in a seperate thread. This is done to prevent lag spikes when running commands that take a long time to complete.

## Command Structure
All commands are ran using the following structure:
```
/townyplus <command> <arguments>
```