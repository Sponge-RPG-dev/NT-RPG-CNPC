package ru.glassspirit.cnpcntrpg.mixin.impl;

import cz.neumimto.rpg.api.Rpg;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ru.glassspirit.cnpcntrpg.mixin.IMixinDialog;

import javax.script.Invocable;
import javax.script.ScriptEngine;

@Mixin(value = Dialog.class, remap = false)
public class MixinDialog implements IMixinDialog {

    private static String scriptMark = "$$$";

    @Shadow
    public String text;

    @Override
    public boolean isScripted() {
        return this.text.contains(scriptMark);
    }

    @Override
    public void processScripts(EntityPlayer player, EntityNPCInterface npc) {
        StringBuilder text = new StringBuilder(this.text);
        while (isScripted()) {
            int startIndex = text.indexOf(scriptMark);
            int endIndex = text.indexOf(scriptMark, startIndex + scriptMark.length());
            if (endIndex == -1) break; // Something went wrong!

            String function = text.substring(startIndex + scriptMark.length(), endIndex);
            String result = "";
            try {
                ScriptEngine engine = Rpg.get().getScriptEngine().getEngine();
                Object func = engine.eval("function(dialog, player, npc) { " + function + " }");
                engine.eval("var cnpcNtRpgRunScript = function(f, dialog, player, npc) {\n" +
                        "    return f(dialog, player, npc);\n" +
                        "}");
                Invocable i = (Invocable) engine;
                Object obj = i.invokeFunction("cnpcNtRpgRunScript", func, this, NpcAPI.Instance().getIEntity(player), NpcAPI.Instance().getIEntity(npc));
                if (obj != null) result = obj.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            text.replace(startIndex, endIndex + scriptMark.length(), result);
        }
        this.text = text.toString();
    }
}
