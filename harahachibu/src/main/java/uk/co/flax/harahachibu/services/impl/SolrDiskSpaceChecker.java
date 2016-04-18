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

import javax.ws.rs.client.Client;
import java.io.File;
import java.util.Map;

/**
 * Solr disk space checker for local (ie. non-clustered) Solr instances.
 * Created by mlp on 18/04/16.
 */
public class SolrDiskSpaceChecker implements DiskSpaceChecker {

	private static final Logger LOGGER = LoggerFactory.getLogger(SolrDiskSpaceChecker.class);

	private DiskSpaceThreshold threshold;
	private File dataDirectory;

	@Override
	public boolean isSpaceAvailable() throws DiskSpaceCheckerException {
		return false;
	}

	@Override
	public void configure(Map<String, Object> configuration) throws DiskSpaceCheckerException {

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

	}
}
