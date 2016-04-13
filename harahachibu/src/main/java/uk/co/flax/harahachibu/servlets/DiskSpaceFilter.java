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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.flax.harahachibu.config.ProxyConfiguration;
import uk.co.flax.harahachibu.services.DiskSpaceChecker;
import uk.co.flax.harahachibu.services.DiskSpaceCheckerException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Filter run on all incoming requests.
 * <p>
 * <p>
 * There are a number of paths through the filter:
 * </p>
 * <p>
 * <ul>
 * <li>if the request is for a local resource, it is passed straight through;</li>
 * <li>if the request is not for a local resource, it is checked to see if it is a
 * path that requires a disk space check. If so, the check is run. If the check passes,
 * the URI path is modified to start with "/proxy", and passed through, otherwise a
 * 500 status code is set in the response.</li>
 * </ul>
 * <p>
 * Created by mlp on 13/04/16.
 */
public class DiskSpaceFilter implements Filter {

	private static final Logger LOGGER = LoggerFactory.getLogger(DiskSpaceFilter.class);

	private static final Pattern PATH_PATTERN = Pattern.compile("https?://[^/]+(/.*)");

	private final DiskSpaceChecker spaceChecker;
	private final ProxyConfiguration proxyConfiguration;
	private final String[] localPaths;

	public DiskSpaceFilter(DiskSpaceChecker spaceChecker, ProxyConfiguration proxyConfiguration, String... localPaths) {
		this.spaceChecker = spaceChecker;
		this.proxyConfiguration = proxyConfiguration;
		this.localPaths = localPaths;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		LOGGER.info("Initialising DiskSpaceFilter...");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
						 FilterChain chain) throws IOException, ServletException {
		final HttpServletRequest httpRequest = (HttpServletRequest) request;

		if (isUriInLocalPaths(httpRequest)) {
			// Ignore and pass through
			chain.doFilter(request, response);
		} else {
			if (isUriInCheckPaths(httpRequest)) {
				// Check the server state
				try {
					if (!spaceChecker.isSpaceAvailable()) {
						((HttpServletResponse) response).sendError(proxyConfiguration.getErrorStatus());
					}
				} catch (DiskSpaceCheckerException e) {
					LOGGER.error("Exception thrown by DiskSpaceChecker", e);
					((HttpServletResponse) response).sendError(proxyConfiguration.getErrorStatus());
				}
			}

			if (!response.isCommitted()) {
				// Forward to proxy location
				RequestDispatcher dispatcher = httpRequest.getRequestDispatcher(getProxyPath(httpRequest));
				dispatcher.forward(request, response);
			}
		}
	}

	private boolean isUriInLocalPaths(HttpServletRequest request) {
		boolean ret = false;

		final String path = getPath(request);
		if (path != null && localPaths != null) {
			for (String localPath : localPaths) {
				if (path.startsWith(localPath)) {
					ret = true;
					break;
				}
			}
		}

		return ret;
	}

	private boolean isUriInCheckPaths(HttpServletRequest request) {
		boolean ret = false;

		final String path = getPath(request);
		if (path != null) {
			for (String checkPath : proxyConfiguration.getCheckUrls()) {
				if (path.startsWith(checkPath)) {
					ret = true;
					break;
				}
			}
		}

		return ret;
	}

	private String getProxyPath(HttpServletRequest request) {
		final String path = getPath(request);
		final String uri = request.getRequestURI();
		final String pathStart = uri.substring(0, uri.length() - path.length());

		StringBuilder pathBuilder = new StringBuilder(pathStart)
				.append(DiskSpaceProxyServlet.PROXY_PATH_PREFIX)
				.append(path);
		if (request.getQueryString() != null) {
			pathBuilder.append("?").append(request.getQueryString());
		}
		return pathBuilder.toString();
	}

	/**
	 * Get the request path, excluding the server context.
	 *
	 * @param request the HTTP request.
	 * @return the path, or <code>null</code> if the path does not
	 * match the expected pattern.
	 */
	private String getPath(HttpServletRequest request) {
		Matcher m = PATH_PATTERN.matcher(request.getRequestURI());
		if (m.matches()) {
			return m.group(1).substring(request.getContextPath().length());
		}

		return null;
	}

	@Override
	public void destroy() {
		LOGGER.info("Destroying DiskSpaceFilter");
	}

}
