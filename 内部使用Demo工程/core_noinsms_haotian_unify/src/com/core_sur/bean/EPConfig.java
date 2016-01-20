package com.core_sur.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class EPConfig {
	public ArrayList<String> customAds;
	public Map<String, Integer> adPrices = new HashMap<String, Integer>();
	public Map<Integer, String> provinces = new HashMap<Integer, String>();

	public ArrayList<String> getCustomAds() {
		return customAds;
	}

	public void setCustomAds(ArrayList<String> customAds) {
		this.customAds = customAds;
	}

	public Map<String, Integer> getAdPrices() {
		return adPrices;
	}

	public void setAdPrices(Map<String, Integer> adPrices) {
		this.adPrices = adPrices;
	}

	public Map<Integer, String> getProvinces() {
		return provinces;
	}

	public void setProvinces(Map<Integer, String> provinces) {
		this.provinces = provinces;
	}

	public int getAppId() {
		return appId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

	public int appId;
}