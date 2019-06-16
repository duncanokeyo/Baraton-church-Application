/*
 * Copyright (c) 2016 Adventech <info@adventech.io>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.dans.apps.baratonchurch.widget;


import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.ActionMode;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dans.apps.baratonchurch.ReadActivity;
import com.dans.apps.baratonchurch.utils.LogUtils;
import com.thefinestartist.finestwebview.FinestWebView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.content.Context.CLIPBOARD_SERVICE;

public class ReadView extends WebView {
    private static final String TAG = "ReadView";

    public static final String SEARCH_PROVIDER = "https://www.google.com/search?q=%s";
    public static final String CLIPBOARD_LABEL = "clipboard_label";
    private static final String bridgeName = "SSBridge";

    private GestureDetectorCompat gestureDetector;
    private ContextMenuCallback contextMenuCallback;
    private HighlightsCommentsCallback highlightsCommentsCallback;

    private String ssReaderContent;

    public ReadViewBridge ssReadViewBridge;

    private float LastTouchX;
    private float LastTouchY;
    private boolean textAreaFocused = false;
    public boolean contextMenuShown = false;

    Context context;
    //public SSReadHighlights ssReadHighlights;

    public ReadView(final Context context) {
        super(context);
        this.context = context;
        if (!isInEditMode()) {
            gestureDetector = new GestureDetectorCompat(context, new GestureListener());
            ssReadViewBridge = new ReadViewBridge(context);
            this.setWebViewClient(new SSWebViewClient());
            this.getSettings().setJavaScriptEnabled(true);
            this.addJavascriptInterface(ssReadViewBridge, bridgeName);
        }
    }

    public ReadView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            gestureDetector = new GestureDetectorCompat(context, new GestureListener());
            ssReadViewBridge = new ReadViewBridge(context);
            this.setWebViewClient(new SSWebViewClient());
            this.getSettings().setJavaScriptEnabled(true);
            this.addJavascriptInterface(ssReadViewBridge, bridgeName);
        }
    }

    public ReadView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        if (!isInEditMode()) {
            gestureDetector = new GestureDetectorCompat(context, new GestureListener());
            ssReadViewBridge = new ReadViewBridge(context);
            this.setWebViewClient(new SSWebViewClient());
            this.getSettings().setJavaScriptEnabled(true);
            this.addJavascriptInterface(ssReadViewBridge, bridgeName);
        }
    }

    @Override
    public ActionMode startActionMode(ActionMode.Callback callback, int type) {
        if (textAreaFocused) {
            return super.startActionMode(callback, type);
        }
        return startActionMode(callback);
    }

    @Override
    public ActionMode startActionMode(ActionMode.Callback callback) {
   //     contextMenuCallback.onSelectionStarted(LastTouchX, LastTouchY);
   //     contextMenuShown = true;
    //    return this.emptyActionMode();
        return null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            LastTouchY = event.getY();
            LastTouchX = event.getX();
        }
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    public void setContextMenuCallback(ContextMenuCallback contextMenuCallback){
        this.contextMenuCallback = contextMenuCallback;
    }

    public void setHighlightsCommentsCallback(HighlightsCommentsCallback highlightsCommentsCallback){
        this.highlightsCommentsCallback = highlightsCommentsCallback;
    }



    /*public void updateReadingDisplayOptions(){
        if (this.ssReadingDisplayOptions != null) {
            ssReadViewBridge.setTheme(ssReadingDisplayOptions.theme);
            ssReadViewBridge.setFont(ssReadingDisplayOptions.font);
            ssReadViewBridge.setSize(ssReadingDisplayOptions.size);
        }
    }

    public void updateHighlights(){
        if (ssReadHighlights != null){
            ssReadViewBridge.setHighlights(ssReadHighlights.highlights);
        }
    }*/

    public void selectionFinished(){
        LogUtils.d("READVIEW","Selection made");
        //contextMenuCallback.onSelectionFinished();
        //contextMenuShown = false;
    }

    public static String readFileFromFiles(String path){
        File file = new File(path);
        try {
            int length = (int) file.length();

            byte[] bytes = new byte[length];

            FileInputStream in = new FileInputStream(file);
            try {
                in.read(bytes);
            } finally {
                in.close();
            }

            return new String(bytes);
        } catch (Exception e){ return ""; }
    }


    public static String readFileFromAssets(Context context, String assetPath){
        StringBuilder buf = new StringBuilder();
        try {
            InputStream json = context.getAssets().open(assetPath);
            BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;
            while ((str = in.readLine()) != null) {
                buf.append(str);
            }
            in.close();
            return buf.toString();

        } catch (IOException e){ return "";  }
    }

    public void loadRead(String readContent){
        // We don't want any flickering of themes, right?
        String baseUrl = "file:///android_asset/reader/";

        if (ssReaderContent == null){
            final File indexFile = new File(getContext().getFilesDir() + "/index.html");

            if (false && indexFile.exists()){
                baseUrl = "file:///" + getContext().getFilesDir() + "/";
                ssReaderContent = readFileFromFiles(getContext().getFilesDir() + "/index.html");
            } else {
                ssReaderContent = readFileFromAssets(getContext(), "reader/index.html");
            }
        }

        String content = ssReaderContent.replaceAll("\\{\\{content\\}\\}", readContent);

      //  content = content.replace("ss-wrapper-light", "ss-wrapper-" + ssReadingDisplayOptions.theme);
      //  content = content.replace("ss-wrapper-andada", "ss-wrapper-" + ssReadingDisplayOptions.font);
      //  content = content.replace("ss-wrapper-medium", "ss-wrapper-" + ssReadingDisplayOptions.size);

        loadDataWithBaseURL(baseUrl, content, "text/html", "utf-8", null);
    }

    public ActionMode emptyActionMode() {
        return new ActionMode() {
            @Override public void setTitle(CharSequence title) {}
            @Override public void setTitle(int resId) {}
            @Override public void setSubtitle(CharSequence subtitle) {}
            @Override public void setSubtitle(int resId) {}
            @Override public void setCustomView(View view) {}
            @Override public void invalidate() {}
            @Override public void finish() {}
            @Override public Menu getMenu() { return null; }
            @Override public CharSequence getTitle() { return null; }
            @Override public CharSequence getSubtitle() { return null; }
            @Override public View getCustomView() { return null; }
            @Override public MenuInflater getMenuInflater() { return null; }
        };
    }

    public interface ContextMenuCallback {
        public void onSelectionStarted(float x, float y);
        public void onSelectionStarted(float x, float y, int highlightId);
        public void onSelectionFinished();

    }

    public interface HighlightsCommentsCallback {
      //  public void onHighlightsReceived(SSReadHighlights ssReadHighlights);
      //  public void onCommentsReceived(SSReadComments ssReadComments);
        public void onVerseClicked(String verse);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public void onLongPress(MotionEvent event) {
            if (contextMenuShown) contextMenuCallback.onSelectionStarted(LastTouchX, LastTouchY);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            selectionFinished();
            return true;
        }
    }

    private class SSWebViewClient extends WebViewClient {

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            new FinestWebView.Builder(context).show(url);
            return true;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            new FinestWebView.Builder(context).show(request.getUrl().toString());

            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

          /*  updateReadingDisplayOptions();
            updateHighlights();
            updateComments();*/
        }
    }

    public class ReadViewBridge {
        Context context;
        ReadViewBridge(Context c) {
            context = c;
        }

        public void highlightSelection(final String color, final int highlightId){
            ((ReadActivity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (highlightId>0) {
                        loadUrl(String.format("javascript:if(typeof ssReader !== \"undefined\"){ssReader.highlightSelection('%s', %d);}", color, highlightId));
                    } else {
                        loadUrl(String.format("javascript:if(typeof ssReader !== \"undefined\"){ssReader.highlightSelection('%s');}", color));
                    }
                }
            });
        }

        public void unHighlightSelection(final int highlightId){
            ((ReadActivity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (highlightId>0) {
                        loadUrl(String.format("javascript:if(typeof ssReader !== \"undefined\"){ssReader.unHighlightSelection(%d);}", highlightId));
                    } else {
                        loadUrl("javascript:if(typeof ssReader !== \"undefined\"){ssReader.unHighlightSelection();}");
                    }
                }
            });
        }

        public void setFont(final String font){
            ((ReadActivity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadUrl(String.format("javascript:if(typeof ssReader !== \"undefined\"){ssReader.setFont('%s');}", font));
                }
            });
        }

        public void setSize(final String size){
            ((ReadActivity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadUrl(String.format("javascript:if(typeof ssReader !== \"undefined\"){ssReader.setSize('%s');}", size));
                }
            });
        }

        public void setTheme(final String theme){
            ((ReadActivity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadUrl(String.format("javascript:if(typeof ssReader !== \"undefined\"){ssReader.setTheme('%s');}", theme));
                }
            });
        }

        public void setHighlights(final String highlights){
            ((ReadActivity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadUrl(String.format("javascript:if(typeof ssReader !== \"undefined\"){ssReader.setHighlights('%s');}", highlights));
                }
            });
        }

        public void copy(){
            ((ReadActivity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadUrl("javascript:if(typeof ssReader !== \"undefined\"){ssReader.copy();}");
                }
            });
        }

        public void paste(){
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = clipboard.getPrimaryClip();
            if (clip == null || clip.getItemCount() == 0) {
                return;
            }

            final String buffer = (String) clip.getItemAt(0).coerceToText(context);

            ((ReadActivity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadUrl(String.format("javascript:if(typeof ssReader !== \"undefined\"){ssReader.paste('%s');}", Base64.encodeToString(buffer.getBytes(), Base64.NO_WRAP)));
                }
            });
        }

        /**
         * Receiving serizlied ssReadHighlights from webapp
         * @param serializedHighlights
         */
        @JavascriptInterface
        public void onReceiveHighlights(String serializedHighlights){
            try {
         //       ssReadHighlights.highlights = serializedHighlights;
          //      highlightsCommentsCallback.onHighlightsReceived(ssReadHighlights);
            } catch (Exception e){}
        }

        @JavascriptInterface
        public void onVerseClick(String verse){
            try {
                String _verse = new String(Base64.decode(verse, Base64.DEFAULT), "UTF-8");
                highlightsCommentsCallback.onVerseClicked(_verse);
            } catch (Exception e){}
        }

        @JavascriptInterface
        public void onCommentsClick(String comments, final String inputId){

            /*try {
                String commentReceived = new String(Base64.decode(comments, Base64.DEFAULT), "UTF-8");

                boolean found = false;
                for (SSComment comment : ssReadComments.comments) {
                    if (comment.elementId.equalsIgnoreCase(inputId)) {
                        comment.comment = commentReceived;
                        found = true;
                    }
                }
                if (!found) {
                    ssReadComments.comments.add(new SSComment(inputId, commentReceived));
                }
                highlightsCommentsCallback.onCommentsReceived(ssReadComments);

            } catch (Exception e){}*/
        }

        @JavascriptInterface
        public void onHighlightClicked(final int highlightId){
            try {
                ((ReadActivity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, String.valueOf(highlightId));
                        contextMenuCallback.onSelectionStarted(LastTouchX, LastTouchY, highlightId);
                    }
                });

            } catch (Exception e){
                Log.d(TAG, e.getMessage());
            }
        }

        @JavascriptInterface
        public void onCopy(String selection){
            try {
                ClipboardManager _clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(ReadView.CLIPBOARD_LABEL, selection);
                _clipboard.setPrimaryClip(clip);
        //        Toast.makeText(context, context.getString(R.string.ss_reading_copied), Toast.LENGTH_LONG).show();
            } catch (Exception e){}
        }

        @JavascriptInterface
        public void onSearch(String selection){
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(ReadView.SEARCH_PROVIDER, selection)));
                context.startActivity(intent);
            } catch (Exception e){}
        }

        @JavascriptInterface
        public void onShare(String selection){
            try {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, selection);
                sendIntent.setType("text/plain");
             //   context.startActivity(Intent.createChooser(sendIntent, context.getString(R.string.ss_reading_share_to)));
            } catch (Exception e){}
        }

        @JavascriptInterface
        public void focusin(){
            textAreaFocused = true;
        }

        @JavascriptInterface
        public void focusout(){
            textAreaFocused = false;
        }
    }
}
