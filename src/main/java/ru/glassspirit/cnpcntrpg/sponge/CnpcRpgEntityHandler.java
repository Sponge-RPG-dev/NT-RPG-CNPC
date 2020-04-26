package ru.glassspirit.cnpcntrpg.sponge;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.hocon.HoconFormat;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.configuration.adapters.EffectsAdapter;
import cz.neumimto.rpg.api.effects.EffectParams;
import cz.neumimto.rpg.api.effects.IGlobalEffect;
import cz.neumimto.rpg.common.entity.AbstractEntityService;
import cz.neumimto.rpg.common.entity.configuration.MobSettingsDao;
import cz.neumimto.rpg.sponge.entities.SpongeMob;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.world.World;
import ru.glassspirit.cnpcntrpg.mixin.IMixinDataStats;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class CnpcRpgEntityHandler extends AbstractEntityService.EntityHandler<SpongeMob> {

    @Override
    public SpongeMob initializeEntity(MobSettingsDao dao, SpongeMob iEntity, String dimName, String type) {
        if (iEntity.getEntity() instanceof EntityNPCInterface) {
            IMixinDataStats rpgStats = (IMixinDataStats) ((EntityNPCInterface) iEntity.getEntity()).stats;

            // Apply properties
            Map<String, Double> properties = rpgStats.getStoredProperties();
            for (Map.Entry<String, Double> prop : properties.entrySet()) {
                if (Rpg.get().getPropertyService().exists(prop.getKey()))
                    iEntity.setProperty(Rpg.get().getPropertyService().getIdByName(prop.getKey()), prop.getValue().floatValue());
            }

            // Apply effects
            List<Config> effects = HoconFormat.instance().createParser().parse(rpgStats.getDefaultEffects()).get("Effects");
            Map<IGlobalEffect, EffectParams> effectMap = new EffectsAdapter().convertToField(effects);
            Rpg.get().scheduleSyncLater(() -> {
                for (Map.Entry<IGlobalEffect, EffectParams> e : effectMap.entrySet()) {
                    Rpg.get().getEffectService().addEffect(e.getKey().construct(iEntity, -1, e.getValue()));
                }
            });
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
