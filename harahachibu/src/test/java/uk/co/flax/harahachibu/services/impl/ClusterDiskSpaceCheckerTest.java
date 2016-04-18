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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.co.flax.harahachibu.services.ClusterDiskSpaceManager;
import uk.co.flax.harahachibu.services.DiskSpaceThreshold;
import uk.co.flax.harahachibu.services.data.DiskSpace;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the ClusterDiskSpaceChecker implementation.
 * Created by mlp on 18/04/16.
 */
public class ClusterDiskSpaceCheckerTest {

	private final String[] servers = new String[]{ "192.168.0.1", "192.168.0.2" };
	private final ClusterDiskSpaceManager clusterManager = mock(ClusterDiskSpaceManager.class);
	private final Map<String, DiskSpace> spaceMap = new HashMap<>();
	private final DiskSpaceThreshold threshold = mock(DiskSpaceThreshold.class);

	private ClusterDiskSpaceChecker checker;

	@Before
	public void setup() {
		checker = new ClusterDiskSpaceChecker(clusterManager);
		checker.setThreshold(threshold);
		// Use LinkedHashSet for predictable ordering
		when(clusterManager.getServers()).thenReturn(new LinkedHashSet<>(Arrays.asList(servers)));
		when(clusterManager.getDiskSpace()).thenReturn(spaceMap);
	}

	@After
	public void tearDown() {
		spaceMap.clear();
	}

	@Test
	public void returnsFalseWhenNoDataForServer() throws Exception {
		final DiskSpace server0Space = new DiskSpace(128L, 1024L);
		spaceMap.put(servers[0], server0Space);

		when(threshold.withinThreshold(server0Space)).thenReturn(true);

		boolean isSpace = checker.isSpaceAvailable();

		assertThat(isSpace).isFalse();
		verify(threshold).withinThreshold(server0Space);
		verify(clusterManager).getServers();
		verify(clusterManager).getDiskSpace();
	}

	@Test
	public void returnsFalseWhenOneServerFails() throws Exception {
		final DiskSpace server0Space = new DiskSpace(128L, 1024L);
		spaceMap.put(servers[0], server0Space);
		final DiskSpace server1Space = new DiskSpace(128L, 512L);
		spaceMap.put(servers[1], server1Space);

		when(threshold.withinThreshold(server0Space)).thenReturn(true);
		when(threshold.withinThreshold(server1Space)).thenReturn(false);

		boolean isSpace = checker.isSpaceAvailable();

		assertThat(isSpace).isFalse();
		verify(threshold).withinThreshold(server0Space);
		verify(threshold).withinThreshold(server1Space);
		verify(clusterManager).getServers();
		verify(clusterManager).getDiskSpace();
	}

	@Test
	public void returnsTrueWhenServersPass() throws Exception {
		final DiskSpace server0Space = new DiskSpace(128L, 1024L);
		spaceMap.put(servers[0], server0Space);
		final DiskSpace server1Space = new DiskSpace(128L, 512L);
		spaceMap.put(servers[1], server1Space);

		when(threshold.withinThreshold(server0Space)).thenReturn(true);
		when(threshold.withinThreshold(server1Space)).thenReturn(true);

		boolean isSpace = checker.isSpaceAvailable();

		assertThat(isSpace).isTrue();
		verify(threshold).withinThreshold(server0Space);
		verify(threshold).withinThreshold(server1Space);
		verify(clusterManager).getServers();
		verify(clusterManager).getDiskSpace();
	}

}
