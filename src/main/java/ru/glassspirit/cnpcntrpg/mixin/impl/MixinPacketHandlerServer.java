package ru.glassspirit.cnpcntrpg.mixin.impl;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.PacketHandlerServer;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.controllers.LinkedNpcController;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.glassspirit.cnpcntrpg.forge.DataNpcRpg;

@Mixin(value = PacketHandlerServer.class, remap = false)
public class MixinPacketHandlerServer {

    @Inject(method = "handlePacket", at = @At(value = "INVOKE", target = "Lnoppes/npcs/entity/data/DataScript;writeToNBT" +
            "(Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/nbt/NBTTagCompound;"), cancellable = true)
    private void onScriptDataGet(EnumPacketServer type, ByteBuf buffer, EntityPlayerMP player, EntityNPCInterface npc, CallbackInfo ci) {
        if (DataNpcRpg.playersEditingRpgData.containsKey(player.getUniqueID())) {
            ci.cancel();
            DataNpcRpg data = new DataNpcRpg(npc, true);
            NBTTagCompound compound = data.writeToNBT(new NBTTagCompound());

            Server.sendData(player, EnumPacketClient.GUI_DATA, compound);
            NoppesUtilServer.setEditingNpc(player, npc);
        }
    }

    @Inject(method = "handlePacket", at = @At(value = "FIELD", target = "Lnoppes/npcs/entity/EntityNPCInterface;" +
            "script:Lnoppes/npcs/entity/data/DataScript;", opcode = Opcodes.GETFIELD, ordinal = 0), cancellable = true)
    private void onScriptDataSave(EnumPacketServer type, ByteBuf buffer, EntityPlayerMP player, EntityNPCInterface npc, CallbackInfo ci) {
        if (DataNpcRpg.playersEditingRpgData.containsKey(player.getUniqueID())) {
            ci.cancel();
            NoppesUtilServer.setEditingNpc(player, null);
            DataNpcRpg.playersEditingRpgData.remove(player.getUniqueID());

            DataNpcRpg data = new DataNpcRpg(npc, false);
            try {
                data.readFromNBT(Server.readNBT(buffer));
                data.apply();

                npc.reset();
                if (npc.linkedData != null) {
                    LinkedNpcController.Instance.saveNpcData(npc);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
