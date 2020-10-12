package com.syh.example.rangedownload.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nullable;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.syh.example.rangedownload.stream.BoundedInputStream;
import com.syh.example.rangedownload.stream.metrics.MetricsInputStream;

/**
 *
 * @author shen.yuhang
 * created on 2020/10/10
 **/
public class LocalRangedResource implements RangeSupported {

	private File file;

	public LocalRangedResource(String path) throws FileNotFoundException {
		this.file = new File(path);
		if (!file.exists() || !file.isFile()) {
			throw new FileNotFoundException("Can not find file: " + path);
		}
	}

	@Override
	public String name() {
		return file.getName();
	}

	@Override
	public long length() {
		return file.length();
	}

	@Nullable
	@Override
	public String eTag() {
		try {
			return Files.asByteSource(file).hash(Hashing.md5()).toString();
		} catch (IOException e) {
			throw new RuntimeException("Hashing file fail", e);
		}
	}

	@Override
	public InputStream read(long start, long end) throws IOException {
		return new MetricsInputStream(new BoundedInputStream(new FileInputStream(file), start, end));
	}

}
