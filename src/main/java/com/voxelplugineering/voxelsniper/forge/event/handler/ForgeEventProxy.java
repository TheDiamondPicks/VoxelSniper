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
package com.voxelplugineering.voxelsniper.forge.event.handler;

import com.voxelplugineering.voxelsniper.brush.BrushAction;
import com.voxelplugineering.voxelsniper.entity.Player;
import com.voxelplugineering.voxelsniper.event.SnipeEvent;
import com.voxelplugineering.voxelsniper.event.SniperEvent.SniperCreateEvent;
import com.voxelplugineering.voxelsniper.event.SniperEvent.SniperDestroyEvent;
import com.voxelplugineering.voxelsniper.forge.config.ForgeConfiguration;
import com.voxelplugineering.voxelsniper.forge.service.ForgeSchedulerService;
import com.voxelplugineering.voxelsniper.service.eventbus.EventBus;
import com.voxelplugineering.voxelsniper.service.registry.PlayerRegistry;
import com.voxelplugineering.voxelsniper.service.scheduler.Scheduler;
import com.voxelplugineering.voxelsniper.util.Context;
import net.minecraft.item.Item;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Optional;

/**
 * An event handler for all forge events that proxy to gunsmith events.
 */
public class ForgeEventProxy
{

    private final PlayerRegistry<net.minecraft.entity.player.EntityPlayer> pr;
    private final EventBus bus;
    private final ForgeSchedulerService sched;

    /**
     * Creates a new {@link ForgeEventProxy}.
     * 
     * @param context The context
     */
    @SuppressWarnings({ "unchecked" })
    public ForgeEventProxy(Context context)
    {
        this.pr = context.getRequired(PlayerRegistry.class);
        this.bus = context.getRequired(EventBus.class);
        this.sched = (ForgeSchedulerService) context.getRequired(Scheduler.class);
    }

    /**
     * The Player logged in event, proxies to Gunsmith's {@link SniperCreateEvent}.
     * 
     * @param event The event
     */
    @SubscribeEvent
    public void onSpawn(PlayerEvent.PlayerLoggedInEvent event)
    {
        Optional<Player> s = this.pr.getPlayer(event.player.getName());
        if (s.isPresent())
        {
            SniperCreateEvent sce = new SniperCreateEvent(s.get());
            this.bus.post(sce);
        }
    }

    /**
     * The Player logged out event, proxies to Gunsmith's {@link SniperDestroyEvent}.
     * 
     * @param event The event
     */
    @SubscribeEvent
    public void onSpawn(PlayerEvent.PlayerLoggedOutEvent event)
    {
        Optional<Player> s = this.pr.getPlayer(event.player.getName());
        if (s.isPresent())
        {
            SniperDestroyEvent sde = new SniperDestroyEvent(s.get());
            this.bus.post(sde);
        }
    }

    /**
     * The player interact event, proxies into Gunsmith's {@link SnipeEvent}.
     * 
     * @param event The event
     */
    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event)
    {
        if (event.world.isRemote)
        {
            return;
        }
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)
        {
            BrushAction action;
            if (event.entityPlayer.getCurrentEquippedItem().getItem().equals(Item.getItemById(ForgeConfiguration.primaryMaterial)))
            {
                action = BrushAction.PRIMARY;
            } else if (event.entityPlayer.getCurrentEquippedItem().getItem().equals(Item.getItemById(ForgeConfiguration.altMaterial)))
            {
                action = BrushAction.ALTERNATE;
            } else
            {
                return;
            }
            Optional<Player> s = this.pr.getPlayer(event.entityPlayer.getName());
            if (s.isPresent())
            {
                Player pl = s.get();
                if (pl.isProcessing())
                {
                    return;
                }
                pl.setProcessing(true);
                SnipeEvent se = new SnipeEvent(pl, event.entityPlayer.rotationYawHead, event.entityPlayer.rotationPitch, action);
                this.bus.post(se);
            }
        }
    }

    /**
     * The ServerTick event, used to synchronously tick the player's change-queues.
     * 
     * @param event The event
     */
    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event)
    {
        this.sched.onTick();
    }
}
