package online.slavok.felarmoniaBan

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player
import java.util.function.Consumer

class Bot (
    token: String
) {
    private val jda: JDA = JDABuilder.createDefault(token).build()
    val unbanMessage = MessageCreateBuilder().addEmbeds(
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
    ).build()

    fun generateBanMessage(moderatorDiscordId: String, moderatorNickname: String, reason: String): MessageCreateData {
        return MessageCreateBuilder().addEmbeds(
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
                        "<@$moderatorDiscordId> ($moderatorNickname)",
                        false
                    ),
                    MessageEmbed.Field(
                        "Причина",
                        reason,
                        false
                    )
                ),
            )
        ).build()
    }

    fun sendMessage(discordId: String, message: MessageCreateData, moderator: Player, playerNickname: String, prefix: String) {
        jda.retrieveUserById(discordId).queue { user: User ->
            user.openPrivateChannel().queue { privateChannel: PrivateChannel ->
                val callback = Consumer<Message> { _ ->
                    moderator.sendMessage(
                        MiniMessage.miniMessage()
                            .deserialize(
                                "$prefix <green>Сообщение в лс игроку <dark_green>$playerNickname</dark_green> успешно отправлено."
                            )
                    )
                }
                val exception = Consumer<Throwable> { _ ->
                    moderator.sendMessage(
                        MiniMessage.miniMessage()
                            .deserialize(
                                "$prefix <red>Не удалось отправить сообщение в лс игроку <dark_red>$playerNickname</dark_red>."
                            )
                    )
                }
                privateChannel.sendMessage(message).queue(callback, exception)
            }
        }
    }

    fun removePlayerRole(discordId: String, moderator: Player, prefix: String, playerNickname: String) {
        val guild = jda.getGuildById("1278106662267523072")!!
        val role = guild.getRoleById("1278308833479102505")!!
        val callback = Consumer<Member> { member ->
            val callback = Consumer<Void> { _ ->
                moderator.sendMessage(
                    MiniMessage.miniMessage()
                        .deserialize(
                            "$prefix <green>Роль у игрока <dark_green>$playerNickname</dark_green> успешно снята."
                        )
                )
            }
            val exception = Consumer<Throwable> { _ ->
                moderator.sendMessage(
                    MiniMessage.miniMessage()
                        .deserialize(
                            "$prefix <red>Не удалось снять роль у игрока <dark_red>$playerNickname</dark_red>."
                        )
                )
            }
            guild.removeRoleFromMember(member, role).queue(callback, exception)
        }
        val exception = Consumer<Throwable> { _ ->
            moderator.sendMessage(
                MiniMessage.miniMessage()
                    .deserialize(
                        "$prefix <red>Не удалось снять роль у игрока <dark_red>$playerNickname</dark_red> (Возможно его нет на дискорд сервере)."
                    )
            )
        }
        guild.retrieveMemberById(discordId).queue(callback, exception)
    }

    fun addPlayerRole(discordId: String, moderator: Player, prefix: String, playerNickname: String) {
        val guild = jda.getGuildById("1278106662267523072")!!
        val role = guild.getRoleById("1278308833479102505")!!
        val callback = Consumer<Member> { member ->
            val callback = Consumer<Void> { _ ->
                moderator.sendMessage(
                    MiniMessage.miniMessage()
                        .deserialize(
                            "$prefix <green>Роль игроку <dark_green>$playerNickname</dark_green> успешно добавлена."
                        )
                )
            }
            val exception = Consumer<Throwable> { _ ->
                moderator.sendMessage(
                    MiniMessage.miniMessage()
                        .deserialize(
                            "$prefix <red>Не удалось добавить роль игроку <dark_red>$playerNickname</dark_red>."
                        )
                )
            }
            guild.addRoleToMember(member, role).queue(callback, exception)
        }
        val exception = Consumer<Throwable> { _ ->
            moderator.sendMessage(
                MiniMessage.miniMessage()
                    .deserialize(
                        "$prefix <red>Не удалось добавить роль игроку <dark_red>$playerNickname</dark_red> (Возможно его нет на дискорд сервере)."
                    )
            )
        }
        guild.retrieveMemberById(discordId).queue(callback, exception)
    }
}