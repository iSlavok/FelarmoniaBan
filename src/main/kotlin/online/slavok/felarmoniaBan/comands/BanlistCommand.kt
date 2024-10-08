package online.slavok.felarmoniaBan.comands

import net.kyori.adventure.text.minimessage.MiniMessage
import online.slavok.felarmoniaBan.database.WhitelistDatabase
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class BanlistCommand (
    private val database: WhitelistDatabase,
    private val prefix: String,
) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        val list = database.getBanlist()
        if (list.isEmpty()) {
            sender.sendMessage(
                MiniMessage.miniMessage()
                    .deserialize(
                        "$prefix <red>Список заблокированных пуст."
                    )
            )
            return true
        }
        sender.sendMessage(
            MiniMessage.miniMessage()
                .deserialize(
                    "$prefix <green>Список заблокированных:"
                )
        )
        list.forEach {
            sender.sendMessage(
                MiniMessage.miniMessage()
                    .deserialize(
                        "$prefix <white>${it[0]}</white> <gray>- ${it[1]}</gray>"
                    )
            )
        }
        return true
    }
}