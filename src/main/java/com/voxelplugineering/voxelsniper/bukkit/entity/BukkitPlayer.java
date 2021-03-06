/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 The Voxel Plugineering Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.voxelplugineering.voxelsniper.bukkit.entity;

import com.voxelplugineering.voxelsniper.brush.BrushManager;
import com.voxelplugineering.voxelsniper.bukkit.config.BukkitConfiguration;
import com.voxelplugineering.voxelsniper.bukkit.util.BukkitUtilities;
import com.voxelplugineering.voxelsniper.bukkit.world.BukkitWorld;
import com.voxelplugineering.voxelsniper.entity.AbstractPlayer;
import com.voxelplugineering.voxelsniper.entity.EntityType;
import com.voxelplugineering.voxelsniper.service.registry.WorldRegistry;
import com.voxelplugineering.voxelsniper.service.text.TextFormat;
import com.voxelplugineering.voxelsniper.service.text.TextFormatParser;
import com.voxelplugineering.voxelsniper.util.Context;
import com.voxelplugineering.voxelsniper.util.math.Vector3d;
import com.voxelplugineering.voxelsniper.world.World;

import java.util.UUID;

/**
 * A wrapper for bukkit's {@link org.bukkit.entity.Player}s.
 */
public class BukkitPlayer extends AbstractPlayer<org.bukkit.entity.Player>
{

    private final WorldRegistry<org.bukkit.World> worldReg;
    private final TextFormatParser textFormat;

    /**
     * Creates a new {@link BukkitPlayer}.
     * 
     * @param player The player to wrap, cannot be null
     */
    @SuppressWarnings({ "unchecked" })
    public BukkitPlayer(org.bukkit.entity.Player player, BrushManager bm, Context context)
    {
        super(player, bm, context);
        this.worldReg = context.getRequired(WorldRegistry.class);
        this.textFormat = context.getRequired(TextFormatParser.class);
    }

    @Override
    public void sendMessage(String msg)
    {
        if (msg.indexOf('\n') != -1)
        {
            for (String message : msg.split("\n"))
            {
                sendMessage(message);
            }
            return;
        }
        if (msg.length() > BukkitConfiguration.maxMessageSize)
        {
            sendMessage(msg.substring(0, BukkitConfiguration.maxMessageSize));
            sendMessage(msg.substring(BukkitConfiguration.maxMessageSize));
            return;
        }
        getThis().sendMessage(formatMessage(msg));
    }

    private String formatMessage(String msg)
    {
        for (TextFormat format : TextFormat.values())
        {
            msg = msg.replaceAll(format.toString(), this.textFormat.getFormat(format));
        }
        return msg;
    }

    @Override
    public World getWorld()
    {
        return this.worldReg.getWorld(getThis().getWorld().getName()).get();
    }

    @Override
    public String getName()
    {
        return getThis().getName();
    }

    @Override
    public EntityType getType()
    {
        return BukkitUtilities.getEntityType(org.bukkit.entity.EntityType.PLAYER);
    }

    @Override
    public com.voxelplugineering.voxelsniper.world.Location getLocation()
    {
        return BukkitUtilities.getGunsmithLocation(getThis().getLocation(), this.worldReg);
    }

    @Override
    public void setLocation(World world, double x, double y, double z)
    {
        getThis().teleport(new org.bukkit.Location(((BukkitWorld) world).getThis(), x, y, z));
    }

    @Override
    public double getHealth()
    {
        return getThis().getHealth();
    }

    @Override
    public UUID getUniqueId()
    {
        return getThis().getUniqueId();
    }

    @Override
    public void setHealth(double health)
    {
        getThis().setHealth(health);
    }

    @Override
    public double getMaxHealth()
    {
        return getThis().getMaxHealth();
    }

    @Override
    public Vector3d getRotation()
    {
        org.bukkit.Location location = getThis().getLocation();
        return new Vector3d(location.getPitch(), location.getYaw(), 0);
    }

    @Override
    public void setRotation(double pitch, double yaw, double roll)
    {
        getThis().getLocation().setYaw((float) yaw);
        getThis().getLocation().setYaw((float) pitch);
    }

    @Override
    public boolean remove()
    {
        getThis().remove();
        return true;
    }

    @Override
    public double getYaw()
    {
        return getThis().getLocation().getYaw();
    }

    @Override
    public double getPitch()
    {
        return getThis().getLocation().getPitch();
    }

    @Override
    public double getRoll()
    {
        return 0;
    }

}
