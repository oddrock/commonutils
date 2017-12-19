package com.oddrock.common.zip;

import java.io.File;

public class ZipResult {
	private boolean success;
	private File zipFile;
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean flag) {
		this.success = flag;
	}
	public File getZipFile() {
		return zipFile;
	}
	public void setZipFile(File zipFile) {
		this.zipFile = zipFile;
	}
}
