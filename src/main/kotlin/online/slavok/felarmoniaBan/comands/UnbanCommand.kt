package online.slavok.felarmoniaBan.comands

import net.kyori.adventure.text.minimessage.MiniMessage
import online.slavok.felarmoniaBan.Bot
import online.slavok.felarmoniaBan.database.WhitelistDatabase
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class UnbanCommand (
    private val bot: Bot,
    private val database: WhitelistDatabase,
    private val prefix: String,
) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender !is Player) return true
        if (args != null) {
            if (args.size != 1) {
                sender.sendMessage(
                    MiniMessage.miniMessage()
                        .deserialize(
                            "$prefix <red>Неверные аргументы."
                        )
                )
                return true
            }
        } else {
            sender.sendMessage(
                MiniMessage.miniMessage()
                    .deserialize(
                        "$prefix <red>Неверные аргументы."
                    )
            )
            return true
        }
        val nickname = args[0]
        if (!database.isBannedByNickname(nickname)) {
            sender.sendMessage(
                MiniMessage.miniMessage()
                    .deserialize(
                        "$prefix <red>Игрок <dark_red>$nickname</dark_red> не заблокирован."
                    )
            )
            return true
        }
        val discordId = database.getDiscordId(nickname)
        val moderatorDiscordId = database.getDiscordId(sender.name)
        if (discordId != null) {
            bot.sendMessage(discordId, bot.unbanMessage, sender, nickname, prefix)
            bot.addPlayerRole(discordId, sender, prefix, nickname)
        } else {
            sender.sendMessage(
                MiniMessage.miniMessage()
                    .deserialize(
                        "$prefix <red>Не удалось получить дискорд игрока или модератора."
                    )
            )
        }
        database.unban(nickname)
        sender.sendMessage(
            MiniMessage.miniMessage()
                .deserialize(
                    "$prefix <green>Игрок <dark_green>$nickname</dark_green> разблокирован."
                )
        )
        bot.sendLogMessage(discordId, moderatorDiscordId, nickname, sender.name, true)
        return true
    }
}