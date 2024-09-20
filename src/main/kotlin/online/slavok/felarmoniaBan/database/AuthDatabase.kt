package online.slavok.felarmoniaBan.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection

class AuthDatabase (
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

    fun getIp(nickname: String): String? {
        return getConnection().use { connection ->
            connection.prepareStatement("SELECT ip FROM auth WHERE nickname = ?").use { ps ->
                ps.setString(1, nickname)
                ps.executeQuery().use { rs ->
                    if (rs.next()) {
                        rs.getString("ip")
                    } else {
                        null
                    }
                }
            }
        }

    }
}