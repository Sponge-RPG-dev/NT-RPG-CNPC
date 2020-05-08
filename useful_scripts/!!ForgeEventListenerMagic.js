/**
 * Script to subscribe to Forge events, since Sponge and Forge events are different things (!!!).
 * To make it work properly, follow this steps:
 * 1 (!!!).
 *    Add 'generateForgeListener()' line to the end of generateListener() function in Main.js (to apply same logic as Sponge listeners)
 *
 * 2. Example how to register Forge event listener:
 *
 *     registerForgeEventListener({
 *         type: "net.minecraftforge.event.entity.player.EntityItemPickupEvent",
 *         consumer: function(event) {
 *             event.setCancelled(true); // cancel the all item-pickup events
 *         },
 *         priority: "NORMAL"
 *     });
 *
 * 3. "priority" parameter is optional, but can be:
 *     HIGHEST, //First to execute
 *     HIGH,
 *     NORMAL,
 *     LOW,
 *     LOWEST //Last to execute
 */
{
    /*
     *  IMPORTS
     */
    const FORGE_EVENT_LISTENER_GENERATOR = Java.type("ru.glassspirit.cnpcntrpg.forge.ForgeEventListenerGenerator");

    /*
     *  CONSTANTS
     */
    const FORGE_EVENTS = new ArrayList();

    /*
     *  LOGIC
     */
    function registerForgeEventListener(eventData) {
        if (eventData == null) {
            log("Could not register ForgeEvent listener defined via JS, param EventData is null");
            return;
        }
        if (eventData.type == null) {
            log("Could not register ForgeEvent listener defined via JS, param EventData.type is null");
            return;
        }
        if (eventData.consumer == null) {
            log("Could not register ForgeEvent listener defined via JS, param EventData.consumer is null");
            return;
        }
        FORGE_EVENTS.add(eventData);
    }

    function generateForgeListener() {
        if (!FORGE_EVENTS.isEmpty()) {
            log("generateForgeListener");
            FORGE_EVENT_LISTENER_GENERATOR.registerDynamicListener(FORGE_EVENTS);
        }
        FORGE_EVENTS.clear();
    }

    Bindings.getScriptEngine().put("registerForgeEventListener", registerForgeEventListener);
    Bindings.getScriptEngine().put("generateForgeListener", generateForgeListener);
}