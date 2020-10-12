package com.syh.example.rangedownload.resource;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nullable;

/**
 *
 * @author shen.yuhang
 * created on 2020/10/12
 **/
public interface RangeSupported {

	String name();

	long length();

	@Nullable
	String eTag();

	InputStream read(long start, long end) throws IOException;

}
