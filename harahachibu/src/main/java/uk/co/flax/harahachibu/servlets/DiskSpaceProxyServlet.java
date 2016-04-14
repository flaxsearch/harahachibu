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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.proxy.ProxyServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * Proxy servlet implementation to pass requests through, post-disk
 * space checks.
 * <p>
 * Created by mlp on 13/04/16.
 */
public class DiskSpaceProxyServlet extends ProxyServlet {

	static final String PROXY_PATH_PREFIX = "/proxy";

	static final String DESTINATION_SERVER_PARAM = "destinationServerPrefix";

	private static final Logger LOGGER = LoggerFactory.getLogger(DiskSpaceProxyServlet.class);

	@Override
	protected String rewriteTarget(HttpServletRequest request) {
		final String target;

		if (!validateDestination(request.getServerName(), request.getServerPort())) {
			target = null;
		} else {
			final String path = request.getRequestURI();

			if (StringUtils.isBlank(path)) {
				LOGGER.debug("No path given extracted from {}", request.getRequestURI());
				target = null;
			} else if (!path.startsWith(PROXY_PATH_PREFIX)) {
				target = null;
			} else {
				final String shortPath = path.substring(PROXY_PATH_PREFIX.length());
				final StringBuilder targetBuilder = new StringBuilder(getInitParameter(DESTINATION_SERVER_PARAM))
						.append(shortPath);
				if (StringUtils.isNotBlank(request.getQueryString())) {
					targetBuilder.append("?").append(request.getQueryString());
				}

				target = targetBuilder.toString();
			}
		}

		return target;
	}

}
