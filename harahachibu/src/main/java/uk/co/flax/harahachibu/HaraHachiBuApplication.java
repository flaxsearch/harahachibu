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
import io.dropwizard.setup.Environment;
import uk.co.flax.harahachibu.servlets.DiskSpaceFilter;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

/**
 * Main application class for Hara Hachi Bu proxy application.
 *
 * Created by mlp on 13/04/16.
 */
public class HaraHachiBuApplication extends Application<HaraHachiBuConfiguration> {

	@Override
	public void run(HaraHachiBuConfiguration config, Environment environment) throws Exception {
		environment.servlets()
				.addFilter("diskSpaceFilter", new DiskSpaceFilter(null, config.getProxy(), "/set"))
				.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
//		environment.servlets().addServlet("diskSpaceProxyServlet",
//				new DiskSpaceProxyServlet(config.getProxy()))
//				.addMapping(DiskSpaceProxyServlet.PROXY_PATH_PREFIX + "/*");
	}

	public static void main(String[] args) throws Exception {
		new HaraHachiBuApplication().run(args);
	}

}
