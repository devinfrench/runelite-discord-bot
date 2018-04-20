# RuneLite Discord Bot
A bot for [RuneLite's](https://github.com/runelite) discord.
## Chat Commands
``!gh <id>`` links the GitHub pull request or issue associated with the specified id.

``!issue <keywords>`` searches the title of open issues and links the most relevant issue.

``!add <command> <response>`` adds a custom chat command that replies with the specified response.

``!del <command>`` deletes the specified custom chat command.

``!help`` lists all custom chat commands.

## How To Use
``java -jar discord-1.0-SNAPSHOT-shaded.jar <discord_bot_token> <github_oauth_token> <twitch_client_id>``