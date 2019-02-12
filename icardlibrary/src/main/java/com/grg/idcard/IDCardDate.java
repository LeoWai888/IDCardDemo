package com.grg.idcard;

/**
 * 
 *身份证日期实体类
 * 
 *@author zjxin2 on 2016-03-24
 *@version  
 *
 */
public class IDCardDate {
	private int year;
	private int month;
	private int day;

	public IDCardDate() {
	}

	public IDCardDate(int year, int month, int day) {
		this.year = year;
		this.month = month;
		this.day = day;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public String toString() {
		return String.valueOf(year) + "-"
				+ ((month < 10) ? ("0" + String.valueOf(month)) : String.valueOf(month)) + "-"
				+ ((day < 10) ? ("0" + String.valueOf(day)) : String.valueOf(day));
	}
}
