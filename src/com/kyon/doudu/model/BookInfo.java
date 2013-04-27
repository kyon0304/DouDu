package com.kyon.doudu.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.kyon.doudu.DDUtils;

public class BookInfo {
	private String TAG = "com.kyon.doudu.model.BookInfo";
	
	private String title;
	private String subtitle;
	private String author;
	private String translator;
	private String publisher;
	private String pubdate;
	private String pages;
	private String isbn;
	private String price;
	private float rate;
	private String ratecount;
	private String coverurl;
	private String coverlocal;
	private String url;
	private String summary;
	
	public BookInfo() {
		Log.d(TAG, TAG);
	}
	
	public String getTitle() {
		return this.title;
	}
	public String getSubTitle() {
		return this.subtitle;
	}
	public String getAuthor() {
		return this.author;
	}
	public String getTranslator() {
		return this.translator;
	}
	public String getPublisher() {
		return this.publisher;
	}
	public String getPubDate() {
		return this.pubdate;
	}
	public String getPages() {
		return this.pages;
	}
	public String getISBN() {
		return this.isbn;
	}
	public String getPrice() {
		return this.price;
	}
	public float getRate() {
		return this.rate;
	}
	public String getRateCount() {
		return this.ratecount;
	}
	public String getCoverUrl() {
		return this.coverurl;
	}
	public String getCoverLocal() {
		return this.coverlocal;
	}
	public String getURL() {
		return this.url;
	}
	public String getSummary() {
		return this.summary;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	public void setSubTitle(String subtitle) {
		this.subtitle = subtitle;
	}
	public void setAuthor(JSONArray author){
		int l = author.length();
		String tmp = "";
		for(int i = 0; i < l; i++) {
			try {
				tmp += author.getString(i);
				tmp += ", ";
			} catch (JSONException e) {
				// Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(l > 0) {
			this.author = tmp.substring(0, tmp.lastIndexOf(","));
		}else {
			this.author = "";
		}
	}
	public void setTranslator(JSONArray translator) {
		int l = translator.length();
		String tmp = "";
		for(int i = 0; i < l; i++) {
			try {
				tmp += translator.getString(i);
				tmp += ", ";
			} catch (JSONException e) {
				// Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(l > 0){
			this.translator = tmp.substring(0, tmp.lastIndexOf(","));
		}else {
			this.translator = "";
		}
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public void setPubDate(String pubdate) {
		this.pubdate = pubdate;
	}
	public void setPages(String pages) {
		this.pages = pages;
	}
	public void setISBN(String isbn) {
		this.isbn = isbn;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public void setRate(float rate) {
		this.rate = rate;
	}
	public void setRateCount(String ratecount) {
		this.ratecount = ratecount;
	}
	public void setCoverUrl(String coverimg) {
		this.coverurl  = coverimg;
	}
	public void setCoverLocal(String coverimg) {
		this.coverlocal = coverimg;
	}
	public void setURL(String url) {
		this.url = url;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	public static BookInfo fillBIFromJson(String jsonStr) {
		BookInfo bookInfo = new BookInfo();
		try {
			JSONObject obj = new JSONObject(jsonStr);
			
			bookInfo.setTitle(obj.getString("title"));
			bookInfo.setSubTitle(obj.getString("alt_title"));
			
			JSONArray ja = obj.getJSONArray("author");
			bookInfo.setAuthor(ja);
			ja = obj.getJSONArray("translator");
			bookInfo.setTranslator(ja);

			bookInfo.setPublisher(obj.getString("publisher"));
			bookInfo.setPubDate(obj.getString("pubdate"));
			bookInfo.setPages(obj.getString("pages"));
			bookInfo.setISBN(obj.getString("isbn10"));
			bookInfo.setPrice(obj.getString("price"));
			
			JSONObject rateObj = obj.getJSONObject("rating");
			bookInfo.setRate(Float.parseFloat(rateObj.getString("average")));
			bookInfo.setRateCount(rateObj.getString("numRaters"));
			
			JSONObject imgObj = obj.getJSONObject("images");
			String imgurl= imgObj.getString("large");
//			String img = obj.getString("image");
			if(imgurl.startsWith("http")) {
				bookInfo.setCoverUrl(imgurl);
				String suffix = imgurl.substring(imgurl.lastIndexOf("."));
				String localpath = DDUtils.CacheCoverDir + bookInfo.isbn + suffix;
				bookInfo.setCoverLocal(localpath);
			}else {
				bookInfo.setCoverLocal(imgurl);
				bookInfo.setCoverUrl("");
			}

			bookInfo.setURL(obj.getString("alt"));
			bookInfo.setSummary(obj.getString("summary"));
		} catch (JSONException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		return bookInfo;
	}

}