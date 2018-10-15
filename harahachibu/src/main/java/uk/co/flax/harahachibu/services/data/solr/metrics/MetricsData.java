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

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * POJO representing the metrics data returned by the Solr metrics API.
 * <p>
 * Most properties are pushed into the general properties object, while
 * the core-specific data is deserialized into a map of {@link CoreData}
 * objects.
 *
 * <p>Created by Matt Pearce on 15/10/18.</p>
 *
 * @author Matt Pearce.
 */
@JsonDeserialize(using = MetricsDataDeserializer.class)
public class MetricsData {

	private final Map<String, Object> properties;
	private final Map<String, CoreData> coreData;

	MetricsData(Map<String, Object> properties, Map<String, CoreData> coreData) {
		this.properties = properties;
		this.coreData = coreData;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public Map<String, CoreData> getCoreData() {
		return coreData;
	}

	public List<CoreData> getCoreDataByCollection(String collection) {
		return coreData.values().stream()
				.filter(cd -> collection.equals(cd.getCollection()))
				.collect(Collectors.toList());
	}

	@JsonAnySetter
	public void setProperty(String key, Object value) {
		properties.put(key, value);
	}

	@JsonAnyGetter
	public Object getProperty(String key) {
		return properties.get(key);
	}
}
