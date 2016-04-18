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
package uk.co.flax.harahachibu.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.flax.harahachibu.services.ClusterDiskSpaceManager;
import uk.co.flax.harahachibu.services.DiskSpaceChecker;
import uk.co.flax.harahachibu.services.DiskSpaceCheckerException;
import uk.co.flax.harahachibu.services.DiskSpaceThreshold;
import uk.co.flax.harahachibu.services.data.DiskSpace;

import javax.ws.rs.client.Client;
import java.util.Map;

/**
 * Disk space manager for clustered servers.
 * <p>
 * Created by mlp on 18/04/16.
 */
public class ClusterDiskSpaceChecker implements DiskSpaceChecker {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClusterDiskSpaceChecker.class);

	public static final String CLUSTER_SERVERS_CONFIG_OPTION = "clusterServers";

	private final ClusterDiskSpaceManager clusterManager;
	private DiskSpaceThreshold threshold;

	public ClusterDiskSpaceChecker(ClusterDiskSpaceManager clusterManager) {
		this.clusterManager = clusterManager;
	}

	@Override
	public boolean isSpaceAvailable() throws DiskSpaceCheckerException {
		Map<String, DiskSpace> clusterSpace = clusterManager.getDiskSpace();
		boolean outsideThreshold = false;

		for (String server : clusterManager.getServers()) {
			DiskSpace ds = clusterSpace.get(server);
			if (ds == null) {
				LOGGER.warn("No DiskSpace details for {} - returning false", server);
				outsideThreshold = true;
				break;
			} else if (!threshold.withinThreshold(ds)) {
				LOGGER.debug("Server {} does not have enough space", server);
				outsideThreshold = true;
			}
		}

		return !outsideThreshold;
	}

	@Override
	public void configure(Map<String, Object> configuration) throws DiskSpaceCheckerException {
		// NO_OP
	}

	@Override
	public boolean requiresHttpClient() {
		return false;
	}

	@Override
	public void setHttpClient(Client httpClient) {
		// NO_OP
	}

	@Override
	public void setThreshold(DiskSpaceThreshold threshold) {
		this.threshold = threshold;
	}

}
