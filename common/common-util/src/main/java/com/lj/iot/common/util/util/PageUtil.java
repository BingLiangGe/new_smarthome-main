package com.lj.iot.common.util.util;

import java.util.List;

/**
 * bootstrapTable对应json格式
 * 
 * @author tyj
 * @Date 2018-7-12 14:00:20
 */
public class PageUtil<T> {

	private List<?> rows;// 数据

	private long total;// 总条数

	public List<?> getRows() {
		return rows;
	}

	public void setRows(List<?> rows) {
		this.rows = rows;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	@Override
	public String toString() {
		return "PageUtil [rows=" + rows + ", total=" + total + "]";
	}

}
