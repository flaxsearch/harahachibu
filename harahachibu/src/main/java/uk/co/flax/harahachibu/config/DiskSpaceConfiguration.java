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
package uk.co.flax.harahachibu.config;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration details for the disk space checker implementation.
 *
 * Created by mlp on 15/04/16.
 */
public class DiskSpaceConfiguration {

	public static final String ELASTICSEARCH_CHECKER = "elasticsearch";
	public static final String SOLR_LOCAL_CHECKER = "solr";
	public static final String SOLR_CLUSTER_CHECKER = "solrCluster";

	@NotNull
	private String checkerType;

	@NotNull
	private String threshold;

	private Map<String, Object> configuration = new HashMap<>();


	public String getCheckerType() {
		return checkerType;
	}

	public void setCheckerType(String checkerType) {
		this.checkerType = checkerType;
	}

	public String getThreshold() {
		return threshold;
	}

	public void setThreshold(String threshold) {
		this.threshold = threshold;
	}

	public Map<String, Object> getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Map<String, Object> configuration) {
		this.configuration = configuration;
	}
}
