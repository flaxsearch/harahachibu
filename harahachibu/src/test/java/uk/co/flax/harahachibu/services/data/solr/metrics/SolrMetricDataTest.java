/*
 * Copyright (c) 2018 Lemur Consulting Ltd.
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
package uk.co.flax.harahachibu.services.data.solr.metrics;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.List;

/**
 * <p>Created by Matt Pearce on 15/10/18.</p>
 *
 * @author Matt Pearce.
 */
public class SolrMetricDataTest {

	private static final String FULL_RESPONSE_PATH = "/solrMetrics/fullResponse.json";
	private static final String SHORT_RESPONSE_PATH = "/solrMetrics/shortResponse.json";

	@Test
	public void canDeserializeFullResponse() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		MetricsResponseData metricsData = mapper.readValue(SolrMetricDataTest.class.getResourceAsStream(FULL_RESPONSE_PATH),
				MetricsResponseData.class);

		assertThat(metricsData).isNotNull();
		assertThat(metricsData.getMetrics()).isNotNull();
		assertThat(metricsData.getMetrics().getCoreData().size()).isEqualTo(2);

		CoreData core1 = metricsData.getMetrics().getCoreData().get("solr.core.gettingstarted.shard2.replica_n4");
		assertThat(core1.getTotalSpace()).isEqualTo(201451704320L);
		assertThat(core1.getUsableSpace()).isEqualTo(130801418240L);
	}

	@Test
	public void canDeserializeShortResponse() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		MetricsResponseData metricsData = mapper.readValue(SolrMetricDataTest.class.getResourceAsStream(SHORT_RESPONSE_PATH),
				MetricsResponseData.class);

		assertThat(metricsData).isNotNull();
		assertThat(metricsData.getMetrics()).isNotNull();
		assertThat(metricsData.getMetrics().getCoreData().size()).isEqualTo(2);

		CoreData core1 = metricsData.getMetrics().getCoreData().get("solr.core.gettingstarted.shard2.replica_n4");
		assertThat(core1.getTotalSpace()).isEqualTo(201451704320L);
		assertThat(core1.getUsableSpace()).isEqualTo(130809856000L);
	}

	@Test
	public void getCoreDataByCollection_returnsEmptyWhenNoSuchCollection() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		MetricsResponseData metricsData = mapper.readValue(SolrMetricDataTest.class.getResourceAsStream(SHORT_RESPONSE_PATH),
				MetricsResponseData.class);

		List<CoreData> cores = metricsData.getMetrics().getCoreDataByCollection("blah");
		assertThat(cores).isNotNull();
		assertThat(cores).isEmpty();
	}

	@Test
	public void getCoreDataByCollection_returnsListWhenCollectionExists() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		MetricsResponseData metricsData = mapper.readValue(SolrMetricDataTest.class.getResourceAsStream(SHORT_RESPONSE_PATH),
				MetricsResponseData.class);

		List<CoreData> cores = metricsData.getMetrics().getCoreDataByCollection("gettingstarted");
		assertThat(cores).isNotNull();
		assertThat(cores).isNotEmpty();
		assertThat(cores.size()).isEqualTo(2);
	}
}
