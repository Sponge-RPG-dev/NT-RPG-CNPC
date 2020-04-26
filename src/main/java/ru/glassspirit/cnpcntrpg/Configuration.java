package ru.glassspirit.cnpcntrpg;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class Configuration {

    @Setting(value = "quests_exp_rpg", comment = "NT-RPG experience for completing quests")
    public boolean QUESTS_EXP_RPG = true;

    @Setting(value = "quests_exp_minecraft", comment = "Minecraft experience for completing quests")
    public boolean QUESTS_EXP_MINECRAFT = false;

    @Setting(value = "npc_kills_exp_rpg", comment = "NT-RPG experience for killing NPCs")
    public boolean NPC_KILLS_EXP_RPG = true;

    @Setting(value = "npc_kills_exp_minecraft", comment = "Minecraft experience for killing NPCs")
    public boolean NPC_KILLS_EXP_MINECRAFT = false;

    @Setting(value = "availability_rpg_level", comment = "Use RPG character level for availability checks")
    public boolean AVAILABILITY_RPG_LEVEL = true;

    @Setting(value = "initialize_npcs", comment = "Initialize NPCs in NT-RPG EntityService when NPC is initialized. It allows applying properties and effects on NPC.")
    public boolean INITIALIZE_NPCS = true;
}
