package com.vova7865.commandomatic.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameType;

public class CommandExtendedPublish extends CommandBase {
	/**
	 * Gets the name of the command
	 */
	public String getName() {
		return "publish";
	}

	/**
	 * Gets the usage string for the command.
	 */
	public String getUsage(ICommandSender sender) {
		return "commandomatic.commands.publish.usage";
	}
	public GameType parseGm(String attempt) {
		GameType gm = GameType.NOT_SET;
		try {
			gm = GameType.parseGameTypeWithDefault(parseInt(attempt), GameType.NOT_SET);
		} catch (NumberInvalidException e) {
			gm = GameType.parseGameTypeWithDefault(attempt, GameType.NOT_SET);
		}

		return gm;
	}
	/**
	 * Callback for when the command is executed
	 */
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		boolean cheats = false;
		GameType gm = server.getGameType();
		if (args.length >= 1) {
			gm = parseGm(args[0]);
		}
		if (args.length >= 2) {
			cheats = Boolean.parseBoolean(args[1]);
		}
		String s = server.shareToLAN(gm, cheats);

		if (s != null) {
			notifyCommandListener(sender, this, "commands.publish.started", new Object[] { s });
		} else {
			notifyCommandListener(sender, this, "commands.publish.failed", new Object[0]);
		}
	}
}
