package com.oddrock.common.time;

import java.util.Date;

import com.oddrock.common.DateUtils;

public class Timer {
	private Long startTime;
	private Long endTime;
	public Long getStartTime() {
		return this.startTime;
	}
	public Date getStartTimeInDate() {
		return DateUtils.millis2Date(this.startTime);
	}
	public Long getEndTime() {
		return this.endTime;
	}
	public Date getEndTimeInDate() {
		return DateUtils.millis2Date(this.endTime);
	}
	// 开始计时
	public Timer start() {
		this.startTime = System.currentTimeMillis();
		return this;
	}
	// 结束计时
	public Timer end() {
		this.endTime = System.currentTimeMillis();
		return this;
	}
	// 重置
	public Timer reset() {
		this.startTime = System.currentTimeMillis();
		this.endTime = -1L;
		return this;
	}
	// 获得当前已花费时间，单位是毫秒
	public Long getSpentTimeMillis() {
		if(this.startTime<0) {
			return -1L;
		}else if(this.endTime>0) {
			return this.endTime-this.startTime;
		}else {
			return System.currentTimeMillis()-this.startTime;
		}
	}
	public Timer() {
		super();
		this.startTime = -1L;
		this.endTime = -1L;
	}
}