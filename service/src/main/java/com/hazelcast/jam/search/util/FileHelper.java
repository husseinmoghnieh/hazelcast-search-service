package com.hazelcast.jam.search.util;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import java.net.URL;


public class FileHelper {

	public static String readSQL(String fileName) {
		try {

			URL url = Resources.getResource(fileName);
			return Resources.toString(url, Charsets.UTF_8);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}