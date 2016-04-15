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
package uk.co.flax.harahachibu.services;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the DiskSpaceThreshold parser.
 *
 * Created by mlp on 15/04/16.
 */
public class DiskSpaceThresholdTest {

	@Test(expected=uk.co.flax.harahachibu.services.DiskSpaceCheckerException.class)
	public void throwsExceptionForBadString() throws Exception {
		DiskSpaceThreshold.parse("abcd");
	}

	@Test(expected=uk.co.flax.harahachibu.services.DiskSpaceCheckerException.class)
	public void throwsExceptionForNegativeString() throws Exception {
		DiskSpaceThreshold.parse("-5");
	}

	@Test
	public void parsesStandaloneValue() throws Exception {
		DiskSpaceThreshold t = DiskSpaceThreshold.parse("1234");
		assertThat(t.getThresholdValue()).isEqualTo(1234L);
		assertThat(t.isPercentage()).isFalse();
	}

	@Test
	public void parsesPercentage() throws Exception {
		DiskSpaceThreshold t = DiskSpaceThreshold.parse("5%");
		assertThat(t.getThresholdValue()).isEqualTo(5L);
		assertThat(t.isPercentage()).isTrue();
	}

	@Test
	public void parsesPercentageWithSpace() throws Exception {
		DiskSpaceThreshold t = DiskSpaceThreshold.parse("5 %");
		assertThat(t.getThresholdValue()).isEqualTo(5L);
		assertThat(t.isPercentage()).isTrue();
	}

	@Test(expected=uk.co.flax.harahachibu.services.DiskSpaceCheckerException.class)
	public void throwsExceptionForBadPercentage() throws Exception {
		DiskSpaceThreshold.parse("105 %");
	}

	@Test
	public void parsesKilobytes() throws Exception {
		DiskSpaceThreshold t = DiskSpaceThreshold.parse("5K");
		assertThat(t.getThresholdValue()).isEqualTo(5 * 1024);
		assertThat(t.isPercentage()).isFalse();
	}

	@Test
	public void parsesMegabytes() throws Exception {
		DiskSpaceThreshold t = DiskSpaceThreshold.parse("5M");
		assertThat(t.getThresholdValue()).isEqualTo(5 * 1024 * 1024);
		assertThat(t.isPercentage()).isFalse();
	}

	@Test
	public void parsesGigabytes() throws Exception {
		DiskSpaceThreshold t = DiskSpaceThreshold.parse("5G");
		// Java starts getting its maths wrong if expected isn't calculated as below
		final long expected = 5 * (long)(1024 * 1024 * 1024);
		assertThat(t.getThresholdValue()).isEqualTo(expected);
		assertThat(t.isPercentage()).isFalse();
	}


	@Test
	public void valueWithinThresholdPasses() {
		DiskSpaceThreshold t = new DiskSpaceThreshold(1024L, false);
		assertThat(t.withinThreshold(1200, 1500)).isTrue();
	}

	@Test
	public void valueOutsideThresholdFails() {
		DiskSpaceThreshold t = new DiskSpaceThreshold(1024L, false);
		assertThat(t.withinThreshold(500, 1500)).isFalse();
	}

	@Test
	public void percentageValueWithinThresholdPasses() {
		DiskSpaceThreshold t = new DiskSpaceThreshold(5L, true);
		assertThat(t.withinThreshold(600, 1000)).isTrue();
	}

	@Test
	public void percentageValueOutsideThresholdFails() {
		DiskSpaceThreshold t = new DiskSpaceThreshold(5L, true);
		assertThat(t.withinThreshold(50, 1000)).isFalse();
	}

	@Test
	public void percentageWithZeroTotalFails() {
		DiskSpaceThreshold t = new DiskSpaceThreshold(5L, true);
		assertThat(t.withinThreshold(0, 0)).isFalse();
	}

}
