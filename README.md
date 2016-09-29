## ExpugnExtras
ExpugnExtras is a minecraft plugin that adds various features to enhance the experience
on the server "Talabrek's Ultimate Minecraft Network". Code is written to fit with the plugins
installed there so there may be compatability errors if attempted to be used in another server.

### Dependencies
These plugins are required to be on your server to use ExpugnExtras to the fullest extent.

####Warps:
  * Soft depends on 'VariableTriggers' so players can run commands through ClickTriggers 
    or WalkTriggers using @CMDOP. Warp commands are not properly permissioned

####Timers:
  * Soft depends on 'VariableTriggers' so people can run commands through ClickTriggers
    or WalkTriggers using @CMDOP. Timer commands are not properly permissioned.

####Time Trials:
  * Soft depends on 'VariableTriggers' so people can run commands through ClickTriggers
    or WalkTriggers using @CMDOP. TimeTrial commands are not properly permissioned.

####Title List:
  * Depends on 'Titles' and 'PermissionsEx' to run.

### Features and Commands
The following are features and the commands associated to them to the plugin.

####/expugn [args]
  * Warps
    - A simple warp system.
    - A cooldown or daily limit can be applied to these warps.
  * Timers
    - Once set, players can check how long it will be until the desired timer runs out.
  * Time Trials
    - Players race from a set point to a set end.
    - Features a Top 5 leaderboard and personal best times for everyone.
  * Title List
    - An extremely improved title list built from the original /title.
    - Players can click the chat to apply whatever title they desire in their list
    - Provides a sample of how it will look on the player in a tooltip
  * Item Drop
    - Drop scatter random items in an item set
    - Useful for drop parties or as a random reward distributor to a group of players
  * /expugn name [playername]
    - Give someone a compliment!
    - Using /expugn name [playername] will generate a random phrase that a player will say if they click on the [Click Me.]
    - Words are stored in extras.yml (See resources for a default configuration file)
    - "[playername] is a [adverb] [adjective] [noun]"
    - Moved from ExpugnFree (**_Removed as of v4.0_**)
  * Rock, Paper, Scissors
    - Simple Rock, Paper, Scissors game.
    - Moved from ExpugnFree (**_Removed as of v4.0_**)
  * ~~ExpugnCash~~
    - Stock Market Simulator.
    - **_Removed as of v5.0_**

####~~/marriage [args]~~
  * Marriage
    - A roleplay dating simulator.
    - Two players can start a relationship and get benefits as they advance in their relationship.
    - Players can see how long they have been together
  * **_Removed as of v5.0_**

####~~/expugnfree [args]~~
  * /expugn name [playername]
    - Give someone a compliment!
    - Using /expugn name [playername] will generate a random phrase that a player will say if they click on the [Click Me.]
    - Words are stored in extras.yml (See resources for a default configuration file)
    - "[playername] is a [adverb] [adjective] [noun]"
  * Rock, Paper, Scissors
    - Simple Rock, Paper, Scissors game.
  * **_Removed as of v4.0_**
 
####~~/gollem [args]~~
  * Personal project, please ignore.
  * **_Removed as of v4.0_**
 
####~~/expugnconsole [args]~~
  * A command that only the console can run.
  * **_Removed as of v4.0_**

### Permissions
  * expugnextras.admin | Permission to use '/expugn', ~~'/expugnconsole'~~, ~~'/gollem'~~
  * ~~marriage.admin | Permission to use '/marriage dev'~~ **_Unused as of v5.0_**
