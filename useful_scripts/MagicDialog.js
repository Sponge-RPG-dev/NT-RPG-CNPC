//***IMPORTS
var NpcDialog = Java.type("noppes.npcs.controllers.data.Dialog")
var NpcDialogOption = Java.type("noppes.npcs.controllers.data.DialogOption");
var NpcEntityDialogNpc = Java.type("noppes.npcs.entity.EntityDialogNpc");
//***

var MagicDialog = {
    d: function () {
        return {
            d: new NpcDialog(null),
            text: function (t) {
                if (t !== undefined) {
                    this.d.text = t;
                    return this;
                } else return this.d.text;
            },
            addText: function (t) {
                this.d.text += t;
                return this;
            },
            addOption: function (option) {
                var options = this.d.getClass().getField("options").get(this.d);
                option.slot = options.size();
                options.put(options.size(), option);
            },
            show: function (player, npcName) {
                MagicDialog.show(player, this.d, npcName)
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
                return this;
            },
            command: function (c) {
                this.o.command = c;
                this.o.optionType = 4;
                return this;
            },
            color: function (c) {
                this.o.color = c;
                return this;
            },
            dialog: function (d) {
                this.o.dialog = d;
                this.o.optionType = 1;
                return this;
            },
            addTo: function (d) {
                d.addOption(this.o)
            }
        };
    },

    show: function (player, dialog, npcName) {
        if (dialog.availability.isAvailable(player)) {
            var pl = _mc(player);
            var dummy = new NpcEntityDialogNpc(pl.getWorld());
            dummy.display.setName(npcName === undefined ? "" : npcName);
            Java.type("noppes.npcs.NoppesUtilServer").openDialog(pl, dummy, dialog);
        }
    }
};

Bindings.getScriptEngine().put("MagicDialog", MagicDialog);
Bindings.getScriptEngine().put("MD", MagicDialog);
