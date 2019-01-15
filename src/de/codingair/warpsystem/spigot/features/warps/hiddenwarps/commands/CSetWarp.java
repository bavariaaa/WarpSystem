package de.codingair.warpsystem.spigot.features.warps.hiddenwarps.commands;

import de.codingair.codingapi.player.chat.SimpleMessage;
import de.codingair.codingapi.server.commands.BaseComponent;
import de.codingair.codingapi.server.commands.CommandBuilder;
import de.codingair.codingapi.server.commands.CommandComponent;
import de.codingair.codingapi.server.commands.MultiCommandComponent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Warp;
import de.codingair.warpsystem.spigot.features.warps.hiddenwarps.HiddenWarp;
import de.codingair.warpsystem.spigot.features.warps.hiddenwarps.managers.HiddenWarpManager;
import de.codingair.warpsystem.spigot.features.warps.managers.IconManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CSetWarp extends CommandBuilder {
    public CSetWarp() {
        super("SetWarp", new BaseComponent(WarpSystem.PERMISSION_MODIFY_HIDDEN_WARPS) {
            @Override
            public void noPermission(CommandSender sender, String label, CommandComponent child) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("No_Permission"));
            }

            @Override
            public void onlyFor(boolean player, CommandSender sender, String label, CommandComponent child) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("Only_For_Players"));
            }

            @Override
            public void unknownSubCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " §e<warp>");
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + "§7" + Lang.get("Use") + ": /" + label + " §e<warp>");
                return false;
            }
        }, true);

        setHighestPriority(true);

        getBaseComponent().addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, List<String> suggestions) {
                HiddenWarpManager hManager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.HIDDEN_WARPS);
                for(HiddenWarp value : hManager.getWarps().values()) {
                    suggestions.add(value.getName());
                }

                IconManager iManager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.WARPS);
                for(Warp warp : iManager.getWarps()) {
                    suggestions.add(warp.getIdentifier());
                }
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                HiddenWarpManager hManager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.HIDDEN_WARPS);
                if(hManager.existsWarp(argument)) {
                    //Override
                    return false;
                }

                IconManager iManager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.WARPS);
                if(iManager.getWarp(argument) != null) {
                    //Override
                    return false;
                }

                //create
                HiddenWarp hiddenWarp = new HiddenWarp((Player) sender, argument, null);
                hManager.addWarp(hiddenWarp);

                sender.sendMessage(Lang.getPrefix() + Lang.get("HiddenWarp_Created").replace("%WARP%", ChatColor.translateAlternateColorCodes('&', argument)));

                SimpleMessage message = new SimpleMessage(Lang.getPrefix() + Lang.get("HiddenWarp_Created_Edit_Info"), WarpSystem.getInstance());

                TextComponent tc = new TextComponent(Lang.get("HiddenWarp_Created_Edit_Info_EDIT"));
                tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/edithiddenwarp " + argument));
                tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.BaseComponent[]{new TextComponent(Lang.get("Click_Hover"))}));

                message.replace("%EDIT%", tc);

                message.send((Player) sender);
                return false;
            }
        });
    }
}
