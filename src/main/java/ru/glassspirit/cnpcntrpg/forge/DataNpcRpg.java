package ru.glassspirit.cnpcntrpg.forge;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.hocon.HoconFormat;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.DataScript;
import ru.glassspirit.cnpcntrpg.mixin.IMixinDataStats;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataNpcRpg extends DataScript {

    public static final Map<UUID, UUID> playersEditingRpgData = new HashMap<>();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String emptyEffectsString = "Effects: [\n]";
    private static final Config emptyEffectsConfig = HoconFormat.instance().createParser().parse(emptyEffectsString);

    private EntityNPCInterface npc;

    public DataNpcRpg(EntityNPCInterface npc, boolean init) {
        super(npc);
        this.npc = npc;
        if (init) {
            setEnabled(true);
            initNpcData();
            initProperties();
            initEffects();
        }
    }

    private void initNpcData() {
        ScriptContainer mainContainer = new ScriptContainer(this);
        mainContainer.appandConsole("This is RPG data of NPC.");
        mainContainer.appandConsole("Do not create new tabs or attach scripts (THIS IS NOT A SCRIPTING GUI!!!)");
        mainContainer.appandConsole("1. First tab is for additional NPC data");
        mainContainer.appandConsole("2. Second tab is for NT-RPG properties.");
        mainContainer.appandConsole("3. Third tab is for NT-RPG GlobalEffects");

        Map<String, Object> dataMap = new LinkedTreeMap<>();
        dataMap.put("Level", ((IMixinDataStats) npc.stats).getLevel());
        dataMap.put("CustomData", ((IMixinDataStats) npc.stats).getCustomData());

        mainContainer.script += gson.toJson(dataMap);
        this.getScripts().add(mainContainer);
    }

    private void initProperties() {
        ScriptContainer container = new ScriptContainer(this);
        Map<String, Double> properties = ((IMixinDataStats) npc.stats).getStoredProperties();
        container.script += gson.toJson(properties);
        this.getScripts().add(container);
    }

    private void initEffects() {
        ScriptContainer container = new ScriptContainer(this);
        String effectsString = ((IMixinDataStats) npc.stats).getDefaultEffects().trim();
        if (effectsString.isEmpty()) effectsString = emptyEffectsString;

        Config conf = HoconFormat.instance().createParser().parse(effectsString);
        if (!conf.contains("Effects")) conf = emptyEffectsConfig;
        container.script += HoconFormat.instance().createWriter().writeToString(conf);

        this.getScripts().add(container);
    }

    public void apply() {
        applyNpcData();
        applyProperties();
        applyEffects();
    }

    private void applyNpcData() {
        String data = this.getScripts().get(0).script;
        Map<String, Object> dataMap = gson.fromJson(data, new TypeToken<LinkedTreeMap<String, Object>>() {
        }.getType());

        ((IMixinDataStats) npc.stats).setLevel(((Number) dataMap.get("Level")).intValue());
        ((IMixinDataStats) npc.stats).getCustomData().putAll((Map<String, Object>) dataMap.get("CustomData"));
    }

    private void applyProperties() {
        String data = this.getScripts().get(1).script;
        Map<String, Double> dataMap = gson.fromJson(data, new TypeToken<LinkedTreeMap<String, Double>>() {
        }.getType());

        ((IMixinDataStats) npc.stats).getStoredProperties().putAll(dataMap);
    }

    private void applyEffects() {
        String data = this.getScripts().get(2).script;
        Config conf = HoconFormat.instance().createParser().parse(data);
        if (!conf.contains("Effects")) {
            ((IMixinDataStats) npc.stats).setDefaultEffects("");
        } else {
            ((IMixinDataStats) npc.stats).setDefaultEffects(HoconFormat.instance().createWriter().writeToString(conf));
        }
    }

}
