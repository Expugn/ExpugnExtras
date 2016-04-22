## ExpugnExtras
ExpugnExtras is a minecraft plugin that adds various features to enhance the experience
on the server "Talabrek's Ultimate Minecraft Network". Code is written to fit with the plugins
installed there so there may be compatability errors if attempted to be used in another server.

### Dependencies
These plugins are required to be on your server to use ExpugnExtras to the fullest extent.

####Warps:
  * No dependencies to run
  * Soft depends on 'VariableTriggers' so players can run commands through ClickTriggers 
    or WalkTriggers using @CMDOP. Warp commands are not properly permissioned

####Timers:
  * No dependencies to run
  * Soft depends on 'VariableTriggers' so people can run commands through ClickTriggers
    or WalkTriggers using @CMDOP. Timer commands are not properly permissioned.

####Time Trials:
  * No dependencies to run
  * Soft depends on 'VariableTriggers' so people can run commands through ClickTriggers
    or WalkTriggers using @CMDOP. TimeTrial commands are not properly permissioned.

####Title List:
  * Depends on 'TitleManager' and 'PermissionsEx' to run.
  * No soft dependencies.

####Marriage:
  * Depends on 'Vault' to run.
  * No soft dependencies.
  
####ItemDrop:
  * No dependencies to run.
  * No soft dependencies.
  
####expugn name:
  * No dependencies to run.
  * No soft dependencies.

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
    - Moved from ExpugnFree (Removed as of v4.0)
  * Rock, Paper, Scissors
    - Simple Rock, Paper, Scissors game.
    - Moved from ExpugnFree (Removed as of v4.0)

####/marriage [args]
  * Marriage
    - A roleplay dating simulator.
    - Two players can start a relationship and get benefits as they advance in their relationship.
    - Players can see how long they have been together

####/expugnfree [args]
  * Removed as of v4.0
 
####/gollem [args]
  * Removed as of v4.0
 
####/expugnconsole [args]
  * Removed as of v4.0

### Permissions
  * expugnextras.admin | Permission to use '/expugn', '/expugnconsole', '/gollem'
  * marriage.admin | Permission to use '/marriage dev'
