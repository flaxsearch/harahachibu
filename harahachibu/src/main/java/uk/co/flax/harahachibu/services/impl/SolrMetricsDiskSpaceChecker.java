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
package uk.co.flax.harahachibu.services.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.flax.harahachibu.services.DiskSpaceChecker;
import uk.co.flax.harahachibu.services.DiskSpaceCheckerException;
import uk.co.flax.harahachibu.services.DiskSpaceThreshold;
import uk.co.flax.harahachibu.services.data.solr.metrics.CoreData;
import uk.co.flax.harahachibu.services.data.solr.metrics.MetricsResponseData;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Created by Matt Pearce on 15/10/18.</p>
 *
 * @author Matt Pearce.
 */
public class SolrMetricsDiskSpaceChecker implements DiskSpaceChecker {

	private static final Logger LOGGER = LoggerFactory.getLogger(SolrMetricsDiskSpaceChecker.class);

	static final String METRICS_ENDPOINT = "/solr/admin/metrics";
	private static final Map<String, String[]> PARAMS = new HashMap<>();

	static {
		PARAMS.put("group", new String[]{ "core" });
		PARAMS.put("prefix", new String[]{ "CORE.fs", "CORE.collection", "CORE.coreName" });
	}

	static final String COLLECTION_CONFIG_KEY = "collection";
	static final String SOLR_URL_CONFIG_KEY = "solrUrl";

	private DiskSpaceThreshold threshold;
	private Client httpClient;

	private String collectionName;
	private String solrServerUrl;

	@Override
	public boolean isSpaceAvailable() throws DiskSpaceCheckerException {
		MetricsResponseData metricsResponseData = getMetricsResponse();
		List<CoreData> coreData = metricsResponseData.getMetrics().getCoreDataByCollection(collectionName);

		boolean allPass = !coreData.isEmpty();
		for (CoreData cd : coreData) {
			if (!threshold.withinThreshold(cd.getUsableSpace(), cd.getTotalSpace())) {
				allPass = false;
				break;
			}
		}

		return allPass;
	}

	@Override
	public void configure(Map<String, Object> configuration) throws DiskSpaceCheckerException {
		final String collection = (String) configuration.get(COLLECTION_CONFIG_KEY);
		final String solrServer = (String) configuration.get(SOLR_URL_CONFIG_KEY);

		if (StringUtils.isBlank(collection)) {
			throw new DiskSpaceCheckerException("Missing Solr collection name in configuration");
		} else if (StringUtils.isBlank(solrServer)) {
			throw new DiskSpaceCheckerException("Missing Solr URL in configuration");
		}

		this.collectionName = collection;
		this.solrServerUrl = solrServer;
	}

	@Override
	public boolean requiresHttpClient() {
		return true;
	}

	@Override
	public void setHttpClient(Client httpClient) {
		this.httpClient = httpClient;
	}

	@Override
	public void setThreshold(DiskSpaceThreshold threshold) {
		this.threshold = threshold;
	}

	private MetricsResponseData getMetricsResponse() throws DiskSpaceCheckerException {
		final String uri = solrServerUrl + METRICS_ENDPOINT;
		try {
			LOGGER.debug("Retrieving stats from cluster via {}", uri);
			WebTarget target = httpClient.target(uri);
			PARAMS.forEach(target::queryParam);
			return httpClient.target(uri)
					.request(MediaType.APPLICATION_JSON)
					.buildGet()
					.invoke(MetricsResponseData.class);
		} catch (Exception e) {
			LOGGER.error("Exception thrown getting cluster stats from {}: {}", uri, e.getMessage());
			throw new DiskSpaceCheckerException(e);
		}
	}
}
