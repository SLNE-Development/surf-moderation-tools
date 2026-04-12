package dev.slne.surf.moderation.tools.commands

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.slne.surf.moderation.tools.util.PermissionRegistry

fun surfModerationToolsCommand() = commandAPICommand("surfmodtools") {
    withPermission(PermissionRegistry.COMMAND_SURF_MOD_TOOLS)

    setMessageCooldownCommand()
    surfModToolsReloadCommand()
}