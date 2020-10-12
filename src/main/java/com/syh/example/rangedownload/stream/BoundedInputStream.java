package com.syh.example.rangedownload.stream;

import java.io.IOException;
import java.io.InputStream;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author shen.yuhang
 * created on 2020/10/12
 **/
@Slf4j
public class BoundedInputStream extends InputStream {

	private InputStream in;
	private long remaining;

	public BoundedInputStream(InputStream in, long start, long end) throws IOException {
		this.in = in;

		if (end < start) {
			throw new IllegalArgumentException("start should not greater than end");
		}

		if (in.skip(start) < start) {
			throw new IOException("Unable to skip leading bytes");
		}

		remaining = end - start + 1;
	}

	@Override
	public int read() throws IOException {
		return --remaining >= 0 ? in.read() : -1;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (remaining > len) {
			remaining -= len;
			return in.read(b, off, len);
		} else if (remaining > 0) {
			int ret = in.read(b, off, (int)remaining);
			remaining = 0;
			return ret;
		} else {
			return -1;
		}
	}

	@Override
	public void close() throws IOException {
		in.close();
	}
}
