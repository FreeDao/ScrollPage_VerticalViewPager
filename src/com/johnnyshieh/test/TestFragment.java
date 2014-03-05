package com.johnnyshieh.test;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;

public final class TestFragment extends Fragment {
	private static final String KEY_URL = "TestFragment:url";
	
	private ArticleContentPageWebView mView ;
	
	public static TestFragment newInstance ( String url ) {
	    TestFragment tf = new TestFragment () ;
	    
	    Bundle bundle = new Bundle () ;
	    bundle.putString ( KEY_URL, url ) ;
	    tf.setArguments ( bundle ) ;
	    
	    return tf ;
	}
	
	public String getUrl () {
	    return getArguments ().getString ( KEY_URL, null ) ;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate ( R.layout.fragment_item, null ) ;
		mView = ( ArticleContentPageWebView ) view.findViewById ( R.id.webView ) ;
		mView.setPager ( SampleActivity.mPager ) ;
		
		mView.setWebChromeClient ( new WebChromeClient () ) ;
		mView.loadUrl ( getUrl () ) ;
		
		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_URL, getUrl () );
	}
}
