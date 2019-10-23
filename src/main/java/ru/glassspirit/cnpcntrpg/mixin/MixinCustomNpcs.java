package ru.glassspirit.cnpcntrpg.mixin;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import noppes.npcs.CustomNpcs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.glassspirit.cnpcntrpg.sponge.CnpcRpgSponge;

@Mixin(CustomNpcs.class)
public abstract class MixinCustomNpcs {

    @Inject(method = "load(Lnet/minecraftforge/fml/common/event/FMLPreInitializationEvent;)V", at = @At("HEAD"), remap = false)
    private void onLoad(FMLPreInitializationEvent ev, CallbackInfo ci) {
        CnpcRpgSponge.useMixins = true;
    }

}
