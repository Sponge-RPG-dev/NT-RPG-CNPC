/*
With CNPC-NTRPG plugin CustomNPCs scripts have access to all NT-RPG scripts bindings.
To give access to new variables or functions, use this

var yourThing = {...};
Bindings.getScriptEngine().put("YourThing", yourThing);
*/

// java things
Bindings.getScriptEngine().put("UUID", Java.type("java.util.UUID"));

// sponge things
Bindings.getScriptEngine().put("Sponge", Java.type("org.spongepowered.api.Sponge"));
