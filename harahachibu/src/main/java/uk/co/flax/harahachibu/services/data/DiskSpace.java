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
package uk.co.flax.harahachibu.services.data;

import java.util.Date;

/**
 * POJO representing disk space information.
 *
 * Created by mlp on 18/04/16.
 */
public class DiskSpace {

	private final long freeSpace;
	private final long maxSpace;
	private final Date creationDate = new Date();

	public DiskSpace(long freeSpace, long maxSpace) {
		this.freeSpace = freeSpace;
		this.maxSpace = maxSpace;
	}

	public long getFreeSpace() {
		return freeSpace;
	}

	public long getMaxSpace() {
		return maxSpace;
	}

	public Date getCreationDate() {
		return creationDate;
	}

}
