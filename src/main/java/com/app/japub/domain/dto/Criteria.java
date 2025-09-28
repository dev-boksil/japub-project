package com.app.japub.domain.dto;

import org.springframework.web.util.UriComponentsBuilder;

import lombok.Data;

@Data
public class Criteria {
	private int page, amount, pageRange;
	private String type, keyword, category, referrer, sort;

	public Criteria() {
		this(1, 10, 5, "recent");
	}

	public Criteria(int page, int amount, int pageRange, String sort) {
		this.page = page;
		this.amount = amount;
		this.pageRange = pageRange;
		this.sort = sort;
	}

	public int getOffset() {
		return (page - 1) * amount;
	}

	public int getEndRow() {
		return page * amount;
	}

	public String[] getTypes() {
		return type == null ? new String[0] : type.split("");
	}

	public String getParams() {
		return UriComponentsBuilder.newInstance().queryParam("page", page).queryParam("type", type)
				.queryParam("keyword", keyword).queryParam("category", category).queryParam("sort", sort).toUriString();
	}

}
