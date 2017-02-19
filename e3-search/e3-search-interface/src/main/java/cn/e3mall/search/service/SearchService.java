package cn.e3mall.search.service;

import java.util.Map;

import cn.e3mall.common.po.E3Result;

public interface SearchService {

	public E3Result importAll();

	public Map<String, Object> search(String q, Integer page) throws Exception;
}
