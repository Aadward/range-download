package com.syh.example.rangedownload;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.syh.example.rangedownload.resource.RangeSupported;
import com.syh.example.rangedownload.util.HttpUtil;

/**
 *
 * @author shen.yuhang
 * created on 2020/10/10
 **/
public class MultipartFileSender {

	private static final String MULTIPART_BOUNDARY = "MULTIPART_BYTERANGES";

	private RangeSupported resource;

	private HttpServletRequest request;
	private HttpServletResponse response;

	private List<HttpRange> ranges;

	private MultipartFileSender(RangeSupported resource) {
		this.resource = resource;
		this.ranges = Lists.newArrayList();
	}

	public static MultipartFileSender create(RangeSupported resource) {
		return new MultipartFileSender(resource);
	}

	public MultipartFileSender with(HttpServletRequest request) {
		this.request = request;
		return this;
	}

	public MultipartFileSender with(HttpServletResponse response) {
		this.response = response;
		return this;
	}

	public void checkAfterSet() {
		Assert.notNull(request, "request should not be null");
		Assert.notNull(response, "response should not be null");
		Assert.notNull(resource, "range resource should not be empty");
	}

	private HttpRange full() {
		return HttpRange.createByteRange(0, resource.length() - 1);
	}

	public void done() throws IOException {
		checkAfterSet();

		String rangeStr = request.getHeader(HttpHeaders.RANGE);
		if (!Strings.isNullOrEmpty(rangeStr)) {
			this.ranges.addAll(HttpRange.parseRanges(rangeStr));
		}

		// check eTag, if don't match, return fail
		String ifMatch = request.getHeader(HttpHeaders.IF_MATCH);
		if (ifMatch != null) {
			if (resource.eTag() == null || !ifMatch.equals(resource.eTag())) {
				response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
				return;
			}
		}

		// check eTag, if don't match, download full
		String ifRange = request.getHeader(HttpHeaders.IF_RANGE);
		if (ifRange != null && resource.eTag() != null && !ifRange.equals(resource.eTag())) {
			// eTag not match, download full
			ranges.clear();
		}

		String contentType = HttpUtil.getContentType(resource.name());
		response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
		if (resource.eTag() != null) {
			response.setHeader(HttpHeaders.ETAG, resource.eTag());
		}
		if (ranges.isEmpty()) {
			// If no ranges set, download full

			HttpRange range = full();

			long start = range.getRangeStart(resource.length());
			long end = range.getRangeEnd(resource.length());

			response.setStatus(HttpStatus.OK.value());    // 200
			response.setHeader(HttpHeaders.CONTENT_TYPE, contentType);
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename*=UTF-8''"
				+ HttpUtil.escapeContentDisposition(resource.name()));
			response.setHeader(HttpHeaders.CONTENT_RANGE,
				"bytes " + start + "-" + end + "/" + (resource.length() - 1));
			response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(end - start + 1));

			copy(range, response.getOutputStream());

		} else if (ranges.size() == 1) {
			// single
			HttpRange range = ranges.get(0);
			long start = range.getRangeStart(resource.length());
			long end = range.getRangeEnd(resource.length());

			response.setStatus(HttpStatus.PARTIAL_CONTENT.value());  // 206
			response.setHeader(HttpHeaders.CONTENT_TYPE, contentType);
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename*=UTF-8''"
				+ HttpUtil.escapeContentDisposition(resource.name()));
			response.setHeader(HttpHeaders.CONTENT_RANGE,
				"bytes " + start + "-" + end + "/" + (resource.length() - 1));
			response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(end - start + 1));

			copy(range, response.getOutputStream());
		} else {
			// multipart

			response.setContentType("multipart/byteranges; boundary=" + MULTIPART_BOUNDARY);
			response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);    // 206

			ServletOutputStream sos = response.getOutputStream();
			for (HttpRange range : ranges) {
				// Add multipart boundary and header fields for every range.
				long start = range.getRangeStart(resource.length());
				long end = range.getRangeEnd(resource.length());

				sos.println();
				sos.println("--" + MULTIPART_BOUNDARY);
				sos.println("Content-Type: " + contentType);
				sos.println("Content-Range: bytes " + start + "-" + end + "/" + (resource.length() - 1));

				// Copy single part range of multi part range.
				copy(range, sos);
			}
			sos.println();
			sos.println("--" + MULTIPART_BOUNDARY + "--");
		}

	}

	private void copy(HttpRange range, OutputStream os) throws IOException {
		long start = range.getRangeStart(resource.length());
		long end = range.getRangeEnd(resource.length());
		try (InputStream in = resource.read(start, end)) {
			IOUtils.copy(in, os);
		}
	}
}
