package com.oddrock.common.file;

import java.io.File;

public class FileZipResult {
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
