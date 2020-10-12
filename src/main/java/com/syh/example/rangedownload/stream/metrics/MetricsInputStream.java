package com.syh.example.rangedownload.stream.metrics;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author shen.yuhang
 * created on 2020/10/12
 **/
public class MetricsInputStream extends InputStream {

	private InputStream in;

	public MetricsInputStream(InputStream in) {
		this.in = in;
	}

	@Override
	public int read() throws IOException {
		MetricsManager.collect(1);
		return in.read();
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int ret = in.read(b, off, len);
		MetricsManager.collect(ret);
		return ret;
	}

	@Override
	public int read(byte[] b) throws IOException {
		int ret = in.read(b);
		MetricsManager.collect(ret);
		return ret;
	}

	@Override
	public void close() throws IOException {
		in.close();
	}
}
