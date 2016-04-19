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
package uk.co.flax.harahachibu.health;

import com.codahale.metrics.health.HealthCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.flax.harahachibu.services.DiskSpaceChecker;
import uk.co.flax.harahachibu.services.DiskSpaceCheckerException;

/**
 * Generic disk checker health check.
 * <p>
 * Created by mlp on 19/04/16.
 */
public class DiskSpaceCheckerHealthCheck extends HealthCheck {

	private static final Logger LOGGER = LoggerFactory.getLogger(DiskSpaceCheckerHealthCheck.class);

	private final DiskSpaceChecker checker;

	public DiskSpaceCheckerHealthCheck(DiskSpaceChecker checker) {
		this.checker = checker;
	}

	@Override
	protected Result check() throws Exception {
		try {
			if (checker.isSpaceAvailable()) {
				return Result.healthy();
			} else {
				return Result.unhealthy("DiskSpaceChecker returns no space available");
			}
		} catch (DiskSpaceCheckerException e) {
			LOGGER.warn("DiskSpaceChecker threw exception during healthcheck: {}", e.getMessage());
			return Result.unhealthy(e);
		}
	}
}
