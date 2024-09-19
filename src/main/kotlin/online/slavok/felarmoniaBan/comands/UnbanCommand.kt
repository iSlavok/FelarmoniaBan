package online.slavok.felarmoniaBan.comands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import online.slavok.felarmoniaBan.Bot
import online.slavok.felarmoniaBan.Database
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class UnbanCommand (
    private val bot: Bot,
    private val database: Database,
) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender !is Player) return true
        if (args != null) {
            if (args.size != 1) {
                sender.sendMessage(Component.text("Неверные аргументы", NamedTextColor.RED))
                return true
            }
        } else {
            sender.sendMessage(Component.text("Неверные аргументы", NamedTextColor.RED))
            return true
        }
        val nickname = args[0]
        if (!database.isBanned(nickname)) {
            sender.sendMessage(Component.text("Игрок не заблокирован.", NamedTextColor.RED))
            return true
        }
        val discordId = database.getDiscordId(nickname)
        if (discordId != null) {
            bot.unbanSendMessage(discordId, sender)
            bot.addPlayerRole(discordId, sender)
        } else {
            sender.sendMessage(Component.text("Не удалось получить дискорд игрока или модератора", NamedTextColor.RED))
        }
        database.unban(nickname)
        sender.sendMessage(Component.text("Игрок разблокирован", NamedTextColor.GREEN))
        return true
    }
}