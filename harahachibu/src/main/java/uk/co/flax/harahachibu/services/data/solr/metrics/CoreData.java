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
 * POJO representing the core data retrieved from Solr metrics.
 *
 * <p>Created by Matt Pearce on 15/10/18.</p>
 *
 * @author Matt Pearce.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoreData {

	public static final String COLLECTION_KEY = "CORE.collection";
	public static final String CORE_NAME_KEY = "CORE.coreName";
	public static final String FS_PATH_KEY = "CORE.fs.path";
	public static final String TOTAL_SPACE_KEY = "CORE.fs.totalSpace";
	public static final String USABLE_SPACE_KEY = "CORE.fs.usableSpace";

	@JsonProperty(COLLECTION_KEY)
	private final String collection;
	@JsonProperty(CORE_NAME_KEY)
	private final String coreName;
	@JsonProperty(FS_PATH_KEY)
	private final String fsPath;
	@JsonProperty(TOTAL_SPACE_KEY)
	private final long totalSpace;
	@JsonProperty(USABLE_SPACE_KEY)
	private final long usableSpace;

	public CoreData(@JsonProperty(COLLECTION_KEY) String collection,
					@JsonProperty(CORE_NAME_KEY) String coreName,
					@JsonProperty(FS_PATH_KEY) String fsPath,
					@JsonProperty(TOTAL_SPACE_KEY) long totalSpace,
					@JsonProperty(USABLE_SPACE_KEY) long usableSpace) {
		this.collection = collection;
		this.coreName = coreName;
		this.fsPath = fsPath;
		this.totalSpace = totalSpace;
		this.usableSpace = usableSpace;
	}

	public String getCollection() {
		return collection;
	}

	public String getCoreName() {
		return coreName;
	}

	public String getFsPath() {
		return fsPath;
	}

	public long getTotalSpace() {
		return totalSpace;
	}

	public long getUsableSpace() {
		return usableSpace;
	}
}
