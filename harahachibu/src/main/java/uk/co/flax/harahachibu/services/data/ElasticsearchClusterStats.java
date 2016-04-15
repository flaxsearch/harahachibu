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

import javax.validation.constraints.NotNull;

/**
 * POJO representing the response from an ES /_cluster/stats request.
 *
 * Created by mlp on 14/04/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ElasticsearchClusterStats {

	@JsonProperty("timestamp")
	private final long timestamp;

	@JsonProperty("cluster_name")
	private final String clusterName;

	@JsonProperty("status")
	private final String status;

	@JsonProperty(value = "nodes")
	private final ElasticsearchClusterNodes nodes;

	public ElasticsearchClusterStats(@JsonProperty("timestamp") long timestamp,
									 @JsonProperty("cluster_name") String clusterName,
									 @JsonProperty("status") String status,
									 @JsonProperty("nodes") ElasticsearchClusterNodes nodes) {
		this.timestamp = timestamp;
		this.clusterName = clusterName;
		this.status = status;
		this.nodes = nodes;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public String getClusterName() {
		return clusterName;
	}

	public String getStatus() {
		return status;
	}

	public ElasticsearchClusterNodes getNodes() {
		return nodes;
	}

	public long getFilesystemFreeBytes() {
		ElasticsearchClusterFilesystem fs = getFilesystem();
		if (fs != null) {
			return fs.getFreeBytes();
		}
		return 0;
	}

	public long getFilesystemAvailableBytes() {
		ElasticsearchClusterFilesystem fs = getFilesystem();
		if (fs != null) {
			return fs.getAvailableBytes();
		}
		return 0;
	}

	public long getFilesystemTotalBytes() {
		ElasticsearchClusterFilesystem fs = getFilesystem();
		if (fs != null) {
			return fs.getTotalBytes();
		}
		return 0;
	}

	private ElasticsearchClusterFilesystem getFilesystem() {
		if (nodes != null) {
			return nodes.getFileSystem();
		}
		return null;
	}

}
