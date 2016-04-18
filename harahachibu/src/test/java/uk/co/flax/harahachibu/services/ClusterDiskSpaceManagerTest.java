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

import org.junit.Test;
import uk.co.flax.harahachibu.services.data.DiskSpace;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the ClusterDiskSpaceManager.
 * Created by mlp on 18/04/16.
 */
public class ClusterDiskSpaceManagerTest {

	private static final String TEST_SERVER = "test.localhost";

	private final Set<String> servers = new HashSet<>(Collections.singleton(TEST_SERVER));
	private final ClusterDiskSpaceManager manager = new ClusterDiskSpaceManager(servers);

	@Test(expected = uk.co.flax.harahachibu.services.DiskSpaceCheckerException.class)
	public void throwsExceptionWhenServerNotKnown() throws Exception {
		manager.setDiskSpace("blah", new DiskSpace(0, 0));
	}

	@Test
	public void recordsDiskSpace() throws Exception {
		manager.setDiskSpace(TEST_SERVER, new DiskSpace(128L, 1024L));

		assertThat(manager.getDiskSpace()).containsKey(TEST_SERVER);
		DiskSpace test = manager.getDiskSpace().get(TEST_SERVER);
		assertThat(test.getFreeSpace()).isEqualTo(128L);
		assertThat(test.getMaxSpace()).isEqualTo(1024L);
	}

}
