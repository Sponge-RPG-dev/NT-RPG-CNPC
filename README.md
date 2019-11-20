# CustomNPCs NT-RPG plugin
This mod/sponge plugin allows you to combine two wonderful things: CustomNPCs mod and NT-RPG plugin, and also adds new useful features.
## Feature list
==TODO==
## Configuration
==TODO==
## Installation
Plugin requires [CustomNPCs mod by Noppes](https://www.curseforge.com/minecraft/mc-mods/custom-npcs) and [NT-RPG plugin by NeumimTo](https://github.com/Sponge-RPG-dev/NT-RPG) to be installed and configured on sponge server.
This project uses [Sponge Mixins](https://github.com/SpongePowered/Mixin). Mixins allow to add new things to existing mods (even without open source code, which is the CustomNPCs mod) or change the mechanics without much effort. But to make them work, you need to perform some manipulations.
If you don't want to use mixins features, just use the basic version.
### Basic version
For basic version just grab the latest release from github and drop to plugins folder.
### Mixin version
To make mixin version work, you should:
1. Once downloaded open both CustomNPCs and CNPC NT-RPG plugin .jar with archivator.

2. Inside the CustomNPCs file navigate to the META-INF folder and extract the MANIFEST.MF file to any location.
3. Move everything from CNPC NT-RPG .jar file to CustomNPCs mod .jar file. You don't need to place CNPC NT-RPG file anywhere afterwards as it is no longer needed.
4. Open the MANIFEST.MF file you extracted from CustomNPCs in step 2 with any text editor and modify the file by adding the following magic lines:
```
TweakClass: org.spongepowered.asm.launch.MixinTweaker
TweakOrder: 0
MixinConfigs: mixins.cnpcntrpg.json
FMLCorePluginContainsFMLMod: true
ForceLoadAsMod: true
```
5. Replace the MANIFEST.MF within META-INF folder within the modified CustomNPC .jar file created in steop 2 with the modified MANIFEST.MF file created in step 4. 

If you do everything right, you shoud see `CNPC NT-RPG: Mixins are used, new features await, YaY!` line in server log.
## Building (for developers)
Project uses CustomNPCs mod code to work, but it is not opensource, so I can't store it in the repository.
Clone the repository and setup forge workspace. Then, to make it compile, grab latest release of CustomNPCs mod from curseforge, deobfuscate it (with [BON](https://ci.tterrag.com/job/BON2/) for example), put it in the folder `libs` in the root of the project and allow forge do its job. You should see that CustomNPCs is now added to your project.

To run the server inside you IDE (for debugging), you should add the following line to Program arguments inside run configuration:
`--tweakClass org.spongepowered.asm.launch.MixinTweaker --mixin mixins.cnpcntrpg.json`
This would make mixins work in dev environment.
## Afterwords
If you need any support, just contact me (GlassSpirit) or NeumimTo at [discord server](https://discordapp.com/invite/YerUbgd). 
