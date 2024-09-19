package online.slavok.felarmoniaBan

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection

class Database (
    private val mysqlUrl: String
) {
    private val dataSource = createHikariDataSource()

    private fun createHikariDataSource(): HikariDataSource {
        val config = HikariConfig()
        config.jdbcUrl = mysqlUrl
        config.maximumPoolSize = 10
        config.connectionTimeout = 5000
        return HikariDataSource(config)
    }

    private fun getConnection(): Connection {
        return dataSource.connection
    }

    fun getDiscordId(nickname: String): String? {
        return getConnection().use { connection ->
            connection.prepareStatement("SELECT discord_id FROM whitelist WHERE nickname = ?").use { ps ->
                ps.setString(1, nickname)
                ps.executeQuery().use { rs ->
                    if (rs.next()) {
                        rs.getString("discord_id")
                    } else {
                        null
                    }
                }
            }
        }
    }

    fun ban(nickname: String, reason: String) {
        getConnection().use { connection ->
            connection.prepareStatement("INSERT INTO banlist (nickname, reason) VALUES (?, ?);").use { ps ->
                ps.setString(1, nickname)
                ps.setString(2, reason)
                ps.execute()
            }
        }
    }

    fun unban(nickname: String) {
        getConnection().use { connection ->
            connection.prepareStatement("DELETE FROM banlist WHERE nickname = ?;").use { ps ->
                ps.setString(1, nickname)
                ps.execute()
            }
        }
    }

    fun isBanned(nickname: String): Boolean {
        return getConnection().use { connection ->
            connection.prepareStatement("SELECT * FROM banlist WHERE nickname = ?;").use { ps ->
                ps.setString(1, nickname)
                ps.executeQuery().use { rs ->
                    rs.next()
                }
            }
        }
    }

    fun getReason(nickname: String): String? {
        return getConnection().use { connection ->
            connection.prepareStatement("SELECT reason FROM banlist WHERE nickname = ?").use { ps ->
                ps.setString(1, nickname)
                ps.executeQuery().use { rs ->
                    if (rs.next()) {
                        rs.getString("reason")
                    } else {
                        null
                    }
                }
            }
        }
    }
}