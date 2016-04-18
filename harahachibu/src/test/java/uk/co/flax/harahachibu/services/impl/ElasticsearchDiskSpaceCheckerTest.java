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

import org.junit.Test;
import uk.co.flax.harahachibu.services.DiskSpaceCheckerException;
import uk.co.flax.harahachibu.services.DiskSpaceThreshold;
import uk.co.flax.harahachibu.services.data.ElasticsearchClusterStats;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the ElasticsearchDiskSpaceChecker.
 *
 * Created by mlp on 15/04/16.
 */
public class ElasticsearchDiskSpaceCheckerTest {

	private ElasticsearchClient elasticsearch = mock(ElasticsearchClient.class);
	private DiskSpaceThreshold threshold = mock(DiskSpaceThreshold.class);

	@Test(expected = uk.co.flax.harahachibu.services.DiskSpaceCheckerException.class)
	public void throwsExceptionWhenESFails() throws Exception {
		when(elasticsearch.getClusterStats()).thenThrow(new DiskSpaceCheckerException("error"));
		ElasticsearchDiskSpaceChecker checker = new ElasticsearchDiskSpaceChecker(elasticsearch);

		checker.isSpaceAvailable();

		verify(elasticsearch).getClusterStats();
	}

	@Test
	public void returnsFalseWhenBelowThreshold() throws Exception {
		final long freeSpace = 128L;
		final long maxSpace = 1024L;

		ElasticsearchClusterStats clusterStats = mock(ElasticsearchClusterStats.class);
		when(clusterStats.getFilesystemAvailableBytes()).thenReturn(freeSpace);
		when(clusterStats.getFilesystemTotalBytes()).thenReturn(maxSpace);
		when(elasticsearch.getClusterStats()).thenReturn(clusterStats);

		when(threshold.withinThreshold(freeSpace, maxSpace)).thenReturn(false);

		ElasticsearchDiskSpaceChecker checker = new ElasticsearchDiskSpaceChecker(elasticsearch);
		checker.setThreshold(threshold);

		assertThat(checker.isSpaceAvailable()).isFalse();
		verify(elasticsearch).getClusterStats();
		verify(threshold).withinThreshold(freeSpace, maxSpace);
	}

	@Test
	public void returnsTrueWhenAboveThreshold() throws Exception {
		final long freeSpace = 128L;
		final long maxSpace = 1024L;

		ElasticsearchClusterStats clusterStats = mock(ElasticsearchClusterStats.class);
		when(clusterStats.getFilesystemAvailableBytes()).thenReturn(freeSpace);
		when(clusterStats.getFilesystemTotalBytes()).thenReturn(maxSpace);
		when(elasticsearch.getClusterStats()).thenReturn(clusterStats);

		when(threshold.withinThreshold(freeSpace, maxSpace)).thenReturn(true);

		ElasticsearchDiskSpaceChecker checker = new ElasticsearchDiskSpaceChecker(elasticsearch);
		checker.setThreshold(threshold);

		assertThat(checker.isSpaceAvailable()).isTrue();
		verify(elasticsearch).getClusterStats();
		verify(threshold).withinThreshold(freeSpace, maxSpace);
	}

}
