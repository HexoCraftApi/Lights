package com.github.hexocraftapi.lights.relighter;

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

import com.github.hexocraftapi.nms.utils.NmsChunkUtil;
import com.github.hexocraftapi.nms.utils.NmsWorldUtil;
import com.github.hexocraftapi.util.LocationUtil;
import com.github.hexocraftapi.util.PlayerUtil;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <b>Hexosse</b> (<a href="https://github.com/hexosse">on GitHub</a>))
 */
public class BlockRelighter
{
	private final Set<LightLocation> blocks  = Collections.synchronizedSet(new HashSet<LightLocation>());
	private final Set<Location>      airs    = Collections.synchronizedSet(new HashSet<Location>());
	private final Set<Chunk>         chunks  = Collections.synchronizedSet(new HashSet<Chunk>());


	public BlockRelighter() {
	}

	public void setBlockLight(Block block, int light)
	{
		Location location = block.getLocation();

		addBlock(location, light);

		if(isTransparent(location, -1, 0,  0))  addAir(location.clone().add(-1, 0, 0));
		if(isTransparent(location,  1, 0,  0))  addAir(location.clone().add(1, 0, 0));
		if(isTransparent(location,  0, 0, -1))  addAir(location.clone().add(0, 0, -1));
		if(isTransparent(location,  0, 0,  1))  addAir(location.clone().add(0, 0, 1));
		if(location.getY() > 0 && isTransparent(location, 0, -1, 0)) addAir(location.clone().add(0, -1, 0));
		if(location.getY() < 256 && isTransparent(location, 0, 1, 0)) addAir(location.clone().add(0, 1, 0));
	}

	public void createLight()
	{
		for(LightLocation ll : blocks)
			NmsWorldUtil.setBlockLight(ll.getLocation(), ll.getLight());

		for(Location l : airs)
			NmsWorldUtil.relightBlock(l);

		// Update chunck
		for(Chunk chunk : chunks)
		{
			NmsChunkUtil.initLighting(chunk);
			NmsChunkUtil.sendUpdate(chunk, PlayerUtil.getOnlinePlayers());
		}

		//
		blocks.clear();
		airs.clear();
		chunks.clear();
	}

	public void removeLight()
	{
		for(LightLocation ll : blocks)
			NmsWorldUtil.setBlockLight(ll.getLocation(), 0);

		for(Location l : airs)
			NmsWorldUtil.relightSky(l);

		for(Location l : airs)
			NmsWorldUtil.relightBlock(l);

		// Update chunck
		for(Chunk chunk : chunks)
		{
			NmsChunkUtil.initLighting(chunk);
			NmsChunkUtil.sendUpdate(chunk, PlayerUtil.getOnlinePlayers());
		}

		//
		blocks.clear();
		airs.clear();
		chunks.clear();
	}

	protected synchronized void addBlock(Location location, int light)
	{
		blocks.add(new LightLocation(location, light));

		// Chunk that could be affected by light change
		chunks.add(location.getChunk());
		chunks.add(LocationUtil.getCardinalDistance(location, BlockFace.NORTH, 15).getChunk());
		chunks.add(LocationUtil.getCardinalDistance(location, BlockFace.NORTH_EAST, 15).getChunk());
		chunks.add(LocationUtil.getCardinalDistance(location, BlockFace.EAST, 15).getChunk());
		chunks.add(LocationUtil.getCardinalDistance(location, BlockFace.SOUTH_EAST, 15).getChunk());
		chunks.add(LocationUtil.getCardinalDistance(location, BlockFace.SOUTH, 15).getChunk());
		chunks.add(LocationUtil.getCardinalDistance(location, BlockFace.SOUTH_WEST, 15).getChunk());
		chunks.add(LocationUtil.getCardinalDistance(location, BlockFace.WEST, 15).getChunk());
		chunks.add(LocationUtil.getCardinalDistance(location, BlockFace.NORTH_WEST, 15).getChunk());
	}

	protected synchronized void addAir(Location location)
	{
		airs.add(location);

		// Chunk that could be affected by light change
		chunks.add(location.getChunk());
		chunks.add(LocationUtil.getCardinalDistance(location, BlockFace.NORTH, 15).getChunk());
		chunks.add(LocationUtil.getCardinalDistance(location, BlockFace.NORTH_EAST, 15).getChunk());
		chunks.add(LocationUtil.getCardinalDistance(location, BlockFace.EAST, 15).getChunk());
		chunks.add(LocationUtil.getCardinalDistance(location, BlockFace.SOUTH_EAST, 15).getChunk());
		chunks.add(LocationUtil.getCardinalDistance(location, BlockFace.SOUTH, 15).getChunk());
		chunks.add(LocationUtil.getCardinalDistance(location, BlockFace.SOUTH_WEST, 15).getChunk());
		chunks.add(LocationUtil.getCardinalDistance(location, BlockFace.WEST, 15).getChunk());
		chunks.add(LocationUtil.getCardinalDistance(location, BlockFace.NORTH_WEST, 15).getChunk());
	}

	private boolean isTransparent(Location location)
	{
		return location.getBlock().getType().isTransparent() || !location.getBlock().getType().isOccluding();
	}

	public boolean isTransparent(Location location, double x, double y, double z)
	{
		return isTransparent(location.clone().add(x, y, z));
	}

	private class LightLocation implements Comparable {
		public final Location location;
		public final int      light;

		public LightLocation(Location location, int light) {
			this.location = location;
			this.light = light < 0 ? 0 : (light > 15 ? 15 : light);
		}

		public Location getLocation() { return location; }
		public int getLight() { return light; }

		@Override
		public int compareTo(Object o)
		{
			LightLocation other = (LightLocation) o;

			if(other.location.getBlockX() < location.getBlockX())
				return -1;
			if(other.location.getBlockX() > location.getBlockX())
				return 1;

			if(other.location.getBlockY() < location.getBlockY())
				return -1;
			if(other.location.getBlockY() > location.getBlockY())
				return 1;

			if(other.location.getBlockZ() < location.getBlockZ())
				return -1;
			if(other.location.getBlockZ() > location.getBlockZ())
				return 1;

			if(other.light < light)
				return -1;
			if(other.light > light)
				return 1;

			return 0;
		}
	}
}
