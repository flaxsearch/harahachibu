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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for parsing a disk space threshold string, and for checking values
 * to see if they pass.
 * <p>
 * Created by mlp on 15/04/16.
 */
public class DiskSpaceThreshold {

	private static final Logger LOGGER = LoggerFactory.getLogger(DiskSpaceThreshold.class);

	private static final Pattern THRESHOLD_PATTERN = Pattern.compile("^(\\d+)\\s*([KMG%])?$", Pattern.CASE_INSENSITIVE);

	private static final int KILOBYTE = 1024;

	private long thresholdValue;
	private boolean percentage;

	DiskSpaceThreshold(final long threshold, final boolean isPercentage) {
		this.thresholdValue = threshold;
		this.percentage = isPercentage;
	}

	/**
	 * Parse a threshold string and return the corresponding DiskSpaceThreshold object.
	 * @param thresholdString the string to parse.
	 * @return a DiskSpaceThreshold representing the threshold value, and whether it
	 * is a percentage.
	 * @throws DiskSpaceCheckerException if the string cannot be parsed, or the value
	 * makes no sense (percentage above 100, negative value, etc.).
	 */
	public static DiskSpaceThreshold parse(String thresholdString) throws DiskSpaceCheckerException {
		if (thresholdString.startsWith("-")) {
			throw new DiskSpaceCheckerException("Negative threshold string not allowed: " + thresholdString);
		}

		Matcher m = THRESHOLD_PATTERN.matcher(thresholdString);
		if (!m.matches()) {
			throw new DiskSpaceCheckerException("Cannot parse threshold string " + thresholdString);
		}

		DiskSpaceThreshold t;

		long value = Long.valueOf(m.group(1));
		if (StringUtils.isBlank(m.group(2))) {
			t = new DiskSpaceThreshold(value, false);
		} else {
			String unit = m.group(2).toUpperCase();

			switch (unit) {
				case "%":
					if (value > 100) {
						throw new DiskSpaceCheckerException("Percentage value higher than 100%: " + thresholdString);
					} else {
						t = new DiskSpaceThreshold(value, true);
					}
					break;
				case "G":
					value = value * KILOBYTE;
					// FALL THROUGH
				case "M":
					value = value * KILOBYTE;
					// FALL THROUGH
				case "K":
					value = value * KILOBYTE;
					t = new DiskSpaceThreshold(value, false);
					break;
				default:
					throw new DiskSpaceCheckerException("Unexpected unit value: " + unit);
			}
		}

		return t;
	}

	public long getThresholdValue() {
		return thresholdValue;
	}

	public boolean isPercentage() {
		return percentage;
	}

	/**
	 * Check whether or not the space available is within this threshold.
	 * @param freeSpace the free space on the drive.
	 * @param maxSpace the maximum space on the drive (required for percentage
	 * thresholds).
	 * @return {code true} if the space is within the threshold value.
	 */
	public boolean withinThreshold(long freeSpace, long maxSpace) {
		boolean ret = true;

		if (isPercentage()) {
			// Work out percentage free space
			if (maxSpace == 0) {
				LOGGER.warn("Skipping withinThreshold() percentage calculation - maxSpace == 0");
				ret = false;
			} else {
				int freePercent = Math.round(((float) freeSpace / maxSpace) * 100);
				if (freePercent <= thresholdValue) {
					ret = false;
				}
			}
		} else {
			if (freeSpace <= thresholdValue) {
				ret = false;
			}
		}

		return ret;
	}

}
