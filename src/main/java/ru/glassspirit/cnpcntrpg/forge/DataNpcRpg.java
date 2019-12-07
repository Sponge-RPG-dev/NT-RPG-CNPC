package ru.glassspirit.cnpcntrpg.forge;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
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
        mainContainer.appandConsole("2. Second tab is for default nt-rpg properties");

        Map<String, Object> dataMap = new TreeMap<>();
        dataMap.put("Level", ((IMixinDataStats) npc.stats).getLevel());

        mainContainer.script += gson.toJson(dataMap);
        this.getScripts().add(mainContainer);
    }

    private void initProperties() {
        ScriptContainer container = new ScriptContainer(this);

        container.script += "properties doesn't work yet"; //TODO properties
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
    }

    private void applyProperties() {
        //TODO properties
    }

}
