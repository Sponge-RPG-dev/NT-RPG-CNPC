package ru.glassspirit.cnpcntrpg.sponge;

import com.onaple.itemizer.Itemizer;
import com.onaple.itemizer.service.ItemService;
import org.spongepowered.api.Sponge;

public class ItemizerHelper {

    public static boolean isItemizerLoaded() {
        return Sponge.getPluginManager().isLoaded("itemizer");
    }

    public static ItemService getItemService() {
        if (isItemizerLoaded()) {
            return Itemizer.getItemService();
        } else return null;
    }

}
