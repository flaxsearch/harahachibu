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

import org.junit.Before;
import org.junit.Test;
import uk.co.flax.harahachibu.config.ProxyConfiguration;
import uk.co.flax.harahachibu.services.DiskSpaceChecker;
import uk.co.flax.harahachibu.services.DiskSpaceCheckerException;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Unit tests for the DiskSpaceFilter implementation.
 *
 * Created by mlp on 13/04/16.
 */
public class DiskSpaceFilterTest {

	private final DiskSpaceChecker checker = mock(DiskSpaceChecker.class);
	private final ProxyConfiguration proxyConfig = mock(ProxyConfiguration.class);
	private final List<String> checkUrls = Arrays.asList("/solr/update", "/solr/.*/update");
	private final String[] localPaths = new String[]{ "/set" };
	private final int errorCode = 500;

	private DiskSpaceFilter filter;

	@Before
	public void setup() {
		when(proxyConfig.getCheckUrls()).thenReturn(checkUrls);
		when(proxyConfig.getErrorStatus()).thenReturn(errorCode);
		filter = new DiskSpaceFilter(checker, proxyConfig, localPaths);
	}

	@Test
	public void passThroughLocalPath() throws Exception {
		final String requestUri = localPaths[0];

		final HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getRequestURI()).thenReturn(requestUri);
		when(req.getContextPath()).thenReturn("");

		final ServletResponse response = mock(ServletResponse.class);
		final FilterChain chain = mock(FilterChain.class);

		filter.doFilter(req, response, chain);

		verify(req, atLeastOnce()).getRequestURI();
		verify(chain).doFilter(req, response);
	}

	@Test
	public void proxyWithoutCheckingState() throws Exception {
		final String requestUri = "/solr/select";
		final String requestQuery = "q=test";
		final String proxiedUri = DiskSpaceProxyServlet.PROXY_PATH_PREFIX + "/solr/select?" + requestQuery;

		final RequestDispatcher dispatcher = mock(RequestDispatcher.class);
		final HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getRequestURI()).thenReturn(requestUri);
		when(req.getContextPath()).thenReturn("");
		when(req.getQueryString()).thenReturn(requestQuery);
		when(req.getRequestDispatcher(proxiedUri)).thenReturn(dispatcher);

		final ServletResponse response = mock(ServletResponse.class);
		final FilterChain chain = mock(FilterChain.class);

		filter.doFilter(req, response, chain);

		verify(req, atLeastOnce()).getRequestURI();
		verify(req, atLeastOnce()).getQueryString();
		verify(req).getRequestDispatcher(proxiedUri);
		verify(dispatcher).forward(req, response);
	}

	@Test
	public void returnErrorWhenDiskCheckFails() throws Exception {
		when(checker.isSpaceAvailable()).thenThrow(new DiskSpaceCheckerException("Error"));

		final String requestUri = "/solr/update";
		final HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getRequestURI()).thenReturn(requestUri);
		when(req.getContextPath()).thenReturn("");
		when(req.getQueryString()).thenReturn(null);

		final HttpServletResponse response = mock(HttpServletResponse.class);
		when(response.isCommitted()).thenReturn(true);
		final FilterChain chain = mock(FilterChain.class);

		filter.doFilter(req, response, chain);

		verify(req, atLeastOnce()).getRequestURI();
		verify(response).sendError(errorCode);
	}

	@Test
	public void returnErrorWhenDiskFull() throws Exception {
		when(checker.isSpaceAvailable()).thenReturn(false);

		final String requestUri = "/solr/update";
		final HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getRequestURI()).thenReturn(requestUri);
		when(req.getContextPath()).thenReturn("");
		when(req.getQueryString()).thenReturn(null);

		final HttpServletResponse response = mock(HttpServletResponse.class);
		when(response.isCommitted()).thenReturn(true);
		final FilterChain chain = mock(FilterChain.class);

		filter.doFilter(req, response, chain);

		verify(req, atLeastOnce()).getRequestURI();
		verify(response).sendError(errorCode);
	}

	@Test
	public void proxyWhenStateCheckPasses() throws Exception {
		when(checker.isSpaceAvailable()).thenReturn(true);

		final String requestUri = "/solr/banana/update";
		final String requestQuery = "q=test";
		final String proxiedUri = DiskSpaceProxyServlet.PROXY_PATH_PREFIX + requestUri + "?" + requestQuery;

		final RequestDispatcher dispatcher = mock(RequestDispatcher.class);
		final HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getRequestURI()).thenReturn(requestUri);
		when(req.getContextPath()).thenReturn("");
		when(req.getQueryString()).thenReturn(requestQuery);
		when(req.getRequestDispatcher(proxiedUri)).thenReturn(dispatcher);

		final ServletResponse response = mock(ServletResponse.class);
		final FilterChain chain = mock(FilterChain.class);

		filter.doFilter(req, response, chain);

		verify(req, atLeastOnce()).getRequestURI();
		verify(req, atLeastOnce()).getQueryString();
		verify(req).getRequestDispatcher(proxiedUri);
		verify(dispatcher).forward(req, response);
	}

}
