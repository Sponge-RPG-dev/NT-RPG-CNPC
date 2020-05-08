/**
 * Script to create simple dialogs on the fly!
 * To make it work properly, follow this steps:
 * 1. Create empty dialog using MD.d() inside CNPC or NT-RPG script
 *
 * 2. Configure this dialog using cool builder style
 * Example:
 * var dialog = MD.d().text("Hello traveller!").addText("How are you?").addText("Pleace take a seat!\nIts pretty hot today...");
 *
 * 3. Configure and add dialog options using MD.o()
 * Example:
 * MD.o().title("Dialog 5").dialog(5).color(0xFF0000).addTo(dialog); // Red colored option that opens dialog with Id = 5 (for colors see RGB -> HEX color converter)
 * MD.o().title("Command").command("say Hello").addTo(dialog); // Option that executes command
 * MD.o().title("Script").script("log('I am script, yay')").addTo(dialog); // Option that executes script (most advanced and usefull feature!). Available variables are: dialog, player, npc
 *
 * 4. Show the new cool dialog to player using dialog.show(player, npc). If you have no npc (showing dialog from NT-RPG script), just specify NPC name.
 * Example:
 * dialog.show(event.player, event.npc); // From CNPC script
 * dialog.show(player) or dialog.show(player, "NpcName"); // From NT-RPG script
 */
{
    /*
     *	IMPORTS
     */
    const NpcDialog = Java.type("noppes.npcs.controllers.data.Dialog");
    const NpcDialogOption = Java.type("noppes.npcs.controllers.data.DialogOption");
    const NpcEntityDialogNpc = Java.type("noppes.npcs.entity.EntityDialogNpc");

    /*
     *	LOGIC
     */
    const MagicDialog = {
        d: function () {
            return {
                d: new NpcDialog(null),
                availability: this.d.availability,
                text: function (t) {
                    if (t !== undefined) {
                        this.d.text = t;
                        return this;
                    } else return this.d.text;
                },
                addText: function (t) {
                    this.d.text += ("\n" + t);
                    return this;
                },
                addOption: function (option) {
                    let options = this.d.getClass().getField("options").get(this.d);
                    option.slot = options.size();
                    options.put(options.size(), option);
                },
                setQuest: function (questId) {
                    this.d.getClass().getField("quest").set(this.d, questId);
                },
                show: function (player, npc) {
                    MagicDialog.show(player, this.d, npc)
                }
            };
        },

        o: function () {
            return {
                o: new NpcDialogOption(),
                title: function (t) {
                    this.o.title = t;
                    return this;
                },
                script: function (s) {
                    this.o.setScript(s);
                    this.o.optionType = 5;
                    return this;
                },
                command: function (c) {
                    this.o.command = c;
                    this.o.optionType = 4;
                    return this;
                },
                dialog: function (d) {
                    this.o.dialogId = d;
                    this.o.optionType = 1;
                    return this;
                },
                color: function (c) {
                    this.o.optionColor = c;
                    return this;
                },
                addTo: function (d) {
                    d.addOption(this.o)
                }
            };
        },

        show: function (player, dialog, npc) {
            if (dialog.availability.isAvailable(player)) {
                const pl = _mc(player);
                if (npc === undefined || typeof npc == "string") {
                    const dummy = new NpcEntityDialogNpc(pl.getWorld());
                    dummy.display.setName(npc === undefined ? "" : npc);

                    npc = dummy;
                }
                Java.type("noppes.npcs.NoppesUtilServer").openDialog(pl, _mc(npc), dialog);
            }
        }
    };

    Bindings.getScriptEngine().put("MagicDialog", MagicDialog);
    Bindings.getScriptEngine().put("MD", MagicDialog);
}