package ru.glassspirit.cnpcntrpg.forge;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.io.ConfigParser;
import com.electronwill.nightconfig.core.io.ConfigWriter;
import com.electronwill.nightconfig.json.JsonFormat;
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
    private static final ConfigParser<Config> parser = JsonFormat.minimalInstance().createParser();
    private static final ConfigWriter fancyWriter = JsonFormat.fancyInstance().createWriter();
    private static final ConfigWriter minimalWriter = JsonFormat.minimalInstance().createWriter();

    private static final Config emptyEffectsConfig = parser.parse("{\"Effects\":[]}");

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
        Config conf;
        if (effectsString.isEmpty()) {
            conf = emptyEffectsConfig;
        } else conf = parser.parse(effectsString);

        if (!conf.contains("Effects")) conf = emptyEffectsConfig;
        container.script += fancyWriter.writeToString(conf);

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
        if (data.trim().isEmpty()) {
            ((IMixinDataStats) npc.stats).setDefaultEffects("");
            return;
        }

        Config conf = parser.parse(data);
        if (!conf.contains("Effects") || conf.equals(emptyEffectsConfig)) {
            ((IMixinDataStats) npc.stats).setDefaultEffects("");
        } else {
            ((IMixinDataStats) npc.stats).setDefaultEffects(minimalWriter.writeToString(conf));
        }
    }

}
