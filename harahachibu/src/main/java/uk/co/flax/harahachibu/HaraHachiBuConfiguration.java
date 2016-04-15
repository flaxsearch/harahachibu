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

import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import uk.co.flax.harahachibu.config.ProxyConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Base configuration class for the Hara Hachi Bu proxy application.
 *
 * Created by mlp on 13/04/16.
 */
public class HaraHachiBuConfiguration extends Configuration {

	@Valid @NotNull
	private ProxyConfiguration proxy;

	@Valid @NotNull
	private JerseyClientConfiguration jerseyClient = new JerseyClientConfiguration();

	public ProxyConfiguration getProxy() {
		return proxy;
	}

	public void setProxy(ProxyConfiguration proxy) {
		this.proxy = proxy;
	}

	public JerseyClientConfiguration getJerseyClient() {
		return jerseyClient;
	}

	public void setJerseyClient(JerseyClientConfiguration jerseyClient) {
		this.jerseyClient = jerseyClient;
	}

}
