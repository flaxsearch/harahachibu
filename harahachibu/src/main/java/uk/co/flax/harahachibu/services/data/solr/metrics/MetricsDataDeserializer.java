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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Deserializer to extract Core-specific data from metrics when building
 * MetricsData object.
 *
 * <p>Created by Matt Pearce on 15/10/18.</p>
 *
 * @author Matt Pearce.
 */
public class MetricsDataDeserializer extends StdDeserializer<MetricsData> {

	private static final Logger LOGGER = LoggerFactory.getLogger(MetricsDataDeserializer.class);

	public MetricsDataDeserializer() {
		super(MetricsData.class);
	}

	@Override
	public MetricsData deserialize(JsonParser jsonParser,
								   DeserializationContext deserializationContext) throws IOException {
		ObjectCodec oc = jsonParser.getCodec();
		JsonNode node = oc.readTree(jsonParser);
		ObjectMapper mapper = new ObjectMapper();

		Map<String, Object> properties = new HashMap<>();
		Map<String, CoreData> coreData = new HashMap<>();

		node.fieldNames().forEachRemaining(field -> {
			try {
				if (field.startsWith("solr.core")) {
					coreData.put(field, mapper.readValue(node.get(field).traverse(), CoreData.class));
				} else {
					properties.put(field, mapper.readValue(node.get(field).traverse(), Object.class));
				}
			} catch (IOException e) {
				LOGGER.error("Could not deserialize data for field {}: {}", field, e.getMessage());
			}
		});

		return new MetricsData(properties, coreData);
	}
}
