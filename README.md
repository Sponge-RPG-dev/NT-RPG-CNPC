
# CustomNPCs NT-RPG plugin
This mod/sponge plugin allows you to combine two wonderful things: CustomNPCs mod and NT-RPG plugin, and also adds new useful features.
## Feature list
1. Gaining NT-RPG experience for killing mobs (PVE exp source).
2. Gaining NT-RPG experience for completing quests (QUESTING exp source).
3. CustomNPCs scripts now have access to all NT-RPG scripts bindings. (MIXINS)
To give access to new variables or functions, use this
```javascript
var yourThing = {/*something*/};
Bindings.getScriptEngine().put("YourThing", yourThing);
// Now you can use it in any CNPC script 
```
4. Moved `NpcEvent.DamagedEvent` after NT-RPG damage calculations, alse `DAMAGE_CHECK` damage type doesn't trigger this event. (MIXINS)
5. [Itemizer plugin](https://github.com/OnapleRPG/Itemizer) support. To use it, just name any item like this `#IT#id#yourid` or `#IT#pool#yourid` and place into NPC drop inventory.
## Configuration
==TODO==
## Installation
Plugin requires [CustomNPCs mod by Noppes](https://www.curseforge.com/minecraft/mc-mods/custom-npcs) and [NT-RPG plugin by NeumimTo](https://github.com/Sponge-RPG-dev/NT-RPG) to be installed and configured on sponge server.
This project uses [Sponge Mixins](https://github.com/SpongePowered/Mixin). Mixins allow to add new things to existing mods (even without open source code, which is the CustomNPCs mod) or change the mechanics without much effort. But to make them work, you need to perform some manipulations.
If you don't want to use mixins features, just use the basic version.
### Basic version
For basic version grab the latest release from github and drop to plugins folder.
### Mixin version
To make mixin version work, you should grab the latest release marked MIXINS, put it in mods folder and remove CustomNPCs mod (mixins version already contains it).
If you do everything right, you shoud see `CNPC NT-RPG: Mixins are used, new features await!` line in server log.
## Building (for developers)
Project uses CustomNPCs mod code to work, but it is not opensource, so I can't store it in the repository.
Clone the repository and setup forge workspace. Then, to make it compile, grab latest release of CustomNPCs mod from curseforge, deobfuscate it (with [BON](https://ci.tterrag.com/job/BON2/) for example), put it in the folder `libs` in the root of the project and allow forge do its job. You should see that CustomNPCs is now added to your project.

To run the server inside you IDE (for debugging), you should add the following line to Program arguments inside run configuration:
`--tweakClass org.spongepowered.asm.launch.MixinTweaker --mixin mixins.cnpcntrpg.json`
This would make mixins work in dev environment.
## Afterwords
If you need any support, just contact me (GlassSpirit) or NeumimTo at [discord server](https://discordapp.com/invite/YerUbgd). 
