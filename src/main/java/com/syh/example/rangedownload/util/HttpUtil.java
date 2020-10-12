package com.syh.example.rangedownload.util;

import org.apache.tika.Tika;

import com.google.common.net.PercentEscaper;

/**
 *
 * @author shen.yuhang
 * created on 2020/10/12
 **/
public class HttpUtil {

	private static final String URL_FORM_PARAMETER_OTHER_SAFE_CHARS = "-_.*";

	public static String getContentType(String fileName) {
		Tika tika = new Tika();
		return tika.detect(fileName);
	}

	@SuppressWarnings("UnstableApiUsage")
	public static String escapeContentDisposition(String str) {
		PercentEscaper percentEscaper = new PercentEscaper(URL_FORM_PARAMETER_OTHER_SAFE_CHARS, false);
		return percentEscaper.escape(str);
	}
}
