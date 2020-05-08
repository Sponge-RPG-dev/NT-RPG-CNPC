/**
 * Script to make your life easier when converting between Sponge/Minecraft entities and CNPC entities
 * To make it work properly, follow this steps:
 * 1. If you have CNPC entity (for example inside CNPC script) and want a Sponge/Minecraft entity, use _mc(entity)
 * Examples:
 * a) Inside CNPC script you have event.player, you want fast access to UUID value of player
 * let uid = _mc(event.player).getUniqueId(); // Done =D
 * b) Inside CNPC script you have event.player, you want to get it's character
 * let character = Rpg.getCharacterService().getCharacter(_mc(event.player)); // No need to access or convert UUID!
 *
 * 2. If you have Sponge/Minecraft entity (for example inside NT-RPG script) and want a CustomNPCs IEntity, use _cnpc(entity)
 */
{
    function _mc(object) {
        if (object instanceof Java.type("noppes.npcs.api.entity.IEntity")) {
            return object.getMCEntity();
        }
        if (object instanceof Java.type("noppes.npcs.api.item.IItemStack")) {
            return object.getMCItemStack();
        }
        if (object instanceof Java.type("noppes.npcs.api.block.IBlock")) {
            return object.getMCBlock();
        }
        if (object instanceof Java.type("noppes.npcs.api.IWorld")) {
            return object.getMCWorld();
        }
        return object;
    }

    Bindings.getScriptEngine().put("_mc", _mc);

    function _cnpc(object) {
        if (object instanceof Java.type("net.minecraft.entity.Entity")) {
            return Java.type("noppes.npcs.api.NpcAPI").Instance().getIEntity(object);
        }
        if (object instanceof Java.type("net.minecraft.item.ItemStack")) {
            return Java.type("noppes.npcs.api.NpcAPI").Instance().getIItemStack(object);
        }
        if (object instanceof Java.type("net.minecraft.world.World")) {
            return Java.type("noppes.npcs.api.NpcAPI").Instance().getIWorld(object);
        }
        return object;
    }

    Bindings.getScriptEngine().put("_cnpc", _cnpc);
}