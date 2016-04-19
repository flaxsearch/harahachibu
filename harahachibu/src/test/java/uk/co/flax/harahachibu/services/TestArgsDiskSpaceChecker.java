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
package uk.co.flax.harahachibu.services;

import javax.ws.rs.client.Client;
import java.util.Map;

/**
 * Test disk space checker.
 *
 * Created by mlp on 19/04/16.
 */
public class TestArgsDiskSpaceChecker implements DiskSpaceChecker {

	public TestArgsDiskSpaceChecker(String name) {

	}

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

	}

	@Override
	public void setThreshold(DiskSpaceThreshold threshold) {

	}
}
