package ru.glassspirit.cnpcntrpg.sponge;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.common.entity.AbstractEntityService;
import cz.neumimto.rpg.common.entity.configuration.MobSettingsDao;
import cz.neumimto.rpg.sponge.entities.SpongeMob;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.world.World;
import ru.glassspirit.cnpcntrpg.mixin.IMixinDataStats;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class CnpcRpgEntityHandler extends AbstractEntityService.EntityHandler<SpongeMob> {

    @Override
    public SpongeMob initializeEntity(MobSettingsDao dao, SpongeMob iEntity, String dimName, String type) {
        if (iEntity.getEntity() instanceof EntityNPCInterface) {
            Map<String, Float> properties = ((IMixinDataStats) ((EntityNPCInterface) iEntity.getEntity()).stats).getProperties();
            for (Map.Entry<String, Float> prop : properties.entrySet()) {
                if (Rpg.get().getPropertyService().exists(prop.getKey()))
                    iEntity.setProperty(Rpg.get().getPropertyService().getIdByName(prop.getKey()), prop.getValue());
            }
            return iEntity;
        } else return super.initializeEntity(dao, iEntity, dimName, type);
    }

    @Override
    public double getExperiences(MobSettingsDao dao, String dimension, String type, UUID uuid) {
        if (CnpcRpgSponge.configuration.NPC_KILLS_EXP_RPG) {
            Optional<World> world = Sponge.getServer().getWorld(dimension);
            if (world.isPresent()) {
                Optional<Entity> entity = world.get().getEntity(uuid);
                if (entity.isPresent() && entity.get() instanceof EntityNPCInterface) {
                    return ((EntityNPCInterface) entity.get()).inventory.getExpRNG();
                }
            }
        }
        return super.getExperiences(dao, dimension, type, uuid);
    }

    @Override
    public boolean handleMobDamage(String dimension, UUID uuid) {
        Optional<World> world = Sponge.getServer().getWorld(dimension);
        if (world.isPresent()) {
            Optional<Entity> entity = world.get().getEntity(uuid);
            if (entity.isPresent() && entity.get() instanceof EntityNPCInterface) {
                return false;
            }
        }
        return super.handleMobDamage(dimension, uuid);
    }
}
