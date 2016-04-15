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

import com.codahale.metrics.MetricRegistry;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.setup.Environment;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.model.Header;
import org.mockserver.model.JsonBody;
import uk.co.flax.harahachibu.services.data.ElasticsearchClusterStats;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/**
 * Unit tests for the ElasticsearchClient class.
 *
 * Created by mlp on 15/04/16.
 */
public class ElasticsearchClientTest {

	private static final int MIN_PORT = 12000;
	private static final int MAX_PORT = 65535;
	private static final String BASIC_JSON_FILE = "/esDiskSpaceChecker/spaceFree.json";

	private static String clusterStatsJson;

	private final int port = MIN_PORT + (int) (Math.random() * (MAX_PORT - MIN_PORT + 1));

	private final JerseyClientBuilder builder = new JerseyClientBuilder(new MetricRegistry());
	private final LifecycleEnvironment lifecycleEnvironment = spy(new LifecycleEnvironment());
	private final Environment environment = mock(Environment.class);

	private MockServerClient mockServer;
	private ElasticsearchClient esClient;

	private Client client;

	@BeforeClass
	public static void initialiseJson() {
		try {
			URL statsUrl = ElasticsearchClientTest.class.getResource(BASIC_JSON_FILE);
			clusterStatsJson = FileUtils.readFileToString(new File(statsUrl.toURI()));
		} catch (URISyntaxException e) {
			System.err.println("URI Syntax Exception: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("IO Exception: " + e.getMessage());
		}
	}

	@Before
	public void setup() {
		when(environment.lifecycle()).thenReturn(lifecycleEnvironment);
		client = builder.using(environment).build("esClientTest");

		// Start the mock server
		mockServer = startClientAndServer(port);

		// Initialise the ES client
		esClient = new ElasticsearchClient(client, "http://localhost:" + port);
	}

	@Test(expected=uk.co.flax.harahachibu.services.DiskSpaceCheckerException.class)
	public void throwsExceptionWhenServerReturns404() throws Exception {
		mockServer.when(request().withPath(ElasticsearchClient.CLUSTER_STATS_ENDPOINT))
				.respond(response()
						.withStatusCode(HttpServletResponse.SC_NOT_FOUND));

		esClient.getClusterStats();
	}

	@Test(expected=uk.co.flax.harahachibu.services.DiskSpaceCheckerException.class)
	public void throwsExceptionWhenServerReturnsNonsense() throws Exception {
		mockServer.when(request().withPath(ElasticsearchClient.CLUSTER_STATS_ENDPOINT))
				.respond(response()
						.withBody("blah blah blah")
						.withStatusCode(HttpServletResponse.SC_OK));

		esClient.getClusterStats();
	}

	@Test(expected=uk.co.flax.harahachibu.services.DiskSpaceCheckerException.class)
	public void throwsExceptionWhenServerNotRunning() throws Exception {
		mockServer.stop();
		esClient.getClusterStats();
	}

	@Test(expected=uk.co.flax.harahachibu.services.DiskSpaceCheckerException.class)
	public void throwsExceptionWhenServerTimesOut() throws Exception {
		// Default DW Jersey client timeout is 500ms
		mockServer.when(request().withPath(ElasticsearchClient.CLUSTER_STATS_ENDPOINT))
				.respond(response()
						.withDelay(TimeUnit.SECONDS, 5)
						.withBody(new JsonBody(clusterStatsJson))
						.withHeaders(new Header("Content-Type", "application/json"))
						.withStatusCode(HttpServletResponse.SC_OK));

		esClient.getClusterStats();
	}

	@Test
	public void returnsClusterStats() throws Exception {
		mockServer.when(request().withPath(ElasticsearchClient.CLUSTER_STATS_ENDPOINT))
				.respond(response()
						.withBody(new JsonBody(clusterStatsJson))
						.withHeaders(new Header("Content-Type", "application/json"))
						.withStatusCode(HttpServletResponse.SC_OK));

		ElasticsearchClusterStats clusterStats = esClient.getClusterStats();
		assertThat(clusterStats).isNotNull();
		assertThat(clusterStats.getStatus()).isEqualTo("red");
		assertThat(clusterStats.getNodes().getFileSystem().getTotalBytes()).isEqualTo(206289465344L);
		assertThat(clusterStats.getNodes().getFileSystem().getFreeBytes()).isEqualTo(132861665280L);
		assertThat(clusterStats.getNodes().getFileSystem().getAvailableBytes()).isEqualTo(122359132160L);
	}

}
