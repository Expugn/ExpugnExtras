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
####/marriage [args]
  * Marriage
    - A roleplay dating simulator.
    - Two players can start a relationship and get benefits as they advance in their relationship.
    - Players can see how long they have been together
####/expugnfree [args]
  * No personal features associated.
    - /expugnfree is a command that isnt restricted with permissions.
    - It currently has Title List.
    - May be removed in the future.
### Permissions
  * expugnextras.admin | Permission to use '/expugn'
  * marriage.admin | Permission to use '/marriage dev'
