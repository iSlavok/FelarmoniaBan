package online.slavok.felarmoniaBan.comands

import net.kyori.adventure.text.minimessage.MiniMessage
import online.slavok.felarmoniaBan.Bot
import online.slavok.felarmoniaBan.database.AuthDatabase
import online.slavok.felarmoniaBan.database.WhitelistDatabase
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class BanCommand (
    private val bot: Bot,
    private val whitelistDatabase: WhitelistDatabase,
    private val authDatabase: AuthDatabase,
    private val prefix: String,
) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender !is Player) return true
        if (args != null) {
            if (args.size < 2) {
                sender.sendMessage(
                    MiniMessage.miniMessage()
                    .deserialize(
                        "$prefix <red>Неверные аргументы"
                    )
                )
                return true
            }
        } else {
            sender.sendMessage(
                MiniMessage.miniMessage()
                    .deserialize(
                        "$prefix <red>Неверные аргументы"
                    )
            )
            return true
        }
        val nickname = args[0]
        val reason = args.slice(1 until args.size).joinToString(" ")
        if (whitelistDatabase.isBannedByNickname(nickname)) {
            sender.sendMessage(
                MiniMessage.miniMessage()
                    .deserialize(
                        "$prefix <red>Игрок <dark_red>$nickname</dark_red> уже заблокирован."
                    )
            )
            return true
        }
        val discordId = whitelistDatabase.getDiscordId(nickname)
        val moderatorDiscordId = whitelistDatabase.getDiscordId(sender.name)
        if (discordId != null && moderatorDiscordId != null) {
            bot.sendMessage(discordId, bot.generateBanMessage(moderatorDiscordId, sender.name, reason), sender, nickname, prefix)
            bot.removePlayerRole(discordId, sender, prefix, nickname)
        } else {
            sender.sendMessage(
                MiniMessage.miniMessage()
                    .deserialize(
                        "$prefix <red>Не удалось получить дискорд игрока или модератора"
                    )
            )
        }
        val ip = authDatabase.getIp(nickname)
        if (ip != null) {
            whitelistDatabase.ban(nickname, reason, ip)
        } else {
            whitelistDatabase.ban(nickname, reason, "")
            sender.sendMessage(
                MiniMessage.miniMessage()
                    .deserialize(
                        "$prefix <red>Не удалось получить ip игрока <dark_red>$nickname</dark_red>"
                    )
            )
        }
        Bukkit.getOnlinePlayers().find {
            it.name == nickname
        }?.kick(
            MiniMessage.miniMessage()
                .deserialize(
                    "<red>Вы были заблокированы по причине $reason"
                )
        )
        sender.sendMessage(
            MiniMessage.miniMessage()
                .deserialize(
                    "$prefix <green>Игрок <dark_green>$nickname</dark_green> заблокирован по причине <dark_green>$reason</dark_green>"
                )
        )
        bot.sendLogMessage(discordId, moderatorDiscordId, nickname, sender.name, true, reason)
        return true
    }
}