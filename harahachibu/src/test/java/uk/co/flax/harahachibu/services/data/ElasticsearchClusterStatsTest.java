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
package uk.co.flax.harahachibu.services.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the ES cluster data.
 *
 * Created by mlp on 15/04/16.
 */
public class ElasticsearchClusterStatsTest {

	private static final String PRETTY_JSON_FILE = "/esDiskSpaceChecker/spaceFree_pretty.json";
	private static final String BASIC_JSON_FILE = "/esDiskSpaceChecker/spaceFree.json";

	@Test
	public void canDeserialisePrettyJson() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		ElasticsearchClusterStats clusterStats = mapper.readValue(
				ElasticsearchClusterStatsTest.class.getResourceAsStream(PRETTY_JSON_FILE),
				ElasticsearchClusterStats.class);

		assertThat(clusterStats).isNotNull();
		assertThat(clusterStats.getStatus()).isEqualTo("red");
		assertThat(clusterStats.getNodes().getFileSystem().getTotalBytes()).isEqualTo(206289465344L);
		assertThat(clusterStats.getNodes().getFileSystem().getFreeBytes()).isEqualTo(132862898176L);
		assertThat(clusterStats.getNodes().getFileSystem().getAvailableBytes()).isEqualTo(122360365056L);
	}

	@Test
	public void canDeserialiseBasicJson() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		ElasticsearchClusterStats clusterStats = mapper.readValue(
				ElasticsearchClusterStatsTest.class.getResourceAsStream(BASIC_JSON_FILE),
				ElasticsearchClusterStats.class);

		assertThat(clusterStats).isNotNull();
		assertThat(clusterStats.getStatus()).isEqualTo("red");
		assertThat(clusterStats.getNodes().getFileSystem().getTotalBytes()).isEqualTo(206289465344L);
		assertThat(clusterStats.getNodes().getFileSystem().getFreeBytes()).isEqualTo(132861665280L);
		assertThat(clusterStats.getNodes().getFileSystem().getAvailableBytes()).isEqualTo(122359132160L);
	}

}
