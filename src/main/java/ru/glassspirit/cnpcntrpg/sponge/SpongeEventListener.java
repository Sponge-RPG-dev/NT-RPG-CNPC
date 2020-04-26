package ru.glassspirit.cnpcntrpg.sponge;

import cz.neumimto.rpg.api.Rpg;
import noppes.npcs.EventHooks;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.event.NpcEvent;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.world.chunk.LoadChunkEvent;

public class SpongeEventListener {

    @Listener(order = Order.LAST)
    public void onEntityConstruction(LoadChunkEvent event) {
        if (CnpcRpgSponge.configuration.INITIALIZE_NPCS) {
            for (Entity e : event.getTargetChunk().getEntities()) {
                if (e instanceof EntityNPCInterface) Rpg.get().getEntityService().get(e);
            }
        }
    }

    @Listener(order = Order.LAST)
    public void onEntityDamaged(DamageEntityEvent spongeEvent, @First DamageSource spongeDamageSource) {
        if (spongeEvent.getTargetEntity() instanceof EntityNPCInterface) {
            net.minecraft.util.DamageSource damageSource = (net.minecraft.util.DamageSource) spongeDamageSource;

            EntityNPCInterface entity = (EntityNPCInterface) spongeEvent.getTargetEntity();

            NpcEvent.DamagedEvent event = new NpcEvent.DamagedEvent(
                    entity.wrappedNPC,
                    NoppesUtilServer.GetDamageSourcee(damageSource),
                    (float) spongeEvent.getFinalDamage(),
                    damageSource);
            if (EventHooks.onNPCDamaged(entity, event)) {
                spongeEvent.setCancelled(true);
            } else {
                spongeEvent.setBaseDamage(event.damage);
                if (event.clearTarget) {
                    entity.setAttackTarget(null);
                    entity.setRevengeTarget(null);
                }
            }
        }
    }

}
