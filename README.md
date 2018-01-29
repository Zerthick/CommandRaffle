# CommandRaffle

A simple plugin to raffle commands.  

CommmandRaffle allows you to set up one-time or repeating raffles of a command on your server.  Players purchase a configurable amount of tickets to enter the raffle.  When the raffle draws a ticket the winning player (if any) will have the command for the raffle executed on their behalf.

## Commands

 * `/raffle` - Displays information on the various commands availalbe (Alias: rf) 
 * `/raffle info` - Display plugin version info (Alias: if)
 * `/raffle list [Raffle]` - Displays all raffles or the participants of `[Raffle]` (Alias: ls)
 * `/raffle me` - Displays all raffles you are currently in
 * `/raffle draw <Raffle>` - Forces `<Raffle>` to immediately draw a winner (Alias: dr)
 * `/raffle cancel <Raffle>` - Cancels `<Raffle>` refunding all bought tickets  (Alias: cn)
 * `/raffle buy <Raffle> <Amount>` - Buys `<Amount>` of tickets in `<Raffle>` (Alias: by)
 * `/raffle create <Raffle> <Command> <Duration>` - Creates a new raffle with name `<Raffle>` reward command `<Command>` and duration `<Duration>` (Alias: cr)
 
## Permissions
 
 | Permission                      | Use                                                             |
 |:--------------------------------|:----------------------------------------------------------------|
 | `commandraffle.command.help`    | Allows the player to run `/raffle`                              |
 | `commandraffle.command.info`    | Allows the player to run `/raffle info`                         |
 | `commandraffle.command.list`    | Allows the player to run `/raffle list`                         |
 | `commandraffle.command.me`      | Allows the player to run `/raffle me`                           |
 | `commandraffle.command.draw`    | Allows the player to run `/raffle draw`                         |
 | `commandraffle.command.cancel`  | Allows the player to run `/raffle cancel`                       |
 | `commandraffle.command.buy`     | Allows the player to run `/raffle buy`                          |
 | `commandraffle.command.create`  | Allows the player to run `/raffle create`                       |
 
 ## Creating Raffles
 
 The `/raffle create` command has several customization options, summarized below:
 
 ### Required Arguments
 
   * `<Raffle>` - This is the name of the raffle which will be displayed in the raffle listing
   * `<Command>` - This is the command that will be executed if the raffle has a winner. Do **not** need to includ the `/` at the begining of the command. In addition you can use `{Winner}` inside the command to get the name of the winning player. Ex: `give {Winner} diamond 5` In the case that the winner is not currenlty online, execution of the command will be delayed until they are online once again.
   * `<Duration>` - The amount of time until the raffle is drawn, it is specified in `DdHhMmSs` format. Ex: `5d4h3m2s` Would be 5 days, 4 hours, 3 mins, 2 seconds. Any of the time sections can be ommitted.
 
### Optional Flags
  * `--repeating` - Will cause the raffle to reset and repeat once a winner is drawn (Alias: `-r`)
  * `--numtickets <Amount>` - Total number of tickets in raffle (Default: 100 tickets) (Alias: `-n`)
  * `--ticketcost <Cost>` - The cost of purchasing one ticket in this raffle (Default: 10.0) (Alias: `-c`)
  * `--ticketlimit <Limit>` - The limit on the number of tickets a player can purchase in the raffle (Default: Unlimited) (Alias: `-l`)
  * `--perm <Node>` - A permission node players must have in order to enter the raffle (Alias: `-p`)
  * `--desc <Description>` - Description of the raffle. Supports [`&` formatting and color codes](http://www.minecraftforum.net/forums/support/server-support/tutorials-and-faqs/1940467-bukkit-colour-codes) (Alias: `-d`)
  
## Configuring CommandRaffle

CommandRaffle will generate a config file located at `~/config/commandraffle/commandraffle.conf` where the default configuration for raffles and the messages displayed can be configured. The default configuration file is shown below:

```
# If raffle winner broadcasts should go to all players or just the participants of the raffle (ticket holders)
BroadcastWinner=false
Messages {
    # If not blank, will broadcast this message to all players when a raffle is created
    CreateBroadcast="&eThe &6{Raffle_Name} &eraffle has been created!"
    # Message sent if a raffle has no winner
    NoWinnerMessage="&eThe &6{Raffle_Name} &eraffle has no winner!"
    # Message sent to announce the winner of a raffle
    WinnerMessage="&eThe winner of the &6{Raffle_Name} &eis &6{Winner}&e!"
}
# Default Raffle Values
RaffleDefaults {
    # The default number of tickets a raffle should have, default: 100
    NumberOfTickets=100
    # The default cost of tickets in a raffle, default: 10.0
    TicketCost=10
}
```

## Support Me
I will **never** charge money for the use of my plugins, however they do require a significant amount of work to maintain and update. If you'd like to show your support and buy me a cup of tea sometime (I don't drink that horrid coffee stuff :P) you can do so [here](https://www.paypal.me/zerthick)
