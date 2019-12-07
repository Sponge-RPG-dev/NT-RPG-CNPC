package ru.glassspirit.cnpcntrpg.mixin.impl;

import cz.neumimto.rpg.sponge.NtRpgPlugin;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.data.Availability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.glassspirit.cnpcntrpg.sponge.CnpcRpgSponge;

@Mixin(value = Availability.class, remap = false)
public class MixinAvailablilty {

    @Shadow
    public int minPlayerLevel;

    @Inject(method = "isAvailable(Lnet/minecraft/entity/player/EntityPlayer;)Z", at = @At("TAIL"), cancellable = true)
    private void onIsAvailable(EntityPlayer player, CallbackInfoReturnable<Boolean> ci) {
        if (CnpcRpgSponge.configuration.AVAILABILITY_RPG_LEVEL) {
            ci.setReturnValue(NtRpgPlugin.GlobalScope.characterService.getCharacter(player.getUniqueID()).getLevel() >= this.minPlayerLevel);
        }
    }

}
