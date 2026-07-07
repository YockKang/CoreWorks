package com.main.CoreWorks.TextParser;


import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.main.CoreWorks.JsonProcessor;
import com.main.CoreWorks.database.KeywordDatabase;
import org.apache.fory.util.StringEncodingUtils;
import regexodus.*;

import java.security.Key;
import java.util.Locale;


public class Sentence {
    public final Array<Text> text;

    public Sentence(String content, boolean highlightKeywords) {
        text = new Array<>();
        if (!highlightKeywords) {
            text.add(new Text(content, Color.CLEAR_WHITE));
        } else {
            Array<String> keywords = KeywordDatabase.getKeywords();
            String patternStr = "\\b(" + String.join("|", keywords) + ")\\b";
            Pattern pattern = Pattern.compile(patternStr, "i");
            Matcher matcher = new Matcher(pattern, content);
            int idx = 0;
            while (matcher.proceed()) {
                if (matcher.start() > idx) {
                    text.add(new Text(content.substring(idx, matcher.start()), Color.CLEAR_WHITE));
                }

                String keyword = matcher.group();

                Keyword template = KeywordDatabase.getKeyword(keyword);
                text.add( new Keyword( keyword, template.color, template.description) );

                idx = matcher.end();
            }

            if (idx < content.length()) {
                text.add(new Text(content.substring(idx), Color.CLEAR_WHITE));
            }
        }
    }

    public Sentence(String content, Color baseColor, boolean highlightKeywords) {
        text = new Array<>();
        if (!highlightKeywords) {
            text.add(new Text(content, baseColor));
        } else {
            Array<String> keywords = KeywordDatabase.getKeywords();
            String patternStr = "\\b(" + String.join("|", keywords) + ")\\b";
            Pattern pattern = Pattern.compile(patternStr, "i");
            Matcher matcher = new Matcher(pattern, content);
            int idx = 0;
            while (matcher.proceed()) {
                if (matcher.start() > idx) {
                    text.add(new Text(content.substring(idx, matcher.start()), baseColor));
                }

                String keyword = matcher.group();

                Keyword template = KeywordDatabase.getKeyword(keyword);
                text.add( new Keyword( keyword, template.color, template.description) );

                idx = matcher.end();
            }

            if (idx < content.length()) {
                text.add(new Text(content.substring(idx), baseColor));
            }
        }
    }


    public Sentence(String content) {
        text = new Array<>();
        text.add(new Text(content, Color.CLEAR_WHITE));
    }

    public Sentence(String content, Color baseColor) {
        text = new Array<>();
        text.add(new Text(content, baseColor));
    }

    public Sentence(String content, String description) {
        text = new Array<>();
        text.add(new Keyword(content, Color.CLEAR_WHITE, description));
    }

    public Sentence(String content, Color baseColor, String description) {
        text = new Array<>();
        text.add(new Keyword(content, baseColor, description));
    }

    public void appendText(Text text) {
        this.text.add(text);
    }


}
