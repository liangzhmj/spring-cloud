package com.liangzhmj.cat.dao.vo;

import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;


/**
 * 分页工具pager-taglib的工具类 主要用于接收参数
 * 
 * @author liangzhmj
 */
@NoArgsConstructor
public class Pager {

	// 分页索引
	private int offset = 0;
	// 页码
	private Integer pageSize = 30;
	// 记录数
	private Long records;
	// 结果（Object类型）
	private List results;
	private Integer currentPage=1;
	private Integer rows=0;
	
	public Pager(int pageSize) {
		this.pageSize = pageSize;
	}

	public static Pager newInstance(int pageSize,int currentPage){
		Pager pager = new Pager();
		pager.init(pageSize,currentPage);
		return pager;
	}
	
	private void init(int pageSize,int currentPage){
		this.currentPage = (currentPage==0?1:currentPage);
		if(pageSize > 0){
			this.pageSize = pageSize;
		}
		offset = pageSize*(currentPage-1);
	}

	public int getOffset() {
		return offset;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Long getRecords() {
		return records;
	}

	public void setRecords(Long records) {
		this.records = records;
	}

	public List getResults() {
		return results;
	}

	public void setResults(List results) {
		if(!CollectionUtils.isEmpty(results)){
			this.rows = results.size();
		}
		this.results = results;
	}

	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public Integer getRows() {
		return rows;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}
}
