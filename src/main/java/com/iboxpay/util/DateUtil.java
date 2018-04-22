package com.iboxpay.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期与字符串转换的工具类
 * @author liuyalou 
 */
public class DateUtil {

	/**
	 * 默认转换格式
	 */
	private static final String PATTERN = "yyyy-MM-dd HH:mm:ss:SS";

	private static DateFormat df = new SimpleDateFormat(PATTERN);

	/**
	 * 将字符串按格式转换为日期
	 * 
	 * @param dateStr
	 *            日期字符串
	 * @param pattern
	 *            转换格式
	 * @return 转换后的日期
	 * @throws ParseException
	 */
	public static Date parseStringToDate(String dateStr, String pattern) throws ParseException {
		if (pattern != null) {
			df = new SimpleDateFormat(pattern);
		}
		Date date = df.parse(dateStr);
		return date;
	}

	/**
	 * 将日期按格式转换为字符串
	 * 
	 * @paramdated
	 *            日期
	 * @param format
	 *            转换格式
	 * @return 格式化后的日期字符串
	 */
	public static String parseDateToString(Date date, String pattern) {
		if (pattern != null) {
			df = new SimpleDateFormat(pattern);
		}
		String dateStr = df.format(date);
		return dateStr;
	}

	/**
	 * 将日期按默认模式转换为日期字符串，如：<b>"yyyy-MM-dd HH:mm:ss:SS"</b>
	 * @param date 日期
	 * @return 格式化的日期字符串
	 */
	public static String parseDateToString(Date date) {
		String dateStr = parseDateToString(date, null);
		return dateStr;
	}
}
