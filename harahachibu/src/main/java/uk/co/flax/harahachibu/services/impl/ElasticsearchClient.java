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
import uk.co.flax.harahachibu.services.DiskSpaceCheckerException;
import uk.co.flax.harahachibu.services.data.ElasticsearchClusterStats;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

/**
 * Client class for retrieving cluster stats from ES.
 *
 * Created by mlp on 15/04/16.
 */
public class ElasticsearchClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchClient.class);

	static final String CLUSTER_STATS_ENDPOINT = "/_cluster/stats";

	private final Client client;
	private final String baseUrl;

	public ElasticsearchClient(Client client, String baseUrl) {
		this.client = client;
		this.baseUrl = baseUrl;
	}

	public ElasticsearchClusterStats getClusterStats() throws DiskSpaceCheckerException {
		try {
			return client.target(baseUrl + CLUSTER_STATS_ENDPOINT)
					.request(MediaType.APPLICATION_JSON)
					.buildGet()
					.invoke(ElasticsearchClusterStats.class);
		} catch (Exception e) {
			LOGGER.error("Exception thrown getting cluster stats from {}: {}", baseUrl, e.getMessage());
			throw new DiskSpaceCheckerException(e);
		}
	}

}
