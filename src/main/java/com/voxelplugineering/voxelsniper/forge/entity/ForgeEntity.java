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
package com.voxelplugineering.voxelsniper.forge.entity;

import com.voxelplugineering.voxelsniper.entity.AbstractEntity;
import com.voxelplugineering.voxelsniper.entity.EntityType;
import com.voxelplugineering.voxelsniper.forge.util.ForgeUtilities;
import com.voxelplugineering.voxelsniper.forge.world.ForgeWorld;
import com.voxelplugineering.voxelsniper.service.registry.WorldRegistry;
import com.voxelplugineering.voxelsniper.util.Context;
import com.voxelplugineering.voxelsniper.util.math.Vector3d;
import com.voxelplugineering.voxelsniper.world.CommonLocation;
import com.voxelplugineering.voxelsniper.world.Location;
import com.voxelplugineering.voxelsniper.world.World;

import java.util.UUID;

/**
 * A wrapper for a forge entity.
 */
public class ForgeEntity extends AbstractEntity<net.minecraft.entity.Entity>
{

    private final EntityType type;
    private final WorldRegistry<org.bukkit.World> worldReg;

    /**
     * Creates a new {@link ForgeEntity}.
     * 
     * @param entity The entity to wrap
     */
    @SuppressWarnings({ "unchecked" })
    public ForgeEntity(net.minecraft.entity.Entity entity, Context context)
    {
        super(entity);
        this.type = ForgeUtilities.getEntityType(entity.getClass());
        this.worldReg = context.getRequired(WorldRegistry.class);
    }

    @Override
    public World getWorld()
    {
        return this.worldReg.getWorld(getThis().worldObj.getProviderName()).get();
    }

    @Override
    public String getName()
    {
        return getThis().getDisplayName().getUnformattedText();
    }

    @Override
    public EntityType getType()
    {
        return this.type;
    }

    @Override
    public Location getLocation()
    {
        return new CommonLocation(this.getWorld(), getThis().posX, getThis().posY, getThis().posZ);
    }

    @Override
    public void setLocation(World world, double x, double y, double z)
    {
        if (world instanceof ForgeWorld)
        {
            getThis().setWorld(((ForgeWorld) world).getThis());
            getThis().setPositionAndUpdate(x, y, z);
        }
    }

    @Override
    public UUID getUniqueId()
    {
        return getThis().getUniqueID();
    }

    @Override
    public Vector3d getRotation()
    {
        return new Vector3d(getThis().rotationYaw, getThis().rotationPitch, 0);
    }

    @Override
    public void setRotation(double pitch, double yaw, double roll)
    {
        getThis().setRotationYawHead((float) yaw);
        getThis().rotationPitch = (float) pitch;
    }

    @Override
    public boolean remove()
    {
        getThis().worldObj.removeEntity(getThis());
        return true;
    }

    @Override
    public double getYaw()
    {
        return getThis().rotationYaw;
    }

    @Override
    public double getPitch()
    {
        return getThis().rotationPitch;
    }

    @Override
    public double getRoll()
    {
        return 0;
    }

}
