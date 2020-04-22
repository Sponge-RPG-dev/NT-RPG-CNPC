package ru.glassspirit.cnpcntrpg.forge;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.configuration.PluginConfig;
import cz.neumimto.rpg.api.utils.Pair;
import cz.neumimto.rpg.common.exp.ExperienceSources;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterService;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.data.INPCInventory;
import noppes.npcs.api.event.NpcEvent;
import noppes.npcs.api.event.QuestEvent;
import noppes.npcs.entity.EntityNPCInterface;
import ru.glassspirit.cnpcntrpg.sponge.CnpcRpgSponge;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CustomNPCsEventListener {

    private SpongeCharacterService characterService = (SpongeCharacterService) Rpg.get().getCharacterService();
    private PluginConfig pluginConfig = Rpg.get().getPluginConfig();

    /**
     * Used for dirty hacking cnpc "minecraft orb" exp drops using two events
     */
    private Map<UUID, Pair<Integer, Integer>> recentlyDeadNpcs = new HashMap<>();


    //=========================QUEST EXP=====================================

    @SubscribeEvent
    public void onNpcQuestCompletion(QuestEvent.QuestTurnedInEvent event) {
        if (CnpcRpgSponge.configuration.QUESTS_EXP_RPG) {
            ISpongeCharacter character = characterService.getCharacter(UUID.fromString(event.player.getUUID()));
            if (character != null && !character.isStub()) {
                characterService.addExperiences(character, event.expReward, ExperienceSources.QUESTING);
            }
        }
        if (!CnpcRpgSponge.configuration.QUESTS_EXP_MINECRAFT) {
            event.expReward = 0;
        }
    }

    //=========================KILL EXP=====================================

    @SubscribeEvent
    public void onNpcDeath(NpcEvent.DiedEvent event) {
        ICustomNpc npc = event.npc;
        INPCInventory inv = npc.getInventory();
        if (!CnpcRpgSponge.configuration.NPC_KILLS_EXP_MINECRAFT) {
            recentlyDeadNpcs.put(npc.getMCEntity().getUniqueID(), new Pair<>(inv.getExpMin(), inv.getExpMax()));
            inv.setExp(0, 0);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onNpcDeath2(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof EntityNPCInterface) {
            if (recentlyDeadNpcs.containsKey(event.getEntityLiving().getUniqueID())) {
                Pair<Integer, Integer> value = recentlyDeadNpcs.get(event.getEntityLiving().getUniqueID());
                ICustomNpc npc = (ICustomNpc) NpcAPI.Instance().getIEntity(event.getEntityLiving());
                npc.getInventory().setExp(value.key, value.value);
                recentlyDeadNpcs.remove(event.getEntityLiving().getUniqueID());
            }
        }
    }
}
