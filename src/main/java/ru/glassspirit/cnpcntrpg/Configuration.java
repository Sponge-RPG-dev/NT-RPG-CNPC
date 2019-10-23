package ru.glassspirit.cnpcntrpg;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class Configuration {

    @Setting(value = "quests_exp_rpg", comment = "NT-RPG experience for completing quests")
    public boolean QUESTS_EXP_RPG = true;

    @Setting(value = "quests_exp_minecraft", comment = "Minecraft experience for completing quests")
    public boolean QUESTS_EXP_MINECRAFT = false;

    @Setting(value = "npc_kills_exp_rpg", comment = "NT-RPG experience for killing npcs")
    public boolean NPC_KILLS_EXP_RPG = true;

    @Setting(value = "npc_kills_exp_minecraft", comment = "Minecraft experience for killing npcs")
    public boolean NPC_KILLS_EXP_MINECRAFT = true;

    @Setting(value = "party_quest_mob_kills", comment = "Minecraft experience for killing mobs")
    public boolean PARTY_QUEST_MOB_KILLS = true;

}