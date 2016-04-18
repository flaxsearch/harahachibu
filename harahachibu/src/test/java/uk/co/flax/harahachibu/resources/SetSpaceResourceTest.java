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
package uk.co.flax.harahachibu.resources;

import org.junit.Before;
import org.junit.Test;
import uk.co.flax.harahachibu.api.SetSpaceResponse;
import uk.co.flax.harahachibu.services.ClusterDiskSpaceManager;
import uk.co.flax.harahachibu.services.DiskSpaceCheckerException;
import uk.co.flax.harahachibu.services.data.DiskSpace;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the set space resource.
 *
 * Created by mlp on 18/04/16.
 */
public class SetSpaceResourceTest {

	private final ClusterDiskSpaceManager manager = mock(ClusterDiskSpaceManager.class);

	private SetSpaceResource resource;

	@Before
	public void setup() {
		resource = new SetSpaceResource(manager);
	}

	@Test
	public void returnsErrorOnException() throws Exception {
		final String server = "dummy";
		doThrow(new DiskSpaceCheckerException("Error")).when(manager).setDiskSpace(eq(server), isA(DiskSpace.class));

		SetSpaceResponse response = resource.handlePost(server, 128L, 1024L);
		assertThat(response).isNotNull();
		assertThat(response.getResponseCode()).isEqualTo(SetSpaceResponse.ResponseCode.ERROR);
		assertThat(response.getMessage()).isEqualTo("Error");

		verify(manager).setDiskSpace(eq(server), isA(DiskSpace.class));
	}

	@Test
	public void recordsUpdate() throws Exception {
		final String server = "dummy";

		SetSpaceResponse response = resource.handlePost(server, 128L, 1024L);
		assertThat(response).isNotNull();
		assertThat(response.getResponseCode()).isEqualTo(SetSpaceResponse.ResponseCode.OK);
		assertThat(response.getMessage()).isNull();

		verify(manager).setDiskSpace(eq(server), isA(DiskSpace.class));
	}

}
