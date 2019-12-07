package ru.glassspirit.cnpcntrpg.mixin;

import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public interface IMixinDialog {

    Map<UUID, Dialog> playerTemporaryDialogMap = new HashMap<>();

    boolean isScripted();

    void processScripts(EntityPlayer player, EntityNPCInterface npc);

}
