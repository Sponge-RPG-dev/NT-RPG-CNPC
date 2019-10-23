package ru.glassspirit.cnpcntrpg.mixin;

import cz.neumimto.rpg.sponge.NtRpgPlugin;
import noppes.npcs.controllers.ScriptContainer;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;

@Mixin(ScriptContainer.class)
public abstract class MixinScriptContainer {

    @Shadow(remap = false)
    private ScriptEngine engine;

    @Inject(method = "setEngine", at = @At(value = "FIELD", target = "noppes/npcs/controllers/ScriptContainer.init:Z",
            opcode = Opcodes.PUTFIELD, remap = false), remap = false)
    private void onSetEngine(CallbackInfo ci) {
        ScriptEngine rpgEngine = NtRpgPlugin.GlobalScope.jsLoader.getEngine();
        if (rpgEngine != null) {
            this.engine.getBindings(ScriptContext.ENGINE_SCOPE).putAll(rpgEngine.getBindings(ScriptContext.ENGINE_SCOPE));
        }
    }

}