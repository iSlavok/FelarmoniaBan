package online.slavok.felarmoniaBan

import online.slavok.felarmoniaBan.comands.BanCommand
import online.slavok.felarmoniaBan.comands.BanlistCommand
import online.slavok.felarmoniaBan.comands.UnbanCommand
import online.slavok.felarmoniaBan.database.AuthDatabase
import online.slavok.felarmoniaBan.database.WhitelistDatabase
import org.bukkit.plugin.java.JavaPlugin

class  FelarmoniaBan : JavaPlugin() {
    private val prefix = config.getString("prefix")
    private val banPrefix = "$prefix ${config.getString("ban-prefix")}"
    private val unbanPrefix = "$prefix ${config.getString("unban-prefix")}"
    private val banlistPrefix = "$prefix ${config.getString("banlist-prefix")}"

    override fun onEnable() {
        saveDefaultConfig()
        val whitelistDatabaseUrl = config.getString("whitelist-mysql-url") ?: return
        val authDatabaseUrl = config.getString("auth-mysql-url") ?: return
        val botToken = config.getString("bot-token") ?: return
        val whitelistDatabase = WhitelistDatabase(whitelistDatabaseUrl)
        val authDatabase = AuthDatabase(authDatabaseUrl)
        val bot = Bot(botToken)
        getCommand("fban")?.setExecutor(BanCommand(bot, whitelistDatabase, authDatabase, banPrefix))
        getCommand("funban")?.setExecutor(UnbanCommand(bot, whitelistDatabase, unbanPrefix))
        getCommand("fbanlist")?.setExecutor(BanlistCommand(whitelistDatabase, banlistPrefix))
        server.pluginManager.registerEvents(PreLoginListener(whitelistDatabase), this)
    }

    override fun onDisable() {

    }
}
