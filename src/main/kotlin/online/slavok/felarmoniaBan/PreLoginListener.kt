package online.slavok.felarmoniaBan

import net.kyori.adventure.text.minimessage.MiniMessage
import online.slavok.felarmoniaBan.database.WhitelistDatabase
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent

class PreLoginListener (
    private val database: WhitelistDatabase
) : Listener {
    @EventHandler(priority = EventPriority.HIGH)
    fun onAsyncPlayerPreLogin(event: AsyncPlayerPreLoginEvent) {
        if (event.loginResult != AsyncPlayerPreLoginEvent.Result.ALLOWED) return
        if (database.isBannedByNickname(event.name)) {
            event.disallow(
                AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                MiniMessage.miniMessage().deserialize(
                    "<newline>" +
                            "<red>Вы были заблокированы по причине:</red> <dark_red>${database.getReasonByNickname(event.name)}</dark_red><newline>" +
                            "<red>Вы можете подать апелляцию на нашем дискорд сервере</red>"
                )
            )
            return
        }
        if (database.isBannedByIp(event.address.hostAddress)) {
            event.disallow(
                AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                MiniMessage.miniMessage().deserialize(
                    "<newline>" +
                            "<red>Вы были заблокированы по причине:</red> <dark_red>${database.getReasonByIp(event.address.hostAddress)}</dark_red><newline>" +
                            "<red>Вы можете подать апелляцию на нашем дискорд сервере</red>"
                )
            )
            return
        }
    }
}