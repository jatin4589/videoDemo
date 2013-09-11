package com.example.videodemo;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.drm.DrmManagerClient.OnErrorListener;
import android.util.Log;
import android.view.Menu;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends Activity {
	
	private VideoView videoViewer;
	private ProgressBar mProgressBar;
	String videoUrl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		videoViewer=(VideoView)findViewById(R.id.videoView1);
		//videoViewer.setVideoPath("/sdcard/documentariesandyou.mp4");
		String path1="http://www.metacafe.com/watch/292662/we_are_very_small/";

	   /* Uri uri=Uri.parse(path1);
		http://www.youtube.com/watch?v=Hxy8BZGQ5Jo
		videoViewer.setVideoURI(uri);	*/
			
		
		mProgressBar = (ProgressBar) findViewById(R.id.Progressbar);
        mProgressBar.setProgress(0);
        mProgressBar.setMax(100);
        
      
		
       new YourAsyncTask().execute();
		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		Toast.makeText(MainActivity.this, "Pause Video", Toast.LENGTH_LONG).show();
	
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

		Toast.makeText(MainActivity.this, "Stop Video", Toast.LENGTH_LONG).show();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Toast.makeText(MainActivity.this, "Destroy Video", Toast.LENGTH_LONG).show();
		
		
	}
	
	/* private class myAsync extends AsyncTask<Void, Integer, Void>
	    {
	        int duration = 0;
	        int current = 0;
	        @Override
	        protected Void doInBackground(Void... params) {

	        	videoViewer.start();
	        	videoViewer.setOnPreparedListener(new OnPreparedListener() {

	                public void onPrepared(MediaPlayer mp) {
	                    duration = videoViewer.getDuration();
	                }
	            });

	            do {
	                current = videoViewer.getCurrentPosition();
	                System.out.println("duration - " + duration + " current- "
	                        + current);
	                try {
	                    publishProgress((int) (current * 100 / duration));
	                    if(mProgressBar.getProgress() >= 100){
	                        break;
	                    }
	                } catch (Exception e) {
	                }
	            } while (mProgressBar.getProgress() <= 100);

	            return null;
	        }

	        @Override
	        protected void onProgressUpdate(Integer... values) {
	            super.onProgressUpdate(values);
	            System.out.println(values[0]);
	            mProgressBar.setProgress(values[0]);
	        }
	    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}*/

	
	private class YourAsyncTask extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MainActivity.this, "", "Loading Video wait...", true);
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
               // String url = "http://www.youtube.com/watch?v=Hxy8BZGQ5Jo";
                String url = "https://www.youtube.com/watch?v=SyrO83x7g-E";
                videoUrl = getUrlVideoRTSP(url);
                Log.e("Video url for playing=========>>>>>", videoUrl);
            }
            catch (Exception e)
            {
                Log.e("Login Soap Calling in Exception", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            progressDialog.dismiss();
/*
            videoView.setVideoURI(Uri.parse("rtsp://v4.cache1.c.youtube.com/CiILENy73wIaGQk4RDShYkdS1BMYDSANFEgGUgZ2aWRlb3MM/0/0/0/video.3gp"));
            videoView.setMediaController(new MediaController(AlertDetail.this));
            videoView.requestFocus();
            videoView.start();*/            
            videoViewer.setVideoURI(Uri.parse(videoUrl));
            MediaController mc = new MediaController(MainActivity.this);
            videoViewer.setMediaController(mc);
            videoViewer.requestFocus();
            videoViewer.start();          
            mc.show();
            
            
            
        }

    }

public static String getUrlVideoRTSP(String urlYoutube)
    {
        try
        {
            String gdy = "http://gdata.youtube.com/feeds/api/videos/";
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            String id = extractYoutubeId(urlYoutube);
            URL url = new URL(gdy + id);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            Document doc = documentBuilder.parse(connection.getInputStream());
            Element el = doc.getDocumentElement();
            NodeList list = el.getElementsByTagName("media:content");///media:content
            String cursor = urlYoutube;
            for (int i = 0; i < list.getLength(); i++)
            {
                Node node = list.item(i);
                if (node != null)
                {
                    NamedNodeMap nodeMap = node.getAttributes();
                    HashMap<String, String> maps = new HashMap<String, String>();
                    for (int j = 0; j < nodeMap.getLength(); j++)
                    {
                        Attr att = (Attr) nodeMap.item(j);
                        maps.put(att.getName(), att.getValue());
                    }
                    if (maps.containsKey("yt:format"))
                    {
                        String f = maps.get("yt:format");
                        if (maps.containsKey("url"))
                        {
                            cursor = maps.get("url");
                        }
                        if (f.equals("1"))
                            return cursor;
                    }
                }
            }
            return cursor;
        }
        catch (Exception ex)
        {
            Log.e("Get Url Video RTSP Exception======>>", ex.toString());
        }
        return urlYoutube;

    }

protected static String extractYoutubeId(String url) throws MalformedURLException
    {
        String id = null;
        try
        {
            String query = new URL(url).getQuery();
            if (query != null)
            {
                String[] param = query.split("&");
                for (String row : param)
                {
                    String[] param1 = row.split("=");
                    if (param1[0].equals("v"))
                    {
                        id = param1[1];
                    }
                }
            }
            else
            {
                if (url.contains("embed"))
                {
                    id = url.substring(url.lastIndexOf("/") + 1);
                }
            }
        }
        catch (Exception ex)
        {
            Log.e("Exception", ex.toString());
        }
        return id;
    }
}
