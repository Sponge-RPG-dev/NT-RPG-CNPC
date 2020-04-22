package ru.glassspirit.cnpcntrpg.mixin;

import com.onaple.itemizer.Itemizer;
import com.onaple.itemizer.service.ItemService;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.sponge.entities.SpongeEntityService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.Living;

public class MixinHooksHelper {

    public static void onNpcReset(Object npc) {
        SpongeEntityService entityService = (SpongeEntityService) Rpg.get().getEntityService();
        if (entityService.get((Living) npc) != null) {
            entityService.remove((Living) npc);
            entityService.get((Living) npc);
        }
    }

    public static boolean isItemizerLoaded() {
        return Sponge.getPluginManager().isLoaded("itemizer");
    }

    public static ItemService getItemizerItemService() {
        if (isItemizerLoaded()) {
            return Itemizer.getItemService();
        } else return null;
    }

}
