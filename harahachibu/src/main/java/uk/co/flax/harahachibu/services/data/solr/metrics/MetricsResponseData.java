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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO representing the full response from a Solr metrics API call.
 *
 * Allows the responseHeader object to be ignored, since we don't need it.
 *
 * <p>Created by Matt Pearce on 15/10/18.</p>
 *
 * @author Matt Pearce.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetricsResponseData {

	static final String METRICS_KEY = "metrics";

	@JsonProperty(METRICS_KEY)
	private final MetricsData metrics;

	public MetricsResponseData(@JsonProperty(METRICS_KEY) MetricsData metrics) {
		this.metrics = metrics;
	}

	public MetricsData getMetrics() {
		return metrics;
	}
}
