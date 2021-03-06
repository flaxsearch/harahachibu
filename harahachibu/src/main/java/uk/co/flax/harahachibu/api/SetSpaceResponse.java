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
package uk.co.flax.harahachibu.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response representation for the /setSpace endpoint.
 *
 * Created by mlp on 18/04/16.
 */
public class SetSpaceResponse {

	public enum ResponseCode {
		OK, ERROR
	};

	@JsonProperty("status")
	private final ResponseCode responseCode;
	@JsonProperty("message")
	private final String message;

	public SetSpaceResponse(ResponseCode code, String message) {
		this.responseCode = code;
		this.message = message;
	}

	public static SetSpaceResponse okResponse() {
		return new SetSpaceResponse(ResponseCode.OK, null);
	}

	public ResponseCode getResponseCode() {
		return responseCode;
	}

	public String getMessage() {
		return message;
	}
}
