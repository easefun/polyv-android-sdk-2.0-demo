package com.easefun.polyvsdk.player.srt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;


public class PolyvMathJaxView extends FrameLayout {
    private String inputText = null;
    private String inputTextColor = "#ffffff";
    private String inputBackgroundColor = colorIntToRgba(Color.TRANSPARENT);
    private int inputFontStyle = Typeface.NORMAL;
    private WebView mWebView;

    public PolyvMathJaxView(Context context) {
        super(context);
        init(context, null);
    }


    public PolyvMathJaxView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PolyvMathJaxView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void init(Context context, AttributeSet attrSet) {
        mWebView = new WebView(context);
        int gravity = Gravity.CENTER;
        boolean verticalScrollbarsEnabled = false;
        boolean horizontalScrollbarsEnabled = false;

        addView(mWebView, new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                gravity)
        );

        mWebView.setVerticalScrollBarEnabled(verticalScrollbarsEnabled);
        mWebView.setHorizontalScrollBarEnabled(horizontalScrollbarsEnabled);
        mWebView.setBackgroundColor(Color.TRANSPARENT);
        WebSettings mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setAppCacheEnabled(true);
        mWebSettings.setAllowContentAccess(true);
        mWebSettings.setSupportZoom(false);
        mWebSettings.setAllowUniversalAccessFromFileURLs(false);
        mWebSettings.setAllowFileAccessFromFileURLs(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebSettings.setMixedContentMode(WebSettings.LOAD_NORMAL);
        }
        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setSavePassword(false);
        mWebSettings.setSaveFormData(true);
        mWebSettings.setLoadsImagesAutomatically(true);
    }

    /**
     * @param inputText formatted string
     */
    public void setText(String inputText) {
        if (this.inputText != null && this.inputText.equals(inputText)) {
            return;
        }
        this.inputText = inputText;
        loadText();
    }

    public void setInputBackgroundColor(int color) {
        this.inputBackgroundColor = colorIntToRgba( color);
    }

    public void setTextColor(int color) {
        this.inputTextColor = colorIntToRgba(color);
    }

    public void setTypeface(int style) {
        this.inputFontStyle = style;
    }

    /**
     * @param text  传入公式
     * @param color  文本颜色
     */
    public void setText(String text, int color) {
        if (this.inputText != null && this.inputText.equals(text)) {
            return;
        }
        this.inputTextColor = colorIntToRgba(color);
        this.inputText = text;
        loadText();
    }

    public void loadText() {
        String laTexString;
        laTexString = inputText;
        loadLocal(getHtmlLocation(laTexString));
    }

    /**
     * 加载本地网页
     *
     * @param html
     */
    private void loadLocal(String html) {
        mWebView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "utf-8", null);

    }

    private String getHtmlLocation(String data) {
        String fontWeight = "normal";
        String fontStyle = "normal";
        if (inputFontStyle == Typeface.BOLD) {
            fontWeight = "bold";
        } else if (inputFontStyle == Typeface.ITALIC) {
            fontStyle = "italic";
        } else if (inputFontStyle == Typeface.BOLD_ITALIC) {
            fontWeight = "bold";
            fontStyle = "italic";
        }
        return "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <meta content=\"width=device-width,height=device-height,initial-scale=1.0,maximum-scale=1.0,user-scalable=no\" name=\"viewport\">\n" +
                "    <style type=\"text/css\">\n" +
                "      html, body { margin:0; padding:0; background:transparent; }\n" +
                "      mjx-container { outline:0; margin:0 !important; padding:0 !important; }\n" +
                "      #math { margin:0; padding:0; }\n" +
                "    </style>\n" +
                "     <script type=\"text/javascript\">\n" +
                "      window.MathJax = {\n" +
                "        tex: { inlineMath: [['$','$'], ['\\\\(','\\\\)']] },\n" +
                "        svg: { fontCache: 'global' },\n" +
                "        startup: {\n" +
                "          pageReady: () => {\n" +
                "            document.getElementById('math').style.visibility = 'visible';\n" +
                "            return MathJax.startup.defaultPageReady();\n" +
                "          }\n" +
                "        }\n" +
                "      };\n" +
                "     </script>\n" +
                "   \n" +
                "      <script type=\"text/javascript\" id=\"MathJax-script\" async  src=\"file:///android_asset/mathjax/tex-chtml.js\"></script>\n" +
                "\n" + "  <script type=\"text/javascript\">\n" +
                "\t           function changeLatexTextColor(color) {\n" +
                "\t                var element = document.getElementById('math')\n" +
                "\t                element.style.color =color\n" +
                "\t            }\n" +
                "\t  </script>" + "   <style type=\"text/css\">\n" +
                "\tmjx-container  {\n" +
                "\t  outline: 0;}\n" +
                "   </style>" +
                "</head>\n" +
                "<body>\n" +
                "<div style='text-align:center;'>\n" +
                "<div id='math' style=\"display: inline-block; height: auto; font-size: 15px; color:" + inputTextColor +
                "; font-weight:" + fontWeight + "; font-style:" + fontStyle + "; background-color:" + inputBackgroundColor + "; visibility: hidden;\">\n" +
                data +
                "\n" +
                "</div>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>\n";
    }

    public static String colorIntToRgba(int color) {
        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        return String.format("rgba(%d,%d,%d,%.2f)", r, g, b, a / 255.0f);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 拦截WebView触摸事件
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}
