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

import com.github.hexocraftapi.nms.NmsChunk;
import com.github.hexocraftapi.nms.utils.NmsChunkUtil;
import com.github.hexocraftapi.nms.utils.NmsWorldUtil;
import com.github.hexocraftapi.util.ChunkUtil;
import com.github.hexocraftapi.util.LocationUtil;
import com.github.hexocraftapi.util.PlayerUtil;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Relighter
{
	private final Set<LightLocation> blocks  = Collections.synchronizedSet(new HashSet<LightLocation>());
	private final Set<Location>      airs    = Collections.synchronizedSet(new HashSet<Location>());
	private final Set<Chunk>         chunks  = Collections.synchronizedSet(new HashSet<Chunk>());


	public Relighter() {
	}

	public boolean setLight(Location location, int intensity)
	{
		// The number of transparent location found
		int count = -airs.size();

		//
		if(isTransparent(location, -1, 0,  0))  addAir(location.clone().add(-1, 0, 0));
		if(isTransparent(location,  1, 0,  0))  addAir(location.clone().add(1, 0, 0));
		if(isTransparent(location,  0, 0, -1))  addAir(location.clone().add(0, 0, -1));
		if(isTransparent(location,  0, 0,  1))  addAir(location.clone().add(0, 0, 1));
		if(location.getY() > 0 && isTransparent(location, 0, -1, 0)) addAir(location.clone().add(0, -1, 0));
		if(location.getY() < 256 && isTransparent(location, 0, 1, 0)) addAir(location.clone().add(0, 1, 0));

		// Update the number of transparent location found
		count += airs.size();

		// Only add location if transparent block exist around
		// the current location
		if(count>0)
			addBlock(location, intensity);

		return count>0;
	}

	public void relight(Location location)
	{
		addChunk(location);
	}

	public int createLight()
	{
		synchronized(blocks)
		{
			synchronized(airs)
			{
				synchronized(chunks)
				{
					for(LightLocation ll : blocks)
						NmsWorldUtil.setBlockLight(ll.getLocation(), ll.getLight());

					for(Location l : airs)
						NmsWorldUtil.relightBlock(l);

					// Update chunck
					for(Chunk chunk : chunks)
					{
						NmsChunk nmsChunk = NmsChunkUtil.initLighting(chunk);
						nmsChunk.setModified(true);
						nmsChunk.sendUpdate(ChunkUtil.getClosePlayers(chunk, PlayerUtil.getOnlinePlayers(), 5));
					}

					// Number of created lights
					int count = blocks.size();

					//
					blocks.clear();
					airs.clear();
					chunks.clear();

					return count;
				}
			}
		}
	}

	public int removeLight()
	{
		synchronized(blocks)
		{
			synchronized(airs)
			{
				synchronized(chunks)
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
						NmsChunk nmsChunk = NmsChunkUtil.initLighting(chunk);
						nmsChunk.setModified(true);
						nmsChunk.sendUpdate(ChunkUtil.getClosePlayers(chunk, PlayerUtil.getOnlinePlayers(), 5));
					}

					// Number of removed lights
					int count = blocks.size();

					//
					blocks.clear();
					airs.clear();
					chunks.clear();

					return count;
				}
			}
		}
	}

	protected synchronized void addBlock(Location location, int light)
	{
		blocks.add(new LightLocation(location, light));

		// Remove transparent location considered
		// as air
		synchronized(airs) {
			if(airs.contains(location))
				airs.remove(location);
		}

		// Chunk that could be affected by light change
		addChunk(location);
	}

	protected synchronized void addChunk(Location location)
	{
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
		addChunk(location);
	}

	private boolean isTransparent(Location location)
	{
		return location.getBlock().getType().isTransparent() || !location.getBlock().getType().isOccluding();
	}

	public boolean isTransparent(Location location, double x, double y, double z)
	{
		return isTransparent(location.clone().add(x, y, z));
	}

	private boolean isAir(Location location)
	{
		return location.getBlock().getType() == Material.AIR;
	}

	public boolean isAir(Location location, double x, double y, double z)
	{
		return isAir(location.clone().add(x, y, z));
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

		@Override
		public boolean equals(Object o)
		{
			if(o == null)
				return false;

			if(o instanceof LightLocation)
				return this.compareTo(o) == 0;

			if(o instanceof Location)
			{
				Location loc = (Location)o;

				if(loc.getBlockX() != location.getBlockX())
					return false;

				if(loc.getBlockY() != location.getBlockY())
					return false;

				if(loc.getBlockZ() != location.getBlockZ())
					return false;

				return true;
			}

			return false;
		}

		@Override
		public int hashCode() {
			int hash = 5;
			hash = 23 * hash + (this.location != null ? this.location.hashCode() : 0);
			hash = 23 * hash + light;
			return hash;
		}
	}
}