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
  
####expugnfree name:
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

####/marriage [args]
  * Marriage
    - A roleplay dating simulator.
    - Two players can start a relationship and get benefits as they advance in their relationship.
    - Players can see how long they have been together

####/expugnfree [args]
  * A command with no permissions attached.
  * /expugnfree name [playername]
    - Give someone a compliment!
    - Using /expugnfree name [playername] will generate a random phrase that a player will say if they click on the [Click Me.]
    - Words are stored in extras.yml (See resources for a default configuration file)
    - "[playername] is a [adverb] [adjective] [noun]"
  * /expugnfree listtitles
    - A branch from /expugn listtitles. They're the same thing but /expugnfree has no permissions.
  * /expugnfree settime
    - A branch from /expugn settime. They're the same thing but /expugnfree has no permission.
 
####/expugnconsole [args]
  * A command that only the console can use.
  * /expugnconsole runitemdrop [itemset_name] [count]
    - A branch from /expugn runitemdrop [itemset_name] [count]. They're the same thing but /expugnconsole is console only and /expugn is player only.

### Permissions
  * expugnextras.admin | Permission to use '/expugn', '/expugnconsole', '/gollem'
  * marriage.admin | Permission to use '/marriage dev'
