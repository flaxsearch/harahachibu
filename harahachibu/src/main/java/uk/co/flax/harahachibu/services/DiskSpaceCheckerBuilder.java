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

import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.flax.harahachibu.config.DiskSpaceConfiguration;
import uk.co.flax.harahachibu.health.ClusterDiskSpaceManagerHealthCheck;
import uk.co.flax.harahachibu.health.ElasticsearchClientHealthCheck;
import uk.co.flax.harahachibu.resources.SetSpaceResource;
import uk.co.flax.harahachibu.services.impl.ClusterDiskSpaceChecker;
import uk.co.flax.harahachibu.services.impl.ElasticsearchClient;
import uk.co.flax.harahachibu.services.impl.ElasticsearchDiskSpaceChecker;
import uk.co.flax.harahachibu.services.impl.SolrDiskSpaceChecker;

import javax.ws.rs.client.Client;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Builder class for DiskSpaceChecker implementations.
 * <p>
 * Created by mlp on 15/04/16.
 */
public class DiskSpaceCheckerBuilder {

	private final Logger LOGGER = LoggerFactory.getLogger(DiskSpaceCheckerBuilder.class);

	private final Environment environment;
	private final Client client;
	private final DiskSpaceConfiguration configuration;

	public DiskSpaceCheckerBuilder(Environment environment, Client client, DiskSpaceConfiguration configuration) {
		this.environment = environment;
		this.client = client;
		this.configuration = configuration;
	}


	/**
	 * Build the disk space checker specified in the configuration.
	 *
	 * @return a {@link DiskSpaceChecker} implementation.
	 * @throws ClassNotFoundException if the {@link DiskSpaceChecker} implementation class
	 * cannot be found.
	 * @throws DiskSpaceCheckerException if other exceptions occur.
	 */
	@SuppressWarnings("unchecked")
	public DiskSpaceChecker build() throws ClassNotFoundException, DiskSpaceCheckerException {
		final DiskSpaceChecker checker;

		switch (configuration.getCheckerType()) {
			case DiskSpaceConfiguration.ELASTICSEARCH_CHECKER:
				checker = buildElasticsearchChecker(environment);
				break;
			case DiskSpaceConfiguration.SOLR_LOCAL_CHECKER:
				checker = new SolrDiskSpaceChecker();
				break;
			case DiskSpaceConfiguration.CLUSTER_CHECKER:
				checker = buildClusterChecker(environment,
						(List<String>) configuration.getConfiguration().get(ClusterDiskSpaceChecker.CLUSTER_SERVERS_CONFIG_OPTION));
				break;
			default:
				checker = buildCustomChecker(configuration.getCheckerType());
		}

		if (checker != null) {
			checker.configure(configuration.getConfiguration());
			checker.setThreshold(DiskSpaceThreshold.parse(configuration.getThreshold()));
			if (checker.requiresHttpClient()) {
				checker.setHttpClient(client);
			}
		}

		return checker;
	}


	private DiskSpaceChecker buildElasticsearchChecker(Environment environment) {
		final ElasticsearchClient elasticsearch = new ElasticsearchClient(
				client,
				(String) configuration.getConfiguration().get(ElasticsearchDiskSpaceChecker.BASE_URL_CONFIG_OPTION));
		environment.healthChecks().register("Elasticsearch client", new ElasticsearchClientHealthCheck(elasticsearch));
		return new ElasticsearchDiskSpaceChecker(elasticsearch);
	}

	private DiskSpaceChecker buildClusterChecker(Environment environment, List<String> servers) {
		final ClusterDiskSpaceManager clusterManager = new ClusterDiskSpaceManager(new LinkedHashSet<>(servers));

		// Register the /setSpace endpoint
		environment.jersey().register(new SetSpaceResource(clusterManager));

		// Add the cluster disk manager health check
		environment.healthChecks().register("Cluster disk manager", new ClusterDiskSpaceManagerHealthCheck(clusterManager));

		// And build the cluster disk space checker
		return new ClusterDiskSpaceChecker(clusterManager);
	}

	private DiskSpaceChecker buildCustomChecker(String clazz) throws ClassNotFoundException, DiskSpaceCheckerException {
		final DiskSpaceChecker checker;

		try {
			checker = (DiskSpaceChecker) Class.forName(clazz).newInstance();
		} catch (InstantiationException e) {
			LOGGER.error("Problem instantiating custom checker: {}", e.getMessage());
			throw new DiskSpaceCheckerException(e);
		} catch (IllegalAccessException e) {
			LOGGER.error("Illegal access instantiating custom checker: {}", e.getMessage());
			throw new DiskSpaceCheckerException(e);
		}

		return checker;
	}

}
