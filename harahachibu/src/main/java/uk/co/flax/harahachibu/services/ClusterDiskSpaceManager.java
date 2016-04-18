/**
 * Copyright (c) 2016 Lemur Consulting Ltd.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.flax.harahachibu.services;

import uk.co.flax.harahachibu.services.data.DiskSpace;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manager class for tracking disk space across a cluster.
 *
 * Created by mlp on 18/04/16.
 */
public class ClusterDiskSpaceManager {

	private final Set<String> servers;
	private final Map<String, DiskSpace> diskSpaceMap = new ConcurrentHashMap<>();

	/**
	 * Construct the ClusterDiskSpaceManager.
	 * @param servers the set of valid servers for the cluster.
	 */
	public ClusterDiskSpaceManager(Set<String> servers) {
		this.servers = servers;
	}

	/**
	 * Set the disk space for a server.
	 * @param server the server.
	 * @param space the DiskSpace object holding the disk space details.
	 * @throws DiskSpaceCheckerException if the server is not in the server list
	 * used to construct the manager.
	 */
	public void setDiskSpace(String server, DiskSpace space) throws DiskSpaceCheckerException {
		if (!servers.contains(server)) {
			throw new DiskSpaceCheckerException("Unrecognised server " + server);
		}

		diskSpaceMap.put(server, space);
	}

	/**
	 * Get the current map of server - disk space statuses.
	 * @return the map.
	 */
	public Map<String, DiskSpace> getDiskSpace() {
		return diskSpaceMap;
	}

}
