package online.slavok.felarmoniaBan.comands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import online.slavok.felarmoniaBan.Bot
import online.slavok.felarmoniaBan.Database
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class BanCommand (
    private val bot: Bot,
    private val database: Database,
) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender !is Player) return true
        if (args != null) {
            if (args.size < 2) {
                sender.sendMessage(Component.text("Неверные аргументы", NamedTextColor.RED))
                return true
            }
        } else {
            sender.sendMessage(Component.text("Неверные аргументы", NamedTextColor.RED))
            return true
        }
        val nickname = args[0]
        val reason = args.slice(1 until args.size).joinToString(" ")
        if (database.isBanned(nickname)) {
            sender.sendMessage(Component.text("Игрок уже заблокирован.", NamedTextColor.RED))
            return true
        }
        val discordId = database.getDiscordId(nickname)
        val moderatorDiscordId = database.getDiscordId(sender.name)
        if (discordId != null && moderatorDiscordId != null) {
            bot.banSendMessage(discordId, moderatorDiscordId.toString(), sender, reason)
            bot.removePlayerRole(discordId, sender)
        } else {
            sender.sendMessage(Component.text("Не удалось получить дискорд игрока или модератора", NamedTextColor.RED))
        }
        database.ban(nickname, reason)
        Bukkit.getOnlinePlayers().find { it.name == nickname }?.kick(Component.text("Вы были заблокированы по причине $reason", NamedTextColor.RED))
        sender.sendMessage(Component.text("Игрок заблокирован", NamedTextColor.GREEN))
        return true
    }
}