package online.slavok.felarmoniaBan

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player

class Bot (
    token: String,
    private val instance: FelarmoniaBan
) {
    private val jda: JDA = JDABuilder.createDefault(token).build()

    fun banSendMessage(discordId: String, moderatorDiscordId: String, moderator: Player, reason: String) {
        jda.retrieveUserById(discordId).queue { user: User ->
            user.openPrivateChannel().queue { privateChannel: PrivateChannel ->
                val message = MessageCreateBuilder()
                    .addEmbeds(
                        MessageEmbed(
                            null,
                            "Вы заблокированы",
                            "Вы были заблокированы на сервере Felarmonia\n" +
                                    "Вы можете подать апелляцию [тут](https://discord.com/channels/1278106662267523072/1279423015259476061/1279464801294356523)",
                            null,
                            null,
                            0xFF0000,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            listOf(
                                MessageEmbed.Field(
                                    "Модератор",
                                    "<@$moderatorDiscordId> (${moderator.name})",
                                    false
                                ),
                                MessageEmbed.Field(
                                    "Причина",
                                    reason,
                                    false
                                )
                            ),
                        )
                    )
                    .build()
                try {
                    privateChannel.sendMessage(message).queue()
                    moderator.sendMessage(
                        Component.text(
                            "Сообщение в лс игроку успешно отправлено",
                            NamedTextColor.GREEN
                        )
                    )
                } catch (ex: Exception) {
                    moderator.sendMessage(
                        Component.text(
                            "Не удалось отправить сообщение в лс игроку",
                            NamedTextColor.RED
                        )
                    )
                    instance.logger.warning("Не удалось отправить сообщение в лс игроку $discordId,\n $ex")
                }
            }
        }
    }

    fun unbanSendMessage(discordId: String, moderator: Player) {
        jda.retrieveUserById(discordId).queue { user: User ->
            user.openPrivateChannel().queue { privateChannel: PrivateChannel ->
                val message = MessageCreateBuilder()
                    .addEmbeds(
                        MessageEmbed(
                            null,
                            "Вы разблокированы",
                            "Вы были разблокированы на сервере Felarmonia",
                            null,
                            null,
                            0x00FF00,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null
                        )
                    )
                    .build()
                try {
                    privateChannel.sendMessage(message).queue()
                    moderator.sendMessage(
                        Component.text(
                            "Сообщение в лс игроку успешно отправлено",
                            NamedTextColor.GREEN
                        )
                    )
                } catch (ex: Exception) {
                    moderator.sendMessage(
                        Component.text(
                            "Не удалось отправить сообщение в лс игроку",
                            NamedTextColor.RED
                        )
                    )
                    instance.logger.warning("Не удалось отправить сообщение в лс игроку $discordId,\n $ex")
                }
            }
        }
    }

    fun removePlayerRole(discordId: String, moderator: Player) {
        val guild = jda.getGuildById("1278106662267523072")
        val member = guild?.getMemberById(discordId)
        val role = guild?.getRoleById("1278308833479102505")
        if (member != null && role != null) {
            try {
                guild.removeRoleFromMember(member, role).queue()
                moderator.sendMessage(
                    Component.text(
                        "Роль у игрока успешно снята",
                        NamedTextColor.GREEN
                    )
                )
            } catch (ex: Exception) {
                moderator.sendMessage(
                    Component.text(
                        "Не удалось снять роль у игрока",
                        NamedTextColor.RED
                    )
                )
                instance.logger.warning("Не удалось снять роль у игрока $discordId,\n $ex")
            }
        }
    }

    fun addPlayerRole(discordId: String, moderator: Player) {
        val guild = jda.getGuildById("1278106662267523072")
        val member = guild?.getMemberById(discordId)
        val role = guild?.getRoleById("1278308833479102505")
        if (member != null && role != null) {
            try {
                guild.addRoleToMember(member, role).queue()
                moderator.sendMessage(
                    Component.text(
                        "Роль игроку успешно добавлена",
                        NamedTextColor.GREEN
                    )
                )
            } catch (ex: Exception) {
                moderator.sendMessage(
                    Component.text(
                        "Не удалось добавить роль игроку",
                        NamedTextColor.RED
                    )
                )
                instance.logger.warning("Не удалось добавить роль игроку $discordId,\n $ex")
            }
        }
    }
}