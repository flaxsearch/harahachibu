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
package uk.co.flax.harahachibu.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import com.codahale.metrics.MetricRegistry;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.setup.Environment;
import org.apache.commons.io.FileUtils;
import org.junit.*;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.model.Header;
import org.mockserver.model.JsonBody;
import org.mockserver.verify.VerificationTimes;
import uk.co.flax.harahachibu.services.DiskSpaceCheckerException;
import uk.co.flax.harahachibu.services.DiskSpaceThreshold;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Created by Matt Pearce on 15/10/18.</p>
 *
 * @author Matt Pearce.
 */
public class SolrMetricsDiskSpaceCheckerTest {

	private static final int MIN_PORT = 12000;
	private static final int MAX_PORT = 65535;

	private final int port = MIN_PORT + (int) (Math.random() * (MAX_PORT - MIN_PORT + 1));

	private static final String SHORT_RESPONSE_PATH = "/solrMetrics/shortResponse.json";
	private static final String NO_SPACE_RESPONSE_PATH = "/solrMetrics/noSpaceResponse.json";
	private static String solrMetricsJson;

	private static final Map<String, Object> CONFIGURATION = new HashMap<>();

	static {
		CONFIGURATION.put(SolrMetricsDiskSpaceChecker.COLLECTION_CONFIG_KEY, "gettingstarted");
	}

	private final JerseyClientBuilder builder = new JerseyClientBuilder(new MetricRegistry());
	private final LifecycleEnvironment lifecycleEnvironment = spy(new LifecycleEnvironment());
	private final Environment environment = mock(Environment.class);

	private MockServerClient mockServer;
	private SolrMetricsDiskSpaceChecker checker;

	@BeforeClass
	public static void initialiseJson() {
		try {
			URL statsUrl = SolrMetricsDiskSpaceCheckerTest.class.getResource(SHORT_RESPONSE_PATH);
			solrMetricsJson = FileUtils.readFileToString(new File(statsUrl.toURI()));
		} catch (URISyntaxException e) {
			System.err.println("URI Syntax Exception: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("IO Exception: " + e.getMessage());
		}
	}

	@Before
	public void setup() throws Exception {
		when(environment.lifecycle()).thenReturn(lifecycleEnvironment);
		Client client = builder.using(environment).build("metricsTest");

		// Start the mock server
		mockServer = startClientAndServer(port);
		// Add the port to the configuration
		CONFIGURATION.put(SolrMetricsDiskSpaceChecker.SOLR_URL_CONFIG_KEY, "http://localhost:" + port);

		// Initialise the checker
		checker = new SolrMetricsDiskSpaceChecker();
		checker.setHttpClient(client);
		checker.configure(CONFIGURATION);
		checker.setThreshold(DiskSpaceThreshold.parse("5%"));
	}

	@After
	public void tearDown() {
		mockServer = null;
		checker = null;
	}

	@Test(expected = DiskSpaceCheckerException.class)
	public void throwsDiskSpaceCheckerException_whenCollectionNotDefined() throws Exception {
		Map<String, Object> configuration = new HashMap<>();
		configuration.put(SolrMetricsDiskSpaceChecker.SOLR_URL_CONFIG_KEY, "http://localhost:8983");

		checker.configure(configuration);
	}

	@Test(expected = DiskSpaceCheckerException.class)
	public void throwsDiskSpaceCheckerException_whenSolrUrlNotDefined() throws Exception {
		Map<String, Object> configuration = new HashMap<>();
		configuration.put(SolrMetricsDiskSpaceChecker.COLLECTION_CONFIG_KEY, "blah");

		checker.configure(configuration);
	}

	@Test(expected = DiskSpaceCheckerException.class)
	public void throwsDiskSpaceCheckerException_whenEndpointNotFound() throws Exception {
		mockServer.when(request().withPath(SolrMetricsDiskSpaceChecker.METRICS_ENDPOINT))
				.respond(response()
						.withStatusCode(HttpServletResponse.SC_NOT_FOUND));

		checker.isSpaceAvailable();

		// Verify mockServer calls
		mockServer.verify(request().withPath(SolrMetricsDiskSpaceChecker.METRICS_ENDPOINT), VerificationTimes.once());
	}

	@Test
	public void returnsTrue_whenSpaceIsAvailable() throws Exception {
		mockServer.when(request().withPath(SolrMetricsDiskSpaceChecker.METRICS_ENDPOINT))
				.respond(response()
						.withBody(new JsonBody(solrMetricsJson))
						.withHeaders(new Header("Content-Type", "application/json"))
						.withStatusCode(HttpServletResponse.SC_OK));

		assertThat(checker.isSpaceAvailable()).isTrue();

		// Verify mockServer calls
		mockServer.verify(request().withPath(SolrMetricsDiskSpaceChecker.METRICS_ENDPOINT), VerificationTimes.once());
	}

	@Test
	public void returnsFalse_whenCollectionNotFound() throws Exception {
		mockServer.when(request().withPath(SolrMetricsDiskSpaceChecker.METRICS_ENDPOINT))
				.respond(response()
						.withBody(new JsonBody(FileUtils.readFileToString(new File(SolrMetricsDiskSpaceCheckerTest.class.getResource(NO_SPACE_RESPONSE_PATH).toURI()))))
						.withHeaders(new Header("Content-Type", "application/json"))
						.withStatusCode(HttpServletResponse.SC_OK));
		Map<String, Object> configuration = new HashMap<>(CONFIGURATION);
		configuration.put(SolrMetricsDiskSpaceChecker.COLLECTION_CONFIG_KEY, "blah");
		checker.configure(configuration);

		assertThat(checker.isSpaceAvailable()).isFalse();

		// Verify mockServer calls
		mockServer.verify(request().withPath(SolrMetricsDiskSpaceChecker.METRICS_ENDPOINT), VerificationTimes.once());
	}

	@Test
	public void returnsFalse_whenNoSpaceIsAvailable() throws Exception {
		mockServer.when(request().withPath(SolrMetricsDiskSpaceChecker.METRICS_ENDPOINT))
				.respond(response()
						.withBody(new JsonBody(FileUtils.readFileToString(new File(SolrMetricsDiskSpaceCheckerTest.class.getResource(NO_SPACE_RESPONSE_PATH).toURI()))))
						.withHeaders(new Header("Content-Type", "application/json"))
						.withStatusCode(HttpServletResponse.SC_OK));

		assertThat(checker.isSpaceAvailable()).isFalse();

		// Verify mockServer calls
		mockServer.verify(request().withPath(SolrMetricsDiskSpaceChecker.METRICS_ENDPOINT), VerificationTimes.once());
	}
}
