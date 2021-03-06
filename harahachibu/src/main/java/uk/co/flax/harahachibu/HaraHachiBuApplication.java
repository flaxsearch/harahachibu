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
package uk.co.flax.harahachibu;

import io.dropwizard.Application;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Environment;
import uk.co.flax.harahachibu.health.DiskSpaceCheckerHealthCheck;
import uk.co.flax.harahachibu.resources.SetSpaceResource;
import uk.co.flax.harahachibu.services.ClusterDiskSpaceManager;
import uk.co.flax.harahachibu.services.DiskSpaceChecker;
import uk.co.flax.harahachibu.services.DiskSpaceCheckerBuilder;
import uk.co.flax.harahachibu.services.impl.ClusterDiskSpaceChecker;
import uk.co.flax.harahachibu.servlets.DiskSpaceFilter;
import uk.co.flax.harahachibu.servlets.DiskSpaceProxyServlet;

import javax.servlet.DispatcherType;
import javax.servlet.ServletRegistration;
import javax.ws.rs.client.Client;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;

/**
 * Main application class for Hara Hachi Bu proxy application.
 *
 * Created by mlp on 13/04/16.
 */
public class HaraHachiBuApplication extends Application<HaraHachiBuConfiguration> {

	@Override
	public void run(HaraHachiBuConfiguration config, Environment environment) throws Exception {
		// Set up the Jersey client - required for HTTP client interactions
		final Client client = new JerseyClientBuilder(environment)
				.using(config.getJerseyClient())
				.build(getName());
		// Build the disk space checker instance -
		// this may add new resources and healthchecks to the environment.
		final DiskSpaceChecker checker = new DiskSpaceCheckerBuilder(environment, client, config.getDiskSpace()).build();

		// Set up the disk space filter
		environment.servlets()
				.addFilter("diskSpaceFilter", new DiskSpaceFilter(checker, config.getProxy(), "/setSpace"))
				.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");

		// Set up the proxy servlet - requires some additional configuration
		ServletRegistration.Dynamic diskSpaceProxyServlet = environment.servlets().addServlet("diskSpaceProxyServlet", new DiskSpaceProxyServlet());
		diskSpaceProxyServlet.setInitParameter(DiskSpaceProxyServlet.DESTINATION_SERVER_PARAM, config.getProxy().getDestinationServer());
		diskSpaceProxyServlet.addMapping(DiskSpaceProxyServlet.PROXY_PATH_PREFIX + "/*");

		// Add healthcheck
		environment.healthChecks().register("Generic disk space checker", new DiskSpaceCheckerHealthCheck(checker));
	}

	public static void main(String[] args) throws Exception {
		new HaraHachiBuApplication().run(args);
	}

}
