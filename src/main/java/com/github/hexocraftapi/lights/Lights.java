package com.github.hexocraftapi.lights;

/*
 * Copyright 2016 hexosse
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.github.hexocraftapi.lights.relighter.Relighter;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * This file is part of AddLightPlugin
 *
 * @author <b>hexosse</b> (<a href="https://github.com/hexosse">hexosse on GitHub</a>).
 */
public class Lights
{
	private static Relighter relighter = new Relighter();

	public static void createLight(final Location location, final int light)
	{
		List<Location> locations = new ArrayList<>(1);
		locations.add(location);
		Lights.createLight(locations, light);

//		NmsWorldUtil.setLight(location, light);
//
//		if(relighter.isTransparent(location, -1, 0,  0))  NmsWorldUtil.relightBlock(location.clone().add(-1, 0, 0));
//		if(relighter.isTransparent(location,  1, 0,  0))  NmsWorldUtil.relightBlock(location.clone().add(1, 0, 0));
//		if(relighter.isTransparent(location,  0, 0, -1))  NmsWorldUtil.relightBlock(location.clone().add(0, 0, -1));
//		if(relighter.isTransparent(location,  0, 0,  1))  NmsWorldUtil.relightBlock(location.clone().add(0, 0, 1));
//		if(location.getY() > 0 && relighter.isTransparent(location, 0, -1, 0)) NmsWorldUtil.relightBlock(location.clone().add(0, -1, 0));
//		if(location.getY() < 256 && relighter.isTransparent(location, 0, 1, 0)) NmsWorldUtil.relightBlock(location.clone().add(0, 1, 0));
//
//		NmsChunkUtil.sendUpdate(location.getChunk(), PlayerUtil.getOnlinePlayers());
//		NmsChunkUtil.sendUpdate(LocationUtil.getCardinalDistance(location, BlockFace.NORTH, 15).getChunk(), PlayerUtil.getOnlinePlayers());
//		NmsChunkUtil.sendUpdate(LocationUtil.getCardinalDistance(location, BlockFace.NORTH_EAST, 15).getChunk(), PlayerUtil.getOnlinePlayers());
//		NmsChunkUtil.sendUpdate(LocationUtil.getCardinalDistance(location, BlockFace.EAST, 15).getChunk(), PlayerUtil.getOnlinePlayers());
//		NmsChunkUtil.sendUpdate(LocationUtil.getCardinalDistance(location, BlockFace.SOUTH_EAST, 15).getChunk(), PlayerUtil.getOnlinePlayers());
//		NmsChunkUtil.sendUpdate(LocationUtil.getCardinalDistance(location, BlockFace.SOUTH, 15).getChunk(), PlayerUtil.getOnlinePlayers());
//		NmsChunkUtil.sendUpdate(LocationUtil.getCardinalDistance(location, BlockFace.SOUTH_WEST, 15).getChunk(), PlayerUtil.getOnlinePlayers());
//		NmsChunkUtil.sendUpdate(LocationUtil.getCardinalDistance(location, BlockFace.WEST, 15).getChunk(), PlayerUtil.getOnlinePlayers());
//		NmsChunkUtil.sendUpdate(LocationUtil.getCardinalDistance(location, BlockFace.NORTH_WEST, 15).getChunk(), PlayerUtil.getOnlinePlayers());
	}

	public static void createLight(final List<Location> locations, final int light)
    {
        if(locations.size() == 0) return;

	    // Block Relighter
	    for(Location location : locations)
			relighter.setLight(location, light);

	    // Send chunks update
		relighter.createLight();
    }

	public static void removeLight(final Location location)
	{
		List<Location> locations = new ArrayList<>(1);
		locations.add(location);
		Lights.removeLight(locations);

//		NmsWorldUtil.setLight(location, 0);
//
//		if(relighter.isTransparent(location, -1, 0,  0))  NmsWorldUtil.relightBlock(location.clone().add(-1, 0, 0));
//		if(relighter.isTransparent(location,  1, 0,  0))  NmsWorldUtil.relightBlock(location.clone().add(1, 0, 0));
//		if(relighter.isTransparent(location,  0, 0, -1))  NmsWorldUtil.relightBlock(location.clone().add(0, 0, -1));
//		if(relighter.isTransparent(location,  0, 0,  1))  NmsWorldUtil.relightBlock(location.clone().add(0, 0, 1));
//		if(location.getY() > 0 && relighter.isTransparent(location, 0, -1, 0)) NmsWorldUtil.relightBlock(location.clone().add(0, -1, 0));
//		if(location.getY() < 256 && relighter.isTransparent(location, 0, 1, 0)) NmsWorldUtil.relightBlock(location.clone().add(0, 1, 0));
//
//		NmsWorldUtil.relight(location);
//
//		NmsChunkUtil.sendUpdate(location.getChunk(), PlayerUtil.getOnlinePlayers());
//		NmsChunkUtil.sendUpdate(LocationUtil.getCardinalDistance(location, BlockFace.NORTH, 15).getChunk(), PlayerUtil.getOnlinePlayers());
//		NmsChunkUtil.sendUpdate(LocationUtil.getCardinalDistance(location, BlockFace.NORTH_EAST, 15).getChunk(), PlayerUtil.getOnlinePlayers());
//		NmsChunkUtil.sendUpdate(LocationUtil.getCardinalDistance(location, BlockFace.EAST, 15).getChunk(), PlayerUtil.getOnlinePlayers());
//		NmsChunkUtil.sendUpdate(LocationUtil.getCardinalDistance(location, BlockFace.SOUTH_EAST, 15).getChunk(), PlayerUtil.getOnlinePlayers());
//		NmsChunkUtil.sendUpdate(LocationUtil.getCardinalDistance(location, BlockFace.SOUTH, 15).getChunk(), PlayerUtil.getOnlinePlayers());
//		NmsChunkUtil.sendUpdate(LocationUtil.getCardinalDistance(location, BlockFace.SOUTH_WEST, 15).getChunk(), PlayerUtil.getOnlinePlayers());
//		NmsChunkUtil.sendUpdate(LocationUtil.getCardinalDistance(location, BlockFace.WEST, 15).getChunk(), PlayerUtil.getOnlinePlayers());
//		NmsChunkUtil.sendUpdate(LocationUtil.getCardinalDistance(location, BlockFace.NORTH_WEST, 15).getChunk(), PlayerUtil.getOnlinePlayers());
	}

	public static void removeLight(final List<Location> locations)
    {
        if(locations.size() == 0) return;

		// Relighter
	    for(Location location : locations)
			relighter.setLight(location, 0);

		// Send chunks update
		relighter.removeLight();
    }

	public static void relight(final Location location)
	{
		List<Location> locations = new ArrayList<>(1);
		locations.add(location);
		Lights.relight(locations);
	}

	public static void relight(final List<Location> locations)
	{
		if(locations.size() == 0) return;

		// Block Relighter
		for(Location location : locations)
			relighter.relight(location);

		// Send chunks update
		relighter.createLight();
	}
}