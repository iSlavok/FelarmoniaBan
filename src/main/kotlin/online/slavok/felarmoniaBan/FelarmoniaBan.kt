package online.slavok.felarmoniaBan

import online.slavok.felarmoniaBan.comands.BanCommand
import online.slavok.felarmoniaBan.comands.UnbanCommand
import org.bukkit.plugin.java.JavaPlugin

class  FelarmoniaBan : JavaPlugin() {

    override fun onEnable() {
        saveDefaultConfig()
        val mysqlUrl = config.getString("mysql-url") ?: return
        val botToken = config.getString("bot-token") ?: return
        val database = Database(mysqlUrl)
        val bot = Bot(botToken, this)
        getCommand("fban")?.setExecutor(BanCommand(bot, database))
        getCommand("funban")?.setExecutor(UnbanCommand(bot, database))
        server.pluginManager.registerEvents(PreLoginListener(database), this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
