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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.flax.harahachibu.services.DiskSpaceCheckerException;
import uk.co.flax.harahachibu.services.data.ElasticsearchClusterStats;
import uk.co.flax.harahachibu.services.impl.ElasticsearchClient;

/**
 * Healthcheck for the Elasticsearch Client class.
 *
 * Created by mlp on 19/04/16.
 */
public class ElasticsearchClientHealthCheck extends HealthCheck {

	private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchClientHealthCheck.class);

	private static final String CLUSTER_HEALTH_MSG = "Cluster status: %s";

	private final ElasticsearchClient client;

	public ElasticsearchClientHealthCheck(ElasticsearchClient client) {
		this.client = client;
	}

	@Override
	protected Result check() throws Exception {
		final Result result;

		try {
			ElasticsearchClusterStats clusterStats = client.getClusterStats();
			String message = String.format(CLUSTER_HEALTH_MSG, clusterStats.getStatus());

			switch (clusterStats.getStatus()) {
				case "red":
					result = Result.unhealthy(message);
					break;
				case "yellow":
				case "green":
					result = Result.healthy(message);
					break;
				default:
					result = Result.unhealthy("Unrecognized cluster status: " + clusterStats.getStatus());
			}
		} catch (DiskSpaceCheckerException e) {
			LOGGER.error("Exception caught checking ES cluster health: {}", e.getMessage());
			return Result.unhealthy(e);
		}

		return result;
	}
}
