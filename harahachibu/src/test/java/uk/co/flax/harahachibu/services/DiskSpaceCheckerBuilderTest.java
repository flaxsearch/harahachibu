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

import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Environment;
import org.junit.Before;
import org.junit.Test;
import uk.co.flax.harahachibu.config.DiskSpaceConfiguration;
import uk.co.flax.harahachibu.resources.SetSpaceResource;
import uk.co.flax.harahachibu.services.impl.ClusterDiskSpaceChecker;
import uk.co.flax.harahachibu.services.impl.ElasticsearchDiskSpaceChecker;
import uk.co.flax.harahachibu.services.impl.SolrDiskSpaceChecker;

import javax.ws.rs.client.Client;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the disk space checker builder.
 *
 * Created by mlp on 18/04/16.
 */
public class DiskSpaceCheckerBuilderTest {

	private final JerseyEnvironment jersey = mock(JerseyEnvironment.class);
	private final Environment environment = mock(Environment.class);
	private final Client httpClient = mock(Client.class);
	private final DiskSpaceConfiguration configuration = new DiskSpaceConfiguration();
	private final DiskSpaceCheckerBuilder builder = new DiskSpaceCheckerBuilder(environment, httpClient, configuration);

	@Before
	public void setup() {
		when(environment.jersey()).thenReturn(jersey);
		configuration.setThreshold("5M");
	}

	@Test
	public void buildsESChecker() throws Exception {
		configuration.setCheckerType(DiskSpaceConfiguration.ELASTICSEARCH_CHECKER);

		DiskSpaceChecker checker = builder.build();

		assertThat(checker).isNotNull();
		assertThat(checker).isInstanceOf(ElasticsearchDiskSpaceChecker.class);
	}

	@Test
	public void buildsSolrChecker() throws Exception {
		configuration.setCheckerType(DiskSpaceConfiguration.SOLR_LOCAL_CHECKER);
		configuration.getConfiguration().put(SolrDiskSpaceChecker.DATA_DIR_CONFIG_OPTION, "/");

		DiskSpaceChecker checker = builder.build();

		assertThat(checker).isNotNull();
		assertThat(checker).isInstanceOf(SolrDiskSpaceChecker.class);
	}

	@Test
	public void buildClusterChecker() throws Exception {
		configuration.setCheckerType(DiskSpaceConfiguration.CLUSTER_CHECKER);
		configuration.getConfiguration().put(ClusterDiskSpaceChecker.CLUSTER_SERVERS_CONFIG_OPTION, Arrays.asList("localhost", "192.168.0.100"));

		DiskSpaceChecker checker = builder.build();

		assertThat(checker).isNotNull();
		assertThat(checker).isInstanceOf(ClusterDiskSpaceChecker.class);

		verify(jersey).register(isA(SetSpaceResource.class));
	}

}
