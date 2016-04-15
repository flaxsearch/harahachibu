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
import uk.co.flax.harahachibu.services.DiskSpaceChecker;
import uk.co.flax.harahachibu.services.DiskSpaceCheckerException;
import uk.co.flax.harahachibu.services.DiskSpaceThreshold;
import uk.co.flax.harahachibu.services.data.ElasticsearchClusterStats;

/**
 * Disk space checker for Elasticsearch using /_cluster/stats lookup.
 * <p>
 * Created by mlp on 14/04/16.
 */
public class ElasticsearchDiskSpaceChecker implements DiskSpaceChecker {

	private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchDiskSpaceChecker.class);

	private final ElasticsearchClient elasticsearch;
	private final DiskSpaceThreshold threshold;

	public ElasticsearchDiskSpaceChecker(ElasticsearchClient client, DiskSpaceThreshold threshold) {
		this.elasticsearch = client;
		this.threshold = threshold;
	}

	@Override
	public boolean isSpaceAvailable() throws DiskSpaceCheckerException {
		final ElasticsearchClusterStats stats = elasticsearch.getClusterStats();

		long free = stats.getFilesystemAvailableBytes();
		long max = stats.getFilesystemTotalBytes();

		if (max == 0) {
			LOGGER.warn("ES Cluster Stats reports 0 total bytes - possible issue with ES cluster");
		} else if (free == 0) {
			LOGGER.warn("ES Cluster Stats reports 0 free bytes");
		}

		return threshold.withinThreshold(free, max);
	}

}
