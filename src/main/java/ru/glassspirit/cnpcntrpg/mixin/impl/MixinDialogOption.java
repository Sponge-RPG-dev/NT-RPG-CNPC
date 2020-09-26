package ru.glassspirit.cnpcntrpg.mixin.impl;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.controllers.data.DialogOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.glassspirit.cnpcntrpg.mixin.IMixinDialogOption;

@Mixin(value = DialogOption.class, remap = false)
public class MixinDialogOption implements IMixinDialogOption {

    @Shadow
    public int optionType;
    @Shadow
    public String command;
    @Shadow
    public String title;
    @Shadow
    public int dialogId;
    @Shadow
    public int optionColor;

    private String script = "";

    @Override
    public String getScript() {
        return script;
    }

    @Override
    public void setScript(String script) {
        this.script = script;
        this.optionType = 5;
    }

    @Inject(method = "readNBT", at = @At("HEAD"))
    private void readNBT(NBTTagCompound compound, CallbackInfo ci) {
        if (compound != null) {
            this.script = compound.getString("Script");
        }
    }

    @Inject(method = "writeNBT", at = @At("HEAD"), cancellable = true)
    private void writeNBT(CallbackInfoReturnable<NBTTagCompound> cir) {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("Title", this.title);
        compound.setInteger("OptionType", this.optionType);
        compound.setInteger("Dialog", this.dialogId);
        compound.setInteger("DialogColor", this.optionColor);
        compound.setString("DialogCommand", this.command);
        compound.setString("Script", this.script);

        cir.setReturnValue(compound);
    }
}
