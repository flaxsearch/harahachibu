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
package uk.co.flax.harahachibu.health;

import com.codahale.metrics.health.HealthCheck;
import uk.co.flax.harahachibu.services.ClusterDiskSpaceManager;
import uk.co.flax.harahachibu.services.data.DiskSpace;

import java.util.Date;
import java.util.Set;

/**
 * HealthCheck for the ClusterDiskSpaceManager.
 *
 * Created by mlp on 19/04/16.
 */
public class ClusterDiskSpaceManagerHealthCheck extends HealthCheck {

	static final int EXPIRY_TIME_MINS = 5;

	private final ClusterDiskSpaceManager manager;

	public ClusterDiskSpaceManagerHealthCheck(ClusterDiskSpaceManager manager) {
		this.manager = manager;
	}

	@Override
	protected Result check() throws Exception {
		final Result result;

		StringBuilder message = new StringBuilder();
		boolean healthy = true;

		for (String server : manager.getServers()) {
			if (message.length() > 0) {
				message.append("; ");
			}
			if (manager.getDiskSpace().get(server) == null) {
				message.append(server).append(" : no disk space recorded");
				healthy = false;
			} else {
				DiskSpace disk = manager.getDiskSpace().get(server);
				Date checkDate = new Date(System.currentTimeMillis() - (EXPIRY_TIME_MINS * 60 * 1000));
				long updateInterval = (System.currentTimeMillis() - disk.getCreationDate().getTime()) / 1000;
				if (disk.getCreationDate().before(checkDate)) {
					healthy = false;
				}
				message.append(server).append(" : last updated ").append(updateInterval).append(" seconds ago");
			}
		}
		message.append(".");

		if (healthy) {
			result = Result.healthy(message.toString());
		} else {
			result = Result.unhealthy(message.toString());
		}

		return result;
	}
}
