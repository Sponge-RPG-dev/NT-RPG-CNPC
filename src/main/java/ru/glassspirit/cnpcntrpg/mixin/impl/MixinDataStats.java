package ru.glassspirit.cnpcntrpg.mixin.impl;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.entity.data.DataStats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.glassspirit.cnpcntrpg.mixin.IMixinDataStats;

import java.util.Map;

@Mixin(value = DataStats.class, remap = false)
public abstract class MixinDataStats implements IMixinDataStats {

    private static final Gson gson = new Gson();

    private int level;
    private Map<String, Object> customData = new LinkedTreeMap<>();
    private Map<String, Double> storedProperties = new LinkedTreeMap<>();

    private String defaultEffects = "";

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
    public Map<String, Double> getStoredProperties() {
        return storedProperties;
    }

    @Override
    public void setDefaultEffects(String e) {
        this.defaultEffects = e;
    }

    @Override
    public String getDefaultEffects() {
        return defaultEffects;
    }

    @Inject(method = "writeToNBT", at = @At("TAIL"))
    private void injectWriteNbt(NBTTagCompound tag, CallbackInfoReturnable<NBTTagCompound> ci) {
        tag.setInteger("Level", getLevel());
        tag.setString("CustomData", gson.toJson(getCustomData()));
        tag.setString("Properties", gson.toJson(getStoredProperties()));
        tag.setString("DefaultEffects", getDefaultEffects());
    }

    @Inject(method = "readToNBT", at = @At("TAIL"))
    private void injectReadNbt(NBTTagCompound tag, CallbackInfo ci) {
        this.setLevel(tag.getInteger("Level"));

        Map<String, Object> customDataMap = gson.fromJson(tag.getString("CustomData"), LinkedTreeMap.class);
        if (customDataMap != null) this.customData.putAll(customDataMap);

        Map<String, Double> propertiesMap = gson.fromJson(tag.getString("Properties"), LinkedTreeMap.class);
        if (propertiesMap != null) this.storedProperties.putAll(propertiesMap);

        this.setDefaultEffects(tag.getString("DefaultEffects"));
    }
}
