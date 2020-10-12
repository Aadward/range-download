package com.syh.example.rangedownload.stream.metrics;

import com.syh.example.rangedownload.util.UnitFormatter;

/**
 *
 * @author shen.yuhang
 * created on 2020/10/12
 **/
public class MetricsManager {

	private static long cnt;

	static {
		cnt = 0;
	}

	public static synchronized void collect(long size) {
		cnt += size;
	}

	public static synchronized String print() {
		return UnitFormatter.byteToHumanReadable(cnt);
	}

}
