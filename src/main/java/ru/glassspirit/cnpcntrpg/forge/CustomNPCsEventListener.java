package ru.glassspirit.cnpcntrpg.forge;

import cz.neumimto.rpg.api.utils.Pair;
import cz.neumimto.rpg.common.exp.ExperienceSources;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterServise;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.EntityType;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.data.INPCInventory;
import noppes.npcs.api.event.NpcEvent;
import noppes.npcs.api.event.QuestEvent;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.api.entity.living.player.Player;
import ru.glassspirit.cnpcntrpg.sponge.CnpcRpgSponge;
import ru.glassspirit.cnpcntrpg.sponge.ItemizerHelper;

import java.util.*;

import static cz.neumimto.rpg.sponge.NtRpgPlugin.pluginConfig;

public class CustomNPCsEventListener {

    private SpongeCharacterServise characterService;

    /**
     * Used for dirty hacking cnpc "minecraft orb" exp drops using two events
     */
    private Map<UUID, Pair<Integer, Integer>> recentlyDeadNpcs = new HashMap<>();

    public CustomNPCsEventListener() {
        characterService = NtRpgPlugin.GlobalScope.characterService;
    }

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

        if (CnpcRpgSponge.configuration.NPC_KILLS_EXP_RPG) {
            if (event.damageSource.getTrueSource().typeOf(EntityType.PLAYER)) {
                ISpongeCharacter character = characterService.getCharacter(UUID.fromString(event.damageSource.getTrueSource().getUUID()));
                if (character != null && !character.isStub()) {
                    int experience = inv.getExpRNG();
                    if (experience > 0) {
                        if (character.hasParty()) {
                            experience *= pluginConfig.PARTY_EXPERIENCE_MULTIPLIER;
                            double dist = Math.pow(pluginConfig.PARTY_EXPERIENCE_SHARE_DISTANCE, 2);
                            Set<ISpongeCharacter> set = new HashSet<>();
                            for (ISpongeCharacter member : character.getParty().getPlayers()) {
                                Player player = member.getPlayer();
                                if (player.getLocation().getPosition()
                                        .distanceSquared(character.getPlayer().getLocation().getPosition()) <= dist) {
                                    set.add(member);
                                }
                            }
                            experience /= set.size();
                            for (ISpongeCharacter character1 : set) {
                                characterService.addExperiences(character1, experience, ExperienceSources.PVE);
                            }
                        } else {
                            characterService.addExperiences(character, experience, ExperienceSources.PVE);
                        }
                    }
                }
            }
        }
        if (!CnpcRpgSponge.configuration.NPC_KILLS_EXP_MINECRAFT) {
            recentlyDeadNpcs.put(npc.getMCEntity().getUniqueID(), new Pair<>(inv.getExpMin(), inv.getExpMax()));
            event.npc.getInventory().setExp(0, 0);
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

    //=========================Itemizer=====================================

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingDrops(LivingDropsEvent event) {
        if (ItemizerHelper.isItemizerLoaded()) {
            if (event.getEntityLiving() instanceof EntityNPCInterface) {
                for (EntityItem item : event.getDrops()) {
                    ItemStack stack = item.getItem();
                    if (stack.getDisplayName().startsWith("#IT")) {
                        String[] str = stack.getDisplayName().split("#");
                        if (str[2].equalsIgnoreCase("id")) {
                            int quantity = item.getItem().getCount();
                            item.setItem((ItemStack) (Object) ItemizerHelper.getItemService().retrieve(str[3]).orElse(org.spongepowered.api.item.inventory.ItemStack.empty()));
                            item.getItem().setCount(item.getItem().getCount() * quantity);
                        } else if (str[2].equalsIgnoreCase("pool")) {
                            int quantity = item.getItem().getCount();
                            item.setItem((ItemStack) (Object) ItemizerHelper.getItemService().fetch(str[3]).orElse(org.spongepowered.api.item.inventory.ItemStack.empty()));
                            item.getItem().setCount(item.getItem().getCount() * quantity);
                        }
                    }
                }
            }
        }

    }
}
