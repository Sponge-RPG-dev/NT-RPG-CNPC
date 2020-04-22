package ru.glassspirit.cnpcntrpg.sponge;

import cz.neumimto.rpg.sponge.damage.SpongeDamageService;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.api.entity.living.Living;

public class CnpcRpgDamageHandler extends SpongeDamageService.SpongeDamageHandler {

    @Override
    public boolean canDamage(ISpongeCharacter damager, Living damaged) {
        if (damaged instanceof EntityNPCInterface) {
            EntityNPCInterface npc = (EntityNPCInterface) damaged;
            return !npc.faction.isFriendlyToPlayer((EntityPlayer) damager.getPlayer());
        }
        return super.canDamage(damager, damaged);
    }
}
