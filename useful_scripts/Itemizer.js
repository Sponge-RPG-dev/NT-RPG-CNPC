/**
 * Script to use Itemizer plugin in scripts with ease!
 * Read the javadocs to methods (god help)
 */
{
    /*
     *  IMPORTS
     */
    const Sponge = Java.type("org.spongepowered.api.Sponge");
    const TextS = Java.type("org.spongepowered.api.text.serializer.TextSerializers").FORMATTING_CODE;
    const ItemStack = Java.type("org.spongepowered.api.item.inventory.ItemStack");
    const ItemType = Java.type("org.spongepowered.api.item.ItemType");
    const ItemTypes = Java.type("org.spongepowered.api.item.ItemTypes");
    const ItemizerItemService = Java.type("com.onaple.itemizer.Itemizer").getItemService();
    const QueryOperationTypes = Java.type("org.spongepowered.api.item.inventory.query.QueryOperationTypes");

    /*
     *  CONSTANTS
     */
    const NOT_ENOUGH_INVENTORY_SPACE = "Not enough inventory space!";
    const NOT_ENOUGH_RESOURCES = "Not enough resources!";

    /*
     *  LOGIC
     */
    const Itemizer = {

        /**
         Gets itemizer ItemStack by id
         @param id: Itemizer id of item
         @return Sponge ItemStack or empty
         */
        retrieve: function (id) {
            const optional = ItemizerItemService.retrieve(id);
            return optional.orElse(ItemStack.empty());
        },

        /**
         Gets itemizer ItemStack from pool by pool id
         @param poolId: Itemizer pool id
         @return Sponge ItemStack from pool or empty
         */
        fetch: function (poolId) {
            const optional = ItemizerItemService.fetch(id);
            return optional.orElse(ItemStack.empty());
        },

        /**
         Checks for itemizer items inside player inventory
         @param player: Sponge player
         @param itemId: itemizer id of item
         @param quantity: quantity of items to search
         @return true if inventory has quantity of items, false either
         */
        hasItemizerItem: function (player, itemId, quantity) {
            return this.hasItem(player, this.retrieve(itemId), quantity);
        },

        /**
         Adds itemizer item to inventory (doesn't check for inventory contents, will act strange if inventory is full)
         @param player: Sponge player
         @param itemId: itemizer id of item
         @param quantity: quantity of items to add
         */
        addItemizerItem: function (player, itemId, quantity) {
            this.addItem(player, this.retrieve(itemId), quantity);
        },

        /**
         Removes itemizer item from inventory (doesn't check for inventory contents, will act strange if inventory doesn't contain quantity of items)
         @param player: Sponge player
         @param itemId: itemizer id of item
         @param quantity: quantity of items to remove
         */
        removeItemizerItem: function (player, itemId, quantity) {
            this.removeItem(player, this.retrieve(itemId), quantity);
        },

        /**
         Tries to craft item
         @param player: Sponge player
         @param required: double-array of item requirements.
         Should look like [[1, 3]] if you want to require itemizer item with id 1 and quantity 3.
         You can have multiple required items, then it will look like [[1, 2], [4, 1], ["minecraft:stone", 1]].
         You can use default minecraft items (mods included) by using String id instead of int ("minecraft:stone" for example).
         @param result: array of craft return. There can be only one item.
         Should look like [1, 1] if you want to give itemizer item with id 1 and quantity 1.
         You can use default minecraft items (mods included) by using String id instead of int ("minecraft:stone" for example).
         @return true if item was successfully crafted, false either
         */
        craft: function (player, required, result) {
            let resultStack;
            if (typeof result[0] == "string") {
                resultStack = ItemStack.of(result[0]);
            } else {
                resultStack = this.retrieve(result[0]);
            }

            if (!player.getInventory().canFit(resultStack)) {
                player.sendMessage(TextS.deserialize(NOT_ENOUGH_INVENTORY_SPACE));
                return false;
            }

            let canCraft = true;
            for (let i = 0; i < required.length; i++) {
                let p = required[i];
                if (!canCraft) return false;

                if (typeof p[0] == "string") {
                    canCraft = this.hasItem(player, p[0], p[1]);
                } else {
                    canCraft = this.hasItemizerItem(player, p[0], p[1]);
                }
            }
            if (!canCraft) {
                player.sendMessage(TextS.deserialize(NOT_ENOUGH_RESOURCES));
                return false;
            } else {
                for (let i = 0; i < required.length; i++) {
                    let p = required[i];
                    if (typeof p[0] == "string") {
                        this.removeItem(player, p[0], p[1]);
                    } else {
                        this.removeItemizerItem(player, p[0], p[1]);
                    }
                }
                if (typeof result[0] == "string") {
                    this.addItem(player, result[0], result[1]);
                } else {
                    this.addItemizerItem(player, result[0], result[1]);
                }
                return true;
            }
        },

        // Use this to check for sponge ItemStack
        // You can use default minecraft items (mods included) by using String id instead of ItemStack ("minecraft:stone" for example).
        hasItem: function (player, itemStack, quantity) {
            if (typeof itemStack == "string")
                itemStack = ItemStack.of(Sponge.getGame().getRegistry()
                    .getType(ItemType.class, itemStack)
                    .orElse(ItemTypes.AIR));
            const check = itemStack.copy();
            check.setQuantity(quantity);
            return player.getInventory().contains(check);
        },

        // Use this to add sponge ItemStack to inventory
        // You can use default minecraft items (mods included) by using String id instead of ItemStack ("minecraft:stone" for example).
        addItem: function (player, itemStack, quantity) {
            if (typeof itemStack == "string")
                itemStack = ItemStack.of(Sponge.getGame().getRegistry()
                    .getType(ItemType.class, itemStack)
                    .orElse(ItemTypes.AIR));
            const stack = itemStack.copy();
            stack.setQuantity(quantity);
            player.getInventory().offer(stack);
        },

        // Use this to remove sponge ItemStack from inventory
        // You can use default minecraft items (mods included) by using String id instead of ItemStack ("minecraft:stone" for example).
        removeItem: function (player, itemStack, quantity) {
            if (typeof itemStack == "string")
                itemStack = ItemStack.of(Sponge.getGame().getRegistry()
                    .getType(ItemType.class, itemStack)
                    .orElse(ItemTypes.AIR));
            const stack = itemStack.copy();
            stack.setQuantity(quantity);
            player.getInventory().query(QueryOperationTypes.ITEM_STACK_IGNORE_QUANTITY.of(stack)).poll(quantity);
        }
    };

    Bindings.getScriptEngine().put("Itemizer", Itemizer);
}