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
package uk.co.flax.harahachibu.resources;

import uk.co.flax.harahachibu.api.SetSpaceResponse;
import uk.co.flax.harahachibu.services.ClusterDiskSpaceManager;
import uk.co.flax.harahachibu.services.DiskSpaceCheckerException;
import uk.co.flax.harahachibu.services.data.DiskSpace;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Endpoint to allow disk space to be set for remote servers.
 *
 * Created by mlp on 14/04/16.
 */
@Path("/setSpace/{host}/{freeSpace}/{maxSpace}")
public class SetSpaceResource {

	private final ClusterDiskSpaceManager clusterManager;

	public SetSpaceResource(ClusterDiskSpaceManager clusterManager) {
		this.clusterManager = clusterManager;
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public SetSpaceResponse handlePost(@PathParam("host") String server,
									   @PathParam("freeSpace") long freeSpace,
									   @PathParam("maxSpace") long maxSpace) {
		SetSpaceResponse response;

		try {
			clusterManager.setDiskSpace(server, new DiskSpace(freeSpace, maxSpace));
			response = SetSpaceResponse.okResponse();
		} catch (DiskSpaceCheckerException e) {
			response = new SetSpaceResponse(SetSpaceResponse.ResponseCode.ERROR, e.getMessage());
		}

		return response;
	}

}
