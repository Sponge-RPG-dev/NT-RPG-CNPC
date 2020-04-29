package ru.glassspirit.cnpcntrpg.mixin.impl;

import cz.neumimto.rpg.api.Rpg;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.controllers.data.Availability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.glassspirit.cnpcntrpg.mixin.IMixinAvailability;
import ru.glassspirit.cnpcntrpg.sponge.CnpcRpgSponge;

@Mixin(value = Availability.class, remap = false)
public class MixinAvailablilty implements IMixinAvailability {

    private String requiredClassName = "";
    private int requiredClassLevel;

    @Shadow
    public int minPlayerLevel;

    @Override
    public String getRequiredClassName() {
        return requiredClassName;
    }

    @Override
    public void setRequiredClassName(String name) {
        this.requiredClassName = name;
    }

    @Inject(method = "isAvailable(Lnet/minecraft/entity/player/EntityPlayer;)Z", at = @At("TAIL"), cancellable = true)
    private void onIsAvailable(EntityPlayer player, CallbackInfoReturnable<Boolean> ci) {
        if (requiredClassName != null && !requiredClassName.isEmpty()) {
            if (Rpg.get().getCharacterService().getCharacter(player.getUniqueID()).getClassByName(requiredClassName) == null
                    || Rpg.get().getCharacterService().getCharacter(player.getUniqueID()).getClassByName(requiredClassName).getLevel() < requiredClassLevel) {
                ci.setReturnValue(false);
                return;
            }
        }
        if (CnpcRpgSponge.configuration.AVAILABILITY_RPG_LEVEL) {
            ci.setReturnValue(Rpg.get().getCharacterService().getCharacter(player.getUniqueID()).getLevel() >= this.minPlayerLevel);
        }
    }

    @Inject(method = "writeToNBT", at = @At("TAIL"))
    private void onWriteToNBT(NBTTagCompound tag, CallbackInfoReturnable<NBTTagCompound> ci) {
        tag.setString("AvailabilityRequiredClassName", requiredClassName);
        tag.setInteger("AvailabilityRequiredClassLevel", requiredClassLevel);
    }

    @Inject(method = "readFromNBT", at = @At("TAIL"))
    private void onReadFromNBT(NBTTagCompound tag, CallbackInfo ci) {
        this.requiredClassName = tag.getString("AvailabilityRequiredClassName");
        this.requiredClassLevel = tag.getInteger("AvailabilityRequiredClassLevel");
    }
}
