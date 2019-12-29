package ru.glassspirit.cnpcntrpg.mixin.impl;

import cz.neumimto.rpg.api.Rpg;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.EventHooks;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.OptionType;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.DialogOption;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.roles.RoleDialog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import ru.glassspirit.cnpcntrpg.mixin.IMixinDialog;
import ru.glassspirit.cnpcntrpg.mixin.IMixinDialogOption;

import javax.script.Invocable;
import javax.script.ScriptEngine;

@Mixin(value = NoppesUtilPlayer.class, remap = false)
public abstract class MixinNoppesUtilPlayer {

    /**
     * @author GlassSpirit 06.12.2019
     * @reason Process scripted options of dialogs and temporary dialogs
     */
    @Overwrite
    public static void dialogSelected(int diaId, int optionId, EntityPlayerMP player, EntityNPCInterface npc) {
        PlayerData data = PlayerData.get(player);
        if (data.dialogId == diaId) {
            if (data.dialogId < 0 && npc.advanced.role == RoleType.DIALOG) {
                String text = ((RoleDialog) npc.roleInterface).optionsTexts.get(optionId);
                if (text != null && !text.isEmpty()) {
                    Dialog d = new Dialog(null);
                    d.text = text;
                    NoppesUtilServer.openDialog(player, npc, d);
                }
            } else {
                Dialog dialog = DialogController.instance.dialogs.get(data.dialogId);
                if (dialog == null && IMixinDialog.playerTemporaryDialogMap.containsKey(player.getUniqueID()))
                    dialog = IMixinDialog.playerTemporaryDialogMap.get(player.getUniqueID());

                if (dialog != null) {
                    if (!dialog.hasDialogs(player) && !dialog.hasOtherOptions()) {
                        NoppesUtilPlayer.closeDialog(player, npc, true);
                    } else {
                        DialogOption option = dialog.options.get(optionId);
                        if (option != null && !EventHooks.onNPCDialogOption(npc, player, dialog, option) && (option.optionType != OptionType.DIALOG_OPTION || option.isAvailable(player) && option.hasDialog()) && option.optionType != OptionType.DISABLED && option.optionType != OptionType.QUIT_OPTION) {
                            if (option.optionType == OptionType.ROLE_OPTION) {
                                NoppesUtilPlayer.closeDialog(player, npc, true);
                                if (npc.roleInterface != null) {
                                    if (npc.advanced.role == RoleType.COMPANION) {
                                        ((RoleCompanion) npc.roleInterface).interact(player, true);
                                    } else {
                                        npc.roleInterface.interact(player);
                                    }
                                }
                            } else if (option.optionType == OptionType.DIALOG_OPTION) {
                                NoppesUtilPlayer.closeDialog(player, npc, false);
                                NoppesUtilServer.openDialog(player, npc, option.getDialog());
                            } else if (option.optionType == OptionType.COMMAND_BLOCK) {
                                NoppesUtilPlayer.closeDialog(player, npc, true);
                                NoppesUtilServer.runCommand(npc, npc.getName(), option.command, player);
                            } else if (option.optionType == 5 /* Script option */) {
                                try {
                                    ScriptEngine engine = Rpg.get().getScriptEngine().getEngine();
                                    IMixinDialogOption optionScripted = (IMixinDialogOption) option;
                                    if (optionScripted.getScript() instanceof String) {
                                        optionScripted.setScript(engine.eval((String) optionScripted.getScript()));
                                    }
                                    engine.eval("var runScriptOptionFunc = function(option, player, npc) {\n" +
                                            "    return option.script(player, npc);\n" +
                                            "}");
                                    Invocable i = (Invocable) engine;
                                    i.invokeFunction("runScriptOptionFunc", option, NpcAPI.Instance().getIEntity(player));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                IMixinDialog.playerTemporaryDialogMap.remove(player.getUniqueID());
                            } else {
                                NoppesUtilPlayer.closeDialog(player, npc, true);
                            }

                        } else {
                            NoppesUtilPlayer.closeDialog(player, npc, true);
                        }
                    }
                }
            }
        }
    }
}
