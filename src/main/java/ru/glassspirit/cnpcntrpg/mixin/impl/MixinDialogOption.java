package ru.glassspirit.cnpcntrpg.mixin.impl;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.controllers.data.DialogOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
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

    /**
     * @author GlassSpirit 05.12.2019
     * @reason Store script data
     */
    @Overwrite
    public void readNBT(NBTTagCompound compound) {
        if (compound != null) {
            this.title = compound.getString("Title");
            this.dialogId = compound.getInteger("Dialog");
            this.optionColor = compound.getInteger("DialogColor");
            this.optionType = compound.getInteger("OptionType");
            this.command = compound.getString("DialogCommand");
            if (this.optionColor == 0) {
                this.optionColor = 14737632;
            }
            this.script = compound.getString("Script");
        }
    }

    /**
     * @author GlassSpirit 05.12.2019
     * @reason Store script data
     */
    @Overwrite
    public NBTTagCompound writeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("Title", this.title);
        compound.setInteger("OptionType", this.optionType);
        compound.setInteger("Dialog", this.dialogId);
        compound.setInteger("DialogColor", this.optionColor);
        compound.setString("DialogCommand", this.command);
        compound.setString("Script", this.script);
        return compound;
    }
}
