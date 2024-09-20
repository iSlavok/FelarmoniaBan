package online.slavok.felarmoniaBan.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection

class WhitelistDatabase (
    private val mysqlUrl: String
) {
    private val dataSource = createHikariDataSource()

    private fun createHikariDataSource(): HikariDataSource {
        val config = HikariConfig()
        config.jdbcUrl = mysqlUrl
        config.maximumPoolSize = 10
        config.connectionTimeout = 5000
        config.maxLifetime = 60000
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

    fun ban(nickname: String, reason: String, ip: String) {
        getConnection().use { connection ->
            connection.prepareStatement("INSERT INTO banlist (nickname, reason, ip) VALUES (?, ?, ?);").use { ps ->
                ps.setString(1, nickname)
                ps.setString(2, reason)
                ps.setString(3, ip)
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

    fun isBannedByNickname(nickname: String): Boolean {
        return getConnection().use { connection ->
            connection.prepareStatement("SELECT * FROM banlist WHERE nickname = ?;").use { ps ->
                ps.setString(1, nickname)
                ps.executeQuery().use { rs ->
                    rs.next()
                }
            }
        }
    }

    fun isBannedByIp(ip: String): Boolean {
        return getConnection().use { connection ->
            connection.prepareStatement("SELECT * FROM banlist WHERE ip = ?;").use { ps ->
                ps.setString(1, ip)
                ps.executeQuery().use { rs ->
                    rs.next()
                }
            }
        }
    }

    fun getReasonByNickname(nickname: String): String? {
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

    fun getReasonByIp(ip: String): String? {
        return getConnection().use { connection ->
            connection.prepareStatement("SELECT reason FROM banlist WHERE ip = ?").use { ps ->
                ps.setString(1, ip)
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