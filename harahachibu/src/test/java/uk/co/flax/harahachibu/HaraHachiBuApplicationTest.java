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

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.jetty.setup.ServletEnvironment;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.setup.Environment;
import org.junit.Before;
import org.junit.Test;
import uk.co.flax.harahachibu.config.DiskSpaceConfiguration;
import uk.co.flax.harahachibu.config.ProxyConfiguration;
import uk.co.flax.harahachibu.servlets.DiskSpaceProxyServlet;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletRegistration;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the Hara Hachi Bu application class.
 *
 * Created by mlp on 13/04/16.
 */
public class HaraHachiBuApplicationTest {

	private final LifecycleEnvironment lifecycleEnvironment = spy(new LifecycleEnvironment());
	private final MetricRegistry metrics = new MetricRegistry();
	private final Environment environment = mock(Environment.class);
	private final ServletEnvironment servlets = mock(ServletEnvironment.class);
	private final FilterRegistration.Dynamic filterDynamic = mock(FilterRegistration.Dynamic.class);
	private final ServletRegistration.Dynamic servletDynamic = mock(ServletRegistration.Dynamic.class);
	private final JerseyEnvironment jersey = mock(JerseyEnvironment.class);
	private final HealthCheckRegistry healthChecks = mock(HealthCheckRegistry.class);
	private final HaraHachiBuApplication application = new HaraHachiBuApplication();
	private final HaraHachiBuConfiguration config = new HaraHachiBuConfiguration();
	private final ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
	private final DiskSpaceConfiguration diskSpaceConfiguration = new DiskSpaceConfiguration();

	@Before
	public void setup() throws Exception {
		when(environment.servlets()).thenReturn(servlets);
		when(environment.lifecycle()).thenReturn(lifecycleEnvironment);
		when(environment.metrics()).thenReturn(metrics);
		when(servlets.addFilter(isA(String.class), isA(Filter.class))).thenReturn(filterDynamic);
		when(servlets.addServlet(eq("diskSpaceProxyServlet"), isA(DiskSpaceProxyServlet.class))).thenReturn(servletDynamic);
		when(environment.jersey()).thenReturn(jersey);
		when(environment.healthChecks()).thenReturn(healthChecks);

		// Config
		config.setProxy(proxyConfiguration);
		config.setDiskSpace(diskSpaceConfiguration);
		diskSpaceConfiguration.setThreshold("5M");
		diskSpaceConfiguration.setCheckerType("uk.co.flax.harahachibu.services.TestDiskSpaceChecker");
	}


	@Test
	public void buildsServlets() throws Exception {
		application.run(config, environment);
	}

}
