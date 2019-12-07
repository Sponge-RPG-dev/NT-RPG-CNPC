package ru.glassspirit.cnpcntrpg.mixin.impl;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.entity.data.DataStats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.glassspirit.cnpcntrpg.mixin.IMixinDataStats;

@Mixin(DataStats.class)
public abstract class MixinDataStats implements IMixinDataStats {

    private int level;

    @Inject(method = "writeToNBT", at = @At("TAIL"), remap = false)
    private void injectWriteNbt(NBTTagCompound tag, CallbackInfoReturnable<NBTTagCompound> ci) {
        tag.setInteger("Level", level);
    }

    @Inject(method = "readToNBT", at = @At("TAIL"), remap = false)
    private void injectReadNbt(NBTTagCompound tag, CallbackInfo ci) {
        this.level = tag.getInteger("Level");
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int l) {
        if (l > 0) this.level = l;
        else level = 0;
    }

}