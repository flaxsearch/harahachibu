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
package uk.co.flax.harahachibu.services.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO representing the filesystem output from an ES /_cluster/stats
 * request.
 *
 * Created by mlp on 14/04/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ElasticsearchClusterFilesystem {

	@JsonProperty("total_in_bytes")
	private final Long totalBytes;

	@JsonProperty("free_in_bytes")
	private final Long freeBytes;

	@JsonProperty("available_in_bytes")
	private final Long availableBytes;

	public ElasticsearchClusterFilesystem(@JsonProperty("total_in_bytes") Long totalBytes,
										  @JsonProperty("free_in_bytes") Long freeBytes,
										  @JsonProperty("available_in_bytes") Long availableBytes) {
		this.totalBytes = totalBytes;
		this.freeBytes = freeBytes;
		this.availableBytes = availableBytes;
	}

	public long getTotalBytes() {
		return totalBytes;
	}

	public long getFreeBytes() {
		return freeBytes;
	}

	public long getAvailableBytes() {
		return availableBytes;
	}
}
