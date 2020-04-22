package ru.glassspirit.cnpcntrpg.forge;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.PropertyService;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.DataScript;
import ru.glassspirit.cnpcntrpg.mixin.IMixinDataStats;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class DataNpcRpg extends DataScript {

    public static final Map<UUID, UUID> playersEditingRpgData = new HashMap<>();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private EntityNPCInterface npc;

    public DataNpcRpg(EntityNPCInterface npc, boolean init) {
        super(npc);
        this.npc = npc;
        if (init) {
            setEnabled(true);
            initNpcData();
            initProperties();
        }
    }

    private void initNpcData() {
        ScriptContainer mainContainer = new ScriptContainer(this);
        mainContainer.appandConsole("This is RPG data of NPC.");
        mainContainer.appandConsole("Do not create new tabs or attach scripts (THIS IS NOT A SCRIPTING GUI!!!)");
        mainContainer.appandConsole("1. First tab is for additional NPC data");
        mainContainer.appandConsole("2. Second tab is for nt-rpg properties. Available properties (name:default value):");
        for (String prop : Rpg.get().getPropertyService().getAllProperties()) {
            mainContainer.appandConsole(prop + ":" + Rpg.get().getPropertyService().getDefault(Rpg.get().getPropertyService().getIdByName(prop)));
        }

        Map<String, Object> dataMap = new TreeMap<>();
        dataMap.put("Level", ((IMixinDataStats) npc.stats).getLevel());
        dataMap.put("CustomData", ((IMixinDataStats) npc.stats).getCustomData());

        mainContainer.script += gson.toJson(dataMap);
        this.getScripts().add(mainContainer);
    }

    private void initProperties() {
        PropertyService propertyService = Rpg.get().getPropertyService();

        ScriptContainer container = new ScriptContainer(this);
        Map<String, Double> properties = ((IMixinDataStats) npc.stats).getProperties();

        Map<String, Object> dataMap = new TreeMap<>();

        for (String prop : properties.keySet()) {
            dataMap.put(prop, properties.get(prop));
        }

        container.script += gson.toJson(dataMap);
        this.getScripts().add(container);
    }

    public void apply() {
        applyNpcData();
        applyProperties();
    }

    private void applyNpcData() {
        String data = this.getScripts().get(0).script;
        Map<String, Object> dataMap = gson.fromJson(data, new TypeToken<TreeMap<String, Object>>() {
        }.getType());

        ((IMixinDataStats) npc.stats).setLevel(((Double) dataMap.get("Level")).intValue());
        ((IMixinDataStats) npc.stats).getCustomData().putAll((Map<String, Object>) dataMap.get("CustomData"));
    }

    private void applyProperties() {
        String data = this.getScripts().get(1).script;
        Map<String, Double> dataMap = gson.fromJson(data, new TypeToken<TreeMap<String, Float>>() {
        }.getType());

        ((IMixinDataStats) npc.stats).getProperties().putAll(dataMap);
    }

}
