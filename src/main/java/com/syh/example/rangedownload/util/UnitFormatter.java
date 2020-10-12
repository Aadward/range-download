package com.syh.example.rangedownload.util;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
 *
 * @author shen.yuhang
 * created on 2019/7/23
 **/
public class UnitFormatter {

	private static final long KB = 1024;
	private static final long MB = 1024 * KB;
	private static final long GB = 1024 * MB;

	private static Map<String, Long> countMap = ImmutableMap.of(
		"KB", KB,
		"MB", MB,
		"GB", GB
	);

	public static long humanReadableToByte(String value) {
		final String upperCaseValue = value.toUpperCase();
		return countMap.keySet()
			.stream()
			.filter(unit -> upperCaseValue.lastIndexOf(unit) > 0)
			.findFirst()
			.map(unit -> {
				String number = upperCaseValue.replace(unit, "");
				try {
					return Long.parseLong(number) * countMap.get(unit);
				} catch (Exception e) {
					throw new IllegalArgumentException("Bad format of unit: " + value);
				}
			})
			.orElseThrow(() -> new IllegalArgumentException("Bad format of unit: " + value));
	}

	public static String byteToHumanReadable(long bytes) {
		if (bytes >= GB) {
			long decimal = bytes % GB;
			return bytes / GB + "." + decimal + " GB";
		} else if (bytes >= MB) {
			long decimal = bytes % MB;
			return bytes / MB + "." + decimal + " MB";
		} else if (bytes >= KB) {
			long decimal = bytes % KB;
			return bytes / KB + "." + decimal + " KB";
		} else {
			return bytes + " Byte";
		}
	}
}
