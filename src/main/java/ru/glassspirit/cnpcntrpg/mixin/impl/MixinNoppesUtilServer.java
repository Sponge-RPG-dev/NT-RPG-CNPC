package ru.glassspirit.cnpcntrpg.mixin.impl;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.EventHooks;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.PlayerQuestController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerDialogData;
import noppes.npcs.entity.EntityDialogNpc;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import ru.glassspirit.cnpcntrpg.mixin.IMixinDialog;

@Mixin(value = NoppesUtilServer.class, remap = false)
public abstract class MixinNoppesUtilServer {

    /**
     * @author GlassSpirit 05.12.2019
     * @reason If dialog contains scripts, process it and send with NBT
     */
    @Overwrite
    public static void openDialog(EntityPlayer player, EntityNPCInterface npc, Dialog originalDialog) {
        Dialog dialog = originalDialog.copy(player);
        PlayerData playerdata = PlayerData.get(player);
        if (EventHooks.onNPCDialog(npc, player, dialog)) {
            playerdata.dialogId = -1;
        } else {
            playerdata.dialogId = dialog.id;
            if (!(npc instanceof EntityDialogNpc) && dialog.id >= 0) {
                IMixinDialog mixinDialog = (IMixinDialog) dialog;
                if (mixinDialog.isScripted()) {
                    dialog.hideNPC = true;
                    mixinDialog.processScripts(player, npc);
                    Server.sendData((EntityPlayerMP) player, EnumPacketClient.DIALOG_DUMMY, npc.getName(), dialog.writeToNBT(new NBTTagCompound()));
                } else Server.sendData((EntityPlayerMP) player, EnumPacketClient.DIALOG, npc.getEntityId(), dialog.id);
            } else {
                dialog.hideNPC = true;
                IMixinDialog.playerTemporaryDialogMap.put(player.getUniqueID(), dialog);
                Server.sendData((EntityPlayerMP) player, EnumPacketClient.DIALOG_DUMMY, npc.getName(), dialog.writeToNBT(new NBTTagCompound()));
            }

            dialog.factionOptions.addPoints(player);
            if (dialog.hasQuest()) {
                PlayerQuestController.addActiveQuest(dialog.getQuest(), player);
            }

            if (!dialog.command.isEmpty()) {
                NoppesUtilServer.runCommand(npc, npc.getName(), dialog.command, player);
            }

            if (dialog.mail.isValid()) {
                PlayerDataController.instance.addPlayerMessage(player.getServer(), player.getName(), dialog.mail);
            }

            PlayerDialogData data = playerdata.dialogData;
            if (!data.dialogsRead.contains(dialog.id) && dialog.id >= 0) {
                data.dialogsRead.add(dialog.id);
                playerdata.updateClient = true;
            }

            NoppesUtilServer.setEditingNpc(player, npc);
        }
    }

}
