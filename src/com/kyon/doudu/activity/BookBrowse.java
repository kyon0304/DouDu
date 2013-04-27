package com.kyon.doudu.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.kyon.doudu.DDUtils;
import com.kyon.doudu.model.BookInfo;
import com.kyon.doudu.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
//import android.view.Menu;
import android.graphics.Bitmap;

public class BookBrowse extends Activity {
	private final static String TAG = "com.kyon.doudu.activity.BookBrowse";
	
	private ImageView CoverImg = null;
	private TextView Title = null;
	private TextView Subtitle = null;
	private TextView Author = null;
	private TextView Translator = null;
	private TextView Publisher = null;
	private TextView Pubdate = null;
	private TextView ISBN = null;
	private RatingBar Rating = null;
	private TextView RatingText = null;
	private TextView Summary = null;
	
	private RelativeLayout Loading = null;
	
	private BookInfo mbookInfo = null;
	
	private int ivWidth;
	private int ivHeight;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookbrowse);
		
		DDUtils.Init(BookBrowse.this, true);
		
//		DDUtils.readJson(BookBrowse.this);
		
		mbookInfo = BookInfo.fillBIFromJson(DDUtils.offlineJson);
		
		CoverImg = (ImageView) findViewById(R.id.coverimg);
		Loading = (RelativeLayout)findViewById(R.id.loadingPanel);
		String imgPath = mbookInfo.getCoverLocal();
		//120和180为xml里面的hard code
		ivWidth = (int) DDUtils.convertDpToPixel(120, BookBrowse.this);
		ivHeight = (int) DDUtils.convertDpToPixel(180, BookBrowse.this);
		if(! new File(imgPath).exists()) {
			// 下载封面图片
			String imgUrl = mbookInfo.getCoverUrl();
			FetchCoverimg mFetch = new FetchCoverimg();
			if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
				mFetch.executeOnExecutor(
						FetchCoverimg.THREAD_POOL_EXECUTOR, imgPath, imgUrl);
			} else {
				mFetch.execute(imgPath, imgUrl);
			}
		} else {
			Loading.setVisibility(View.GONE);
			CoverImg.setVisibility(View.VISIBLE);
			Bitmap bm = DDUtils.loadBitmapFile(imgPath, ivWidth, ivHeight);
			CoverImg.setImageBitmap(bm);
			bm = null;
		}
		
		Title = (TextView) findViewById(R.id.title);
		Title.setText(mbookInfo.getTitle());
		Subtitle = (TextView)findViewById(R.id.subtitle);
		String subtitle = mbookInfo.getSubTitle();
		if(!subtitle.equals("")){
			Subtitle.setText(subtitle);
			Subtitle.setVisibility(View.VISIBLE);
		}
		
		Author = (TextView)findViewById(R.id.author);
		Author.setText(this.getString(R.string.author) + mbookInfo.getAuthor());
		
		Translator = (TextView)findViewById(R.id.translator);
		Translator.setText(this.getString(R.string.translator) + mbookInfo.getTranslator());
		
		Publisher = (TextView)findViewById(R.id.publisher);
		Publisher.setText(this.getString(R.string.publisher) + mbookInfo.getPublisher());
		Pubdate = (TextView)findViewById(R.id.pubdate);
		Pubdate.setText(this.getString(R.string.pubdate) + mbookInfo.getPubDate());
		ISBN = (TextView)findViewById(R.id.isbn);
		ISBN.setText(this.getString(R.string.isbn) + mbookInfo.getISBN());
		
		Rating = (RatingBar)findViewById(R.id.statusStar);
		Rating.setRating(mbookInfo.getRate()/10*5);
		RatingText = (TextView)findViewById(R.id.statusText);
		String tmp = this.getString(R.string.evaluate).replaceAll("X", mbookInfo.getRateCount());
		RatingText.setText(tmp);
		//tmp.replaceAll("X", mbookInfo.getRateCount());
		
		
		Summary = (TextView)findViewById(R.id.summary);
		Summary.setMovementMethod(new ScrollingMovementMethod());
		Summary.setText(mbookInfo.getSummary());
	}

	private class FetchCoverimg extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CoverImg.setVisibility(View.GONE);
			Loading.setVisibility(View.VISIBLE);
		}
		@Override
		protected String doInBackground(String... params) {
			String imgPath = params[0];
			String imgUrl = params[1];
			GetUrlFile(imgUrl, imgPath, false);
			return imgPath;
		}
		
		@Override
		protected void onPostExecute(String imgPath) {
			Loading.setVisibility(View.GONE);
			CoverImg.setVisibility(View.VISIBLE);
			if(new File(imgPath).exists()){
				Bitmap bm = DDUtils.loadBitmapFile(imgPath, ivWidth, ivHeight);
				CoverImg.setImageBitmap(bm);
			} else {
				Toast.makeText(BookBrowse.this, "封面图片下载失败",
						Toast.LENGTH_SHORT).show();
				//设置占位图片
				CoverImg.setImageResource(R.drawable.cover);
			}
		}
		
	}
	
	/**
	 * 简单下载链接方法
	 * @param url
	 * @param localPath
	 * @return
	 */
	public boolean GetUrlFile(String url, String localPath, boolean overwrite){
		Log.d(TAG, "GetUrlFile:" + url);
		boolean isSuccess = false;
		File tmpFile = new File(localPath);
		if (!overwrite && tmpFile.exists()){
			isSuccess = true;
			return isSuccess;
		}
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);

		try {
			HttpResponse response = httpClient.execute(get);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				byte[] bytes = EntityUtils.toByteArray(response.getEntity());
				FileOutputStream out = new FileOutputStream(tmpFile, false);
				out.write(bytes);
				out.flush();
				out.close();
				isSuccess = true;
			}
		} catch (ClientProtocolException e) {
			Log.d(DDUtils.TAG, e.getMessage());
		} catch (IOException e) {
			Log.d(DDUtils.TAG, e.getMessage());
		}
		return isSuccess;
	}
	
/*	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.bookbrowse, menu);
		return true;
	}
*/
}
