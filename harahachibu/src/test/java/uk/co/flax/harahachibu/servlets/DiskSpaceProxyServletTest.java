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
package uk.co.flax.harahachibu.servlets;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HttpException;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;

import javax.servlet.http.HttpServletResponse;
import java.util.Hashtable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/**
 * Unit tests for the DiskSpaceProxyServlet.
 * <p>
 * Created by mlp on 14/04/16.
 */
public class DiskSpaceProxyServletTest {

	private static final int MIN_PORT = 12000;
	private static final int MAX_PORT = 65535;
	private static final String HEADER_NAME = "Mock-Server";
	private static final String HEADER_VALUE = "Proxied OK";

	private final int port = MIN_PORT + (int) (Math.random() * (MAX_PORT - MIN_PORT + 1));

	private MockServerClient mockServer;
	private final ServletRunner servletRunner = new ServletRunner();
	private final Hashtable initParams = new Hashtable();

	@Before
	@SuppressWarnings("unchecked")
	public void setup() {
		// Start the mock server
		mockServer = startClientAndServer(port);

		// Set up the servlet runner
		initParams.put(DiskSpaceProxyServlet.DESTINATION_SERVER_PARAM, "http://localhost:" + port + "/");
		initParams.put("maxThreads", "8");
		servletRunner.registerServlet("/proxy/*", DiskSpaceProxyServlet.class.getName(), initParams);
	}

	@Test
	public void errorWhenNoPathPrefix() throws Exception {
		final String requestUri = "http://localhost/";

		ServletUnitClient servletClient = servletRunner.newClient();
		WebRequest req = new GetMethodWebRequest(requestUri);

		try {
			servletClient.getResponse(req);
		} catch (HttpException e) {
			assertThat(e.getResponseCode()).isEqualTo(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	@Test
	@Ignore // Fails due to startAsync() not implemented in servletunit
	public void forwardsWithQueryParam() throws Exception {
		final String requestUri = "http://localhost/proxy/solr/select";

		mockServer.when(request().withPath("/solr/select").withQueryStringParameter("q", "blah"))
				.respond(response()
						.withStatusCode(HttpServletResponse.SC_OK)
						.withHeader(HEADER_NAME, HEADER_VALUE));

		ServletUnitClient servletClient = servletRunner.newClient();
		WebRequest req = new GetMethodWebRequest(requestUri);
		req.setParameter("q", "blah");
		WebResponse response = servletClient.getResponse(req);

		assertThat(response.getResponseCode()).isEqualTo(HttpServletResponse.SC_OK);
	}

}
