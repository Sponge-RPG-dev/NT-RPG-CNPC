package ru.glassspirit.cnpcntrpg.mixin.impl;

import com.google.gson.Gson;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.entity.data.DataStats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.glassspirit.cnpcntrpg.mixin.IMixinDataStats;

import java.util.Map;
import java.util.TreeMap;

@Mixin(DataStats.class)
public abstract class MixinDataStats implements IMixinDataStats {

    private static final Gson gson = new Gson();

    private int level;
    private Map<String, Object> customData = new TreeMap<>();
    private Map<String, Float> properties = new TreeMap<>();

    @Inject(method = "writeToNBT", at = @At("TAIL"), remap = false)
    private void injectWriteNbt(NBTTagCompound tag, CallbackInfoReturnable<NBTTagCompound> ci) {
        tag.setInteger("Level", level);
        tag.setString("CustomData", gson.toJson(customData));
        tag.setString("Properties", gson.toJson(properties));
    }

    @Inject(method = "readToNBT", at = @At("TAIL"), remap = false)
    private void injectReadNbt(NBTTagCompound tag, CallbackInfo ci) {
        this.level = tag.getInteger("Level");

        Map<String, Object> customDataMap = gson.fromJson(tag.getString("CustomData"), TreeMap.class);
        if (customDataMap != null) this.customData.putAll(customDataMap);

        Map<String, Float> propertiesMap = gson.fromJson(tag.getString("Properties"), TreeMap.class);
        if (propertiesMap != null) this.properties.putAll(propertiesMap);
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int l) {
        this.level = Math.max(l, 0);
    }

    @Override
    public Map<String, Object> getCustomData() {
        return customData;
    }

    @Override
    public Map<String, Float> getProperties() {
        return properties;
    }
}
