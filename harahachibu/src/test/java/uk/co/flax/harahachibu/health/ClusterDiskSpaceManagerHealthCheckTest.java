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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.co.flax.harahachibu.services.ClusterDiskSpaceManager;
import uk.co.flax.harahachibu.services.data.DiskSpace;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the ClusterDiskSpaceManagerHealthCheck.
 * <p>
 * Created by mlp on 19/04/16.
 */
public class ClusterDiskSpaceManagerHealthCheckTest {

	private final ClusterDiskSpaceManager manager = mock(ClusterDiskSpaceManager.class);

	private final String[] serverNames = new String[]{"server1", "server2"};
	private final Set<String> servers = new LinkedHashSet<>(Arrays.asList(serverNames));
	private final Map<String, DiskSpace> diskSpaceMap = new HashMap<>();
	private ClusterDiskSpaceManagerHealthCheck healthCheck;

	@Before
	public void setup() {
		healthCheck = new ClusterDiskSpaceManagerHealthCheck(manager);

		when(manager.getServers()).thenReturn(servers);
		when(manager.getDiskSpace()).thenReturn(diskSpaceMap);
	}

	@After
	public void tearDown() {
		diskSpaceMap.clear();
	}

	@Test
	public void returnsUnhealthyWhenDiskStatsMissing() throws Exception {
		diskSpaceMap.put(serverNames[0], new DiskSpace(123L, 1024L));

		HealthCheck.Result result = healthCheck.check();
		assertThat(result.isHealthy()).isFalse();
	}

	@Test
	public void returnsUnhealthyWhenDiskStatsOutOfDate() throws Exception {
		diskSpaceMap.put(serverNames[0], new DiskSpace(123L, 1024L));
		DiskSpace disk2 = mock(DiskSpace.class);
		when(disk2.getCreationDate()).thenReturn(new Date(
				System.currentTimeMillis() - ((ClusterDiskSpaceManagerHealthCheck.EXPIRY_TIME_MINS + 1) * 60 * 1000)));
		diskSpaceMap.put(serverNames[1], disk2);

		HealthCheck.Result result = healthCheck.check();
		assertThat(result.isHealthy()).isFalse();
	}

	@Test
	public void returnsHealthyWhenDiskStatsAvailable() throws Exception {
		diskSpaceMap.put(serverNames[0], new DiskSpace(123L, 1024L));
		diskSpaceMap.put(serverNames[1], new DiskSpace(456L, 4560L));

		HealthCheck.Result result = healthCheck.check();
		assertThat(result.isHealthy()).isTrue();
	}

}
