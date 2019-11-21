package ru.glassspirit.cnpcntrpg.mixin;

import noppes.npcs.api.event.NpcEvent;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = EntityNPCInterface.class)
public abstract class MixinEntityNPCInterface {

    /**
     * @author GlassSpirit
     * @reason Move CustomNPCs DamagedEvent after LivingHurtEvent for compatibility with NT-RPG plugin
     */
    @Redirect(method = "attackEntityFrom", at = @At(value = "INVOKE", target = "Lnoppes/npcs/EventHooks;onNPCDamaged(Lnoppes/npcs/entity/EntityNPCInterface;Lnoppes/npcs/api/event/NpcEvent$DamagedEvent;)Z"))
    private boolean onNPCDamagedEvent(EntityNPCInterface entity, NpcEvent.DamagedEvent event) {
        return false;
    }

}
