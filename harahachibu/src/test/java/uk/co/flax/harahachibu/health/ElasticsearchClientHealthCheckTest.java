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
import org.junit.Before;
import org.junit.Test;
import uk.co.flax.harahachibu.services.DiskSpaceCheckerException;
import uk.co.flax.harahachibu.services.data.ElasticsearchClusterStats;
import uk.co.flax.harahachibu.services.impl.ElasticsearchClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the Elasticsearch client health check.
 *
 * Created by mlp on 19/04/16.
 */
public class ElasticsearchClientHealthCheckTest {

	private final ElasticsearchClient client = mock(ElasticsearchClient.class);
	private ElasticsearchClientHealthCheck healthCheck;

	@Before
	public void setup() {
		healthCheck = new ElasticsearchClientHealthCheck(client);
	}

	@Test
	public void returnsUnhealthyWhenClientThrowsException() throws Exception {
		when(client.getClusterStats()).thenThrow(new DiskSpaceCheckerException("Error"));

		HealthCheck.Result result = healthCheck.check();
		assertThat(result.isHealthy()).isFalse();

		verify(client).getClusterStats();
	}

	@Test
	public void returnsUnhealthyWhenClusterStateRed() throws Exception {
		ElasticsearchClusterStats stats = mock(ElasticsearchClusterStats.class);
		when(stats.getStatus()).thenReturn("red");
		when(client.getClusterStats()).thenReturn(stats);

		HealthCheck.Result result = healthCheck.check();
		assertThat(result.isHealthy()).isFalse();

		verify(client).getClusterStats();
	}

	@Test
	public void returnsHealthyWhenClusterStateYellow() throws Exception {
		ElasticsearchClusterStats stats = mock(ElasticsearchClusterStats.class);
		when(stats.getStatus()).thenReturn("yellow");
		when(client.getClusterStats()).thenReturn(stats);

		HealthCheck.Result result = healthCheck.check();
		assertThat(result.isHealthy()).isTrue();

		verify(client).getClusterStats();
	}

	@Test
	public void returnsHealthyWhenClusterStateGreen() throws Exception {
		ElasticsearchClusterStats stats = mock(ElasticsearchClusterStats.class);
		when(stats.getStatus()).thenReturn("yellow");
		when(client.getClusterStats()).thenReturn(stats);

		HealthCheck.Result result = healthCheck.check();
		assertThat(result.isHealthy()).isTrue();

		verify(client).getClusterStats();
	}

	@Test
	public void returnsUnhealthyWhenClusterStateUnknown() throws Exception {
		ElasticsearchClusterStats stats = mock(ElasticsearchClusterStats.class);
		when(stats.getStatus()).thenReturn("blah");
		when(client.getClusterStats()).thenReturn(stats);

		HealthCheck.Result result = healthCheck.check();
		assertThat(result.isHealthy()).isFalse();

		verify(client).getClusterStats();
	}

}
