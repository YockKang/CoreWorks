package com.main.CoreWorks.TextParser;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.main.CoreWorks.database.KeywordDatabase;
import regexodus.*;

import javax.swing.plaf.basic.BasicBorders;


public class Sentence {
    public final Array<Text> text;

    public Sentence(String content, boolean highlightKeywords) {
        Array<Text> tempText = new Array<>();
        if (!highlightKeywords) {
            tempText.add(new Text(content, Color.WHITE));
        } else {
            Array<String> keywords = KeywordDatabase.getKeywords();
            String patternStr = "\\b(" + String.join("|", keywords) + ")\\b";
            Pattern pattern = Pattern.compile(patternStr, "i");
            Matcher matcher = new Matcher(pattern, content);
            int idx = 0;
            while (matcher.proceed()) {
                if (matcher.start() > idx) {
                    tempText.add(new Text(content.substring(idx, matcher.start()), Color.WHITE));
                }

                String keyword = matcher.group();

                Keyword template = KeywordDatabase.getKeyword(keyword);
                tempText.add( new Keyword(keyword, template.color, template.description) );

                idx = matcher.end();
            }

            if (idx < content.length()) {
                tempText.add(new Text(content.substring(idx), Color.WHITE));
            }
        }
        text = newlineFrag(tempText);
    }

    public Sentence(String content, Color baseColor, boolean highlightKeywords) {
        Array<Text> tempText = new Array<>();
        if (!highlightKeywords) {
            tempText.add(new Text(content, baseColor));
        } else {
            Array<String> keywords = KeywordDatabase.getKeywords();
            String patternStr = "\\b(" + String.join("|", keywords) + ")\\b";
            Pattern pattern = Pattern.compile(patternStr, "i");
            Matcher matcher = new Matcher(pattern, content);
            int idx = 0;
            while (matcher.proceed()) {
                if (matcher.start() > idx) {
                    tempText.add(new Text(content.substring(idx, matcher.start()), baseColor));
                }

                String keyword = matcher.group();

                Keyword template = KeywordDatabase.getKeyword(keyword);
                tempText.add( new Keyword( keyword, template.color, template.description) );

                idx = matcher.end();
            }

            if (idx < content.length()) {
                tempText.add(new Text(content.substring(idx), baseColor));
            }
        }
        text = newlineFrag(tempText);
    }

    public Sentence(String content) {
        text = new Array<>();
        text.add(new Text(content, Color.WHITE));
    }

    public Sentence() {
        text = new Array<>();
    }

    public Sentence(String content, Color baseColor) {
        text = new Array<>();
        text.add(new Text(content, baseColor));
    }

    public Sentence(String content, String description) {
        text = new Array<>();
        text.add(new Keyword(content, Color.WHITE, description));
    }

    public Sentence(String content, Color baseColor, String description) {
        text = new Array<>();
        text.add(new Keyword(content, baseColor, description));
    }

    private Array<Text> newlineFrag(Array<Text> textIn) {
        Array<Text> textOut = new Array<>();
        for (Text fragment : textIn) {
            if (fragment instanceof Keyword) {
                textOut.add(fragment);
            } else {
                String[] lines = fragment.text.split("\n", -1);
                for (int i = 0; i < lines.length; i++) {
                    String line = lines[i];
                    if (i < lines.length - 1) {
                        textOut.add(new Text(line, fragment.color, true));
                    } else {
                        textOut.add(new Text(line, fragment.color, false));
                    }
                }
            }
        }
        return textOut;
    }

    public void appendText(Text text) {
        this.text.add(text);
    }

    public Table toTable(Skin skin) {
        Array<Array<Text>> formattedText = new Array<>();
        formattedText.add(new Array<>());
        for (Text fragment : text) {
            formattedText.get(formattedText.size - 1).add(fragment);
            if (fragment.newline) {
                formattedText.add(new Array<>());
            }
        }
        Table table = new Table();
        for (Array<Text> line : formattedText) {
            Table row = new Table();
            for (Text txt : line) {
                Label displayText = new Label(txt.text, skin);
                displayText.setColor(txt.color);
                if (txt instanceof Keyword keyword) {
                    TextTooltip tooltip = new TextTooltip(keyword.description, skin);
                    displayText.addListener(tooltip);
                }
                row.add(displayText);
            }
            table.add(row).row();
        }
        return table;
    }

    public Table toVertTable(Skin skin) {
        Table table = new Table();
        for (Text fragment : text) {
            Label displayText = new Label(fragment.text, skin);
            displayText.setColor(fragment.color);
            if (fragment instanceof Keyword keyword) {
                TextTooltip tooltip = new TextTooltip(keyword.description, skin);
                displayText.addListener(tooltip);
            }
            table.add(displayText).row();
        }
        return table;
    }
}
