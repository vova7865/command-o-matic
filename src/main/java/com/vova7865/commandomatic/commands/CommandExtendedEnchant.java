package com.vova7865.commandomatic.commands;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandExtendedEnchant extends CommandBase {

	/**
	 * Gets the name of the command
	 */
	public String getName() {
		return "enchant";
	}

	/**
	 * Return the required permission level for this command.
	 */
	public int getRequiredPermissionLevel() {
		return 2;
	}

	/**
	 * Gets the usage string for the command.
	 */
	public String getUsage(ICommandSender sender) {
		return "commandomatic.commands.enchant.usage";
	}
	/**
	 * Callback for when the command is executed
	 */
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 2) {
			throw new WrongUsageException("commandomatic.commands.enchant.usage", new Object[0]);
		} else {
			String mode = args[1];
			EntityLivingBase entitylivingbase = (EntityLivingBase) getEntity(server, sender, args[0],
					EntityLivingBase.class);
			sender.setCommandStat(net.minecraft.command.CommandResultStats.Type.AFFECTED_ITEMS, 0);
			ItemStack itemstack = entitylivingbase.getHeldItemMainhand();
			if (itemstack.isEmpty()) {
				throw new CommandException("commands.enchant.noItem", new Object[0]);
			}
			switch (mode) {
				case "set":
					if (args.length < 3) {
						throw new WrongUsageException("commandomatic.commands.enchant.usage", new Object[0]);
					}
					Enchantment enchantment;

					try {
						enchantment = Enchantment.getEnchantmentByID(parseInt(args[2], 0));
					} catch (NumberInvalidException var12) {
						enchantment = Enchantment.getEnchantmentByLocation(args[2]);
					}

					if (enchantment == null) {
						throw new NumberInvalidException("commands.enchant.notFound", new Object[] { args[2] });
					}
					if (itemstack.isItemEnchanted()) {
						itemstack.getTagCompound().removeTag("ench");
					}
					int level = 1;
					if (args.length >= 4) {
						level = parseInt(args[3], 0, Short.MAX_VALUE);
					}

					itemstack.addEnchantment(enchantment, level);
					notifyCommandListener(sender, this, "commandomatic.commands.enchant.set", new Object[0]);
					sender.setCommandStat(net.minecraft.command.CommandResultStats.Type.AFFECTED_ITEMS, 1);
					break;
				case "add":
					int lvlToAdd = 1;
					if (args.length < 3) {
						throw new WrongUsageException("commandomatic.commands.enchant.usage", new Object[0]);
					}
					Enchantment enchantmentToAdd;

					try {
						enchantmentToAdd = Enchantment.getEnchantmentByID(parseInt(args[2], 0));
					} catch (NumberInvalidException var12) {
						enchantmentToAdd = Enchantment.getEnchantmentByLocation(args[2]);
					}

					if (enchantmentToAdd == null) {
						throw new NumberInvalidException("commands.enchant.notFound", new Object[] { args[2] });
					}

					if (args.length >= 4) {
						lvlToAdd = parseInt(args[3], 0, Short.MAX_VALUE);
					}
					boolean flag = true;
					if (itemstack.isItemEnchanted()) {
						for (NBTBase nbt : itemstack.getTagCompound().getTagList("ench", 10)) {
							if (nbt instanceof NBTTagCompound) {
								NBTTagCompound enchCompound = ((NBTTagCompound) nbt);
								if (enchCompound.getShort("id") == Enchantment.getEnchantmentID(enchantmentToAdd)) {
									enchCompound.setShort("lvl", (short) lvlToAdd);
									flag = false;
								}
							}
						}
					}
					if (flag)
						itemstack.addEnchantment(enchantmentToAdd, lvlToAdd);
					notifyCommandListener(sender, this, "commandomatic.commands.enchant.add", new Object[0]);
					sender.setCommandStat(net.minecraft.command.CommandResultStats.Type.AFFECTED_ITEMS, 1);
					break;
				case "clear":
					if (itemstack.isItemEnchanted()) {
						itemstack.getTagCompound().removeTag("ench");
						notifyCommandListener(sender, this, "commandomatic.commands.enchant.clear", new Object[0]);
						break;
					} else {
						throw new CommandException("commandomatic.commands.enchant.notenchanted", new Object[0]);
					}

				default:
					throw new WrongUsageException("commandomatic.commands.enchant.usage", new Object[0]);
			}
		}
	}

	/**
	 * Get a list of options for when the user presses the TAB key
	 */
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
			@Nullable BlockPos targetPos) {
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
		} else if (args.length == 2) {
			return getListOfStringsMatchingLastWord(args, new String[] { "add", "set", "clear" });
		} else if (args.length == 3 && (args[1].equals("add") || args[1].equals("set"))) {
			return getListOfStringsMatchingLastWord(args, Enchantment.REGISTRY.getKeys());

		} else
			return Collections.emptyList();
	}

	/**
	 * Return whether the specified command parameter index is a username parameter.
	 */
	public boolean isUsernameIndex(String[] args, int index) {
		return index == 0;
	}
}
