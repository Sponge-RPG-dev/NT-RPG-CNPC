package ru.glassspirit.cnpcntrpg.sponge;

import cz.neumimto.rpg.api.Rpg;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.RayTraceResult;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.IPermission;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.common.entity.EntityUtil;
import ru.glassspirit.cnpcntrpg.forge.DataNpcRpg;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.SimpleBindings;

public class Commands {

    Commands() {
        CommandSpec dataCommand = CommandSpec.builder()
                .description(Text.of("Command to modify additional data of npc"))
                .permission(CustomNpcsPermissions.NPC_GUI.name)
                .executor((src, args) -> {
                    if (!(src instanceof Player)) {
                        return CommandResult.empty();
                    }
                    EntityPlayerMP player = (EntityPlayerMP) src;
                    if (!(player.getHeldItemMainhand().getItem() instanceof IPermission)) {
                        src.sendMessage(Text.of("You must have 'Scripter' item in hand"));
                        return CommandResult.empty();
                    }

                    RayTraceResult result = EntityUtil.rayTraceFromEntity(player, 10D, 0, true);
                    if (result.entityHit instanceof EntityNPCInterface) {
                        EntityNPCInterface npc = (EntityNPCInterface) result.entityHit;
                        NoppesUtilServer.setEditingNpc(player, npc);
                        DataNpcRpg.playersEditingRpgData.put(player.getUniqueID(), npc.getUniqueID());
                        Server.sendData(player, EnumPacketClient.GUI, EnumGuiType.Script.ordinal(), 0, 0, 0);
                    } else {
                        src.sendMessage(Text.of("No npc found!"));
                    }
                    return CommandResult.empty();
                })
                .build();

        CommandSpec npcCommand = CommandSpec.builder()
                .description(Text.of("Main CNPC-RPG command"))
                .child(dataCommand, "data")
                .build();

        Sponge.getCommandManager().register(CnpcRpgSponge.instance, npcCommand, "npc");

        CommandSpec jsCommand = CommandSpec.builder()
                .description(Text.of("Execute some js code"))
                .permission("ntrpg.admin")
                .arguments(GenericArguments.remainingRawJoinedStrings(Text.of("script")))
                .executor((src, args) -> {
                    String script = (String) args.getOne("script").get();
                    try {
                        Bindings engineBindings = Rpg.get().getScriptEngine().getEngine().getBindings(ScriptContext.ENGINE_SCOPE);
                        Bindings tempBindings = new SimpleBindings();
                        tempBindings.putAll(engineBindings);
                        if (src instanceof Player) {
                            tempBindings.put("player", src);
                        }
                        Object result = Rpg.get().getScriptEngine().getEngine().eval(script, tempBindings);
                        src.sendMessage(Text.of(TextColors.RED, "[NT-RPG-JS]: ", TextColors.RESET, result != null ? result : "success"));
                    } catch (Exception e) {
                        src.sendMessage(Text.of(TextColors.RED, "[NT-RPG-JS]: ", e.getMessage()));
                        e.printStackTrace();
                    }
                    return CommandResult.empty();
                })
                .build();
        Sponge.getCommandManager().register(CnpcRpgSponge.instance, jsCommand, "js");
    }

}
