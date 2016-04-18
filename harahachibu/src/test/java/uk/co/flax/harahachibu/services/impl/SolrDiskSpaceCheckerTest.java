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
package uk.co.flax.harahachibu.services.impl;

import org.junit.Before;
import org.junit.Test;
import uk.co.flax.harahachibu.services.DiskSpaceThreshold;

import java.io.IOException;
import java.nio.file.FileStore;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the local Solr disk space checker implementation.
 *
 * Created by mlp on 18/04/16.
 */
public class SolrDiskSpaceCheckerTest {

	private SolrDiskSpaceChecker checker;
	private Map<String, Object> configuration = new HashMap<>();
	private DiskSpaceThreshold threshold = mock(DiskSpaceThreshold.class);

	@Before
	public void setup() {
		checker = new SolrDiskSpaceChecker();
		checker.setThreshold(threshold);
	}

	@Test(expected = uk.co.flax.harahachibu.services.DiskSpaceCheckerException.class)
	public void configureThrowsExceptionWhenNoDataDirConfigured() throws Exception {
		checker.configure(configuration);
	}

	@Test(expected = uk.co.flax.harahachibu.services.DiskSpaceCheckerException.class)
	public void configureThrowsExceptionWhenDataDirNotExist() throws Exception {
		configuration.put(SolrDiskSpaceChecker.DATA_DIR_CONFIG_OPTION, "/no_such_path");
		checker.configure(configuration);
	}

	@Test(expected = uk.co.flax.harahachibu.services.DiskSpaceCheckerException.class)
	public void throwsExceptionWhenFileStoreThrowsException() throws Exception {
		FileStore fs = mock(FileStore.class);
		when(fs.getUsableSpace()).thenThrow(new IOException("Error"));
		checker.setFileStore(fs);

		checker.isSpaceAvailable();
	}

	@Test
	public void returnsResultFromThreshold() throws Exception {
		final long freeSpace = 128L;
		final long maxSpace = 1024L;

		FileStore fs = mock(FileStore.class);
		when(fs.getUsableSpace()).thenReturn(freeSpace);
		when(fs.getTotalSpace()).thenReturn(maxSpace);
		checker.setFileStore(fs);

		when(threshold.withinThreshold(freeSpace, maxSpace)).thenReturn(false);

		boolean result = checker.isSpaceAvailable();
		assertThat(result).isFalse();

		verify(fs).getUsableSpace();
		verify(fs).getTotalSpace();
		verify(threshold).withinThreshold(freeSpace, maxSpace);
	}

}
