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

import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.flax.harahachibu.services.DiskSpaceChecker;
import uk.co.flax.harahachibu.services.DiskSpaceCheckerException;
import uk.co.flax.harahachibu.services.DiskSpaceThreshold;

import javax.ws.rs.client.Client;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Solr disk space checker for local (ie. non-clustered) Solr instances.
 * Created by mlp on 18/04/16.
 */
public class SolrDiskSpaceChecker implements DiskSpaceChecker {

	private static final Logger LOGGER = LoggerFactory.getLogger(SolrDiskSpaceChecker.class);

	public static final String DATA_DIR_CONFIG_OPTION = "dataDirectory";

	private DiskSpaceThreshold threshold;
	private FileStore fs;

	@Override
	public boolean isSpaceAvailable() throws DiskSpaceCheckerException {
		boolean available;

		try {
			long freeSpace = fs.getUsableSpace();
			long totalSpace = fs.getTotalSpace();

			available = threshold.withinThreshold(freeSpace, totalSpace);
		} catch (IOException e) {
			LOGGER.error("IO Exception caught checking file store: {}", e.getMessage());
			throw new DiskSpaceCheckerException(e);
		}

		return available;
	}

	@Override
	public void configure(Map<String, Object> configuration) throws DiskSpaceCheckerException {
		final String dataPath = (String) configuration.get(DATA_DIR_CONFIG_OPTION);
		if (StringUtils.isBlank(dataPath)) {
			throw new DiskSpaceCheckerException("No data directory given for Solr disk space checker");
		} else {
			try {
				// Set up the FileStore
				Path path = FileSystems.getDefault().getPath(dataPath);
				fs = Files.getFileStore(path);
			} catch (IOException e) {
				LOGGER.error("IO Exception initialising file store: {}", e.getMessage());
				throw new DiskSpaceCheckerException(e);
			}
		}
	}

	@Override
	public boolean requiresHttpClient() {
		return false;
	}

	@Override
	public void setHttpClient(Client httpClient) {
		// NO_OP
	}

	@Override
	public void setThreshold(DiskSpaceThreshold threshold) {
		this.threshold = threshold;
	}

	@VisibleForTesting
	void setFileStore(FileStore fs) {
		this.fs = fs;
	}

}
