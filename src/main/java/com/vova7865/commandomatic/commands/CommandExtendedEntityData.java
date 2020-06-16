package com.vova7865.commandomatic.commands;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandExtendedEntityData extends CommandBase {

	@Override
	public String getName() {
		return "entitydata";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "commands.entitydata.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 2) {
			throw new WrongUsageException("commands.entitydata.usage", new Object[0]);
		} else {
			Entity entity = getEntity(server, sender, args[0]);
			NBTTagCompound nbttagcompound = entityToNBT(entity);
			NBTTagCompound nbttagcompound1 = nbttagcompound.copy();
			NBTTagCompound nbttagcompound2;

			try {
				nbttagcompound2 = JsonToNBT.getTagFromJson(buildString(args, 1));
			} catch (NBTException nbtexception) {
				throw new CommandException("commands.entitydata.tagError", new Object[] { nbtexception.getMessage() });
			}

			UUID uuid = entity.getUniqueID();
			nbttagcompound.merge(nbttagcompound2);
			entity.setUniqueId(uuid);

			if (nbttagcompound.equals(nbttagcompound1)) {
				throw new CommandException("commands.entitydata.failed", new Object[] { nbttagcompound.toString() });
			} else {
				entity.readFromNBT(nbttagcompound);
				notifyCommandListener(sender, this, "commands.entitydata.success",
						new Object[] { nbttagcompound.toString() });
			}

		}
	}
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
			@Nullable BlockPos targetPos) {
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames())
				: Collections.emptyList();
	}
	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return index == 0;
	}

}
