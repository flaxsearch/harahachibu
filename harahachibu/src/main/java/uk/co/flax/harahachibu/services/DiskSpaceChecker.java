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
 * Interface defining functionality for disk space checks.
 *
 * Created by mlp on 13/04/16.
 */
public interface DiskSpaceChecker {

	/**
	 * Check whether space is available on the disk or cluster of
	 * disks.
	 * @return {@code true} if space is available, {@code false} if not
	 * (including if server calls fail).
	 * @throws DiskSpaceCheckerException if problems occur checking the
	 * disks.
	 */
	boolean isSpaceAvailable() throws DiskSpaceCheckerException;

	/**
	 * Pass configuration properties into the checker instance, for custom
	 * checkers.
	 * @param configuration a {@link Map} of configuration details.
	 * @throws DiskSpaceCheckerException if the configuration cannot be read.
	 */
	void configure(Map<String, Object> configuration) throws DiskSpaceCheckerException;

	/**
	 * Does the implementation require an HTTP client for remote access?
	 * @return {@code true} if the checker requires HTTP access.
	 */
	boolean requiresHttpClient();

	/**
	 * Set the HTTP client to use for remote access. This will be supplied and managed
	 * by DropWizard, avoiding the requirement to implement your own.
	 * @param httpClient the client to use.
	 */
	void setHttpClient(Client httpClient);

}
