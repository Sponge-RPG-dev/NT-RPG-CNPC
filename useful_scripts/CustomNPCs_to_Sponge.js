/*
 *
 */
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

/*
 *
 */
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