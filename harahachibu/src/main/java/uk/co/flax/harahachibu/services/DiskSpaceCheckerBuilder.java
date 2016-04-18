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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.flax.harahachibu.config.DiskSpaceConfiguration;
import uk.co.flax.harahachibu.services.impl.ElasticsearchClient;
import uk.co.flax.harahachibu.services.impl.ElasticsearchDiskSpaceChecker;
import uk.co.flax.harahachibu.services.impl.SolrDiskSpaceChecker;

import javax.ws.rs.client.Client;

/**
 * Builder class for DiskSpaceChecker implementations.
 * <p>
 * Created by mlp on 15/04/16.
 */
public class DiskSpaceCheckerBuilder {

	private final Logger LOGGER = LoggerFactory.getLogger(DiskSpaceCheckerBuilder.class);

	private final Client client;
	private final DiskSpaceConfiguration configuration;

	public DiskSpaceCheckerBuilder(Client client, DiskSpaceConfiguration configuration) {
		this.client = client;
		this.configuration = configuration;
	}


	/**
	 * Build the disk space checker specified in the configuration.
	 * @return a {@link DiskSpaceChecker} implementation.
	 * @throws ClassNotFoundException if the {@link DiskSpaceChecker} implementation class
	 * cannot be found.
	 * @throws DiskSpaceCheckerException if other exceptions occur.
	 */
	public DiskSpaceChecker build() throws ClassNotFoundException, DiskSpaceCheckerException {
		final DiskSpaceChecker checker;

		switch (configuration.getCheckerType()) {
			case DiskSpaceConfiguration.ELASTICSEARCH_CHECKER:
				checker = buildElasticsearchChecker();
				break;
			case DiskSpaceConfiguration.SOLR_LOCAL_CHECKER:
				checker = new SolrDiskSpaceChecker();
				break;
			default:
				LOGGER.warn("Cannot instantiate DiskSpaceChecker of type {}", configuration.getCheckerType());
				checker = null;
		}

		if (checker != null) {
			checker.configure(configuration.getConfiguration());
			checker.setThreshold(DiskSpaceThreshold.parse(configuration.getThreshold()));
		}

		return checker;
	}


	private DiskSpaceChecker buildElasticsearchChecker() {
		final ElasticsearchClient elasticsearch = new ElasticsearchClient(
				client,
				(String) configuration.getConfiguration().get(ElasticsearchDiskSpaceChecker.BASE_URL_CONFIG_OPTION));
		return new ElasticsearchDiskSpaceChecker(elasticsearch);
	}

}
