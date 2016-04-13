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
package uk.co.flax.harahachibu.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for the proxy servlet.
 * <p>
 * Created by mlp on 13/04/16.
 */
public class ProxyConfiguration {

	private int errorStatus = 500;

	private String destinationServer;

	private List<String> checkUrls = new ArrayList<>();

	public int getErrorStatus() {
		return errorStatus;
	}

	public void setErrorStatus(int status) {
		this.errorStatus = status;
	}

	public String getDestinationServer() {
		return destinationServer;
	}

	public void setDestinationServer(String destinationServer) {
		this.destinationServer = destinationServer;
	}

	public List<String> getCheckUrls() {
		return checkUrls;
	}

	public void setCheckUrls(List<String> checkUrls) {
		this.checkUrls = checkUrls;
	}
}
