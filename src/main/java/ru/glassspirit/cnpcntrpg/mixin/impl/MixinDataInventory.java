package ru.glassspirit.cnpcntrpg.mixin.impl;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import noppes.npcs.entity.data.DataInventory;
import org.spongepowered.api.Sponge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.glassspirit.cnpcntrpg.sponge.ItemizerHelper;

@Mixin(value = DataInventory.class, remap = false)
public abstract class MixinDataInventory {

    @Redirect(method = "dropStuff", at = @At(value = "INVOKE", target = "Lnoppes/npcs/entity/data/DataInventory;getEntityItem(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/item/EntityItem;"))
    private EntityItem onDropStuffItemizer(DataInventory data, ItemStack stack) {
        if (Sponge.getPluginManager().isLoaded("itemizer")) {
            if (stack.getDisplayName().startsWith("#IT")) {
                String[] str = stack.getDisplayName().split("#");
                if (str[2].equalsIgnoreCase("id")) {
                    return data.getEntityItem((ItemStack) (Object) ItemizerHelper.getItemService().retrieve(str[3]).orElse(org.spongepowered.api.item.inventory.ItemStack.empty()));
                } else if (str[2].equalsIgnoreCase("pool")) {
                    return data.getEntityItem((ItemStack) (Object) ItemizerHelper.getItemService().fetch(str[3]).orElse(org.spongepowered.api.item.inventory.ItemStack.empty()));
                }
            }
        }
        return data.getEntityItem(stack);
    }

}
