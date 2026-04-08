package org.telegram.messenger;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.TextStyleSpan;
import org.telegram.ui.Components.URLSpanReplacement;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlexEntitiesHelper {

    private static final String URL_REGEX = "https?://[^\\s\\)]+";
    private static final Pattern[] PATTERNS = new Pattern[] {
        Pattern.compile("^`{3}(.*?)[\\n\\r](.*?[\\n\\r]?)`{3}", Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile("^`{3}[\\n\\r]?(.*?)[\\n\\r]?`{3}", Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile("[`]{3}([^`]+)[`]{3}"),
        Pattern.compile("[`]([^`\\n]+)[`]"),
        Pattern.compile("[*]{2}([^*\\n]+)[*]{2}"),
        Pattern.compile("[_]{2}([^_\\n]+)[_]{2}"),
        Pattern.compile("[~]{2}([^~\\n]+)[~]{2}"),
        Pattern.compile("[|]{2}([^|\\n]+)[|]{2}"),
        Pattern.compile("\\[([^]]+?)]\\((" + URL_REGEX + ")\\)")
    };

    public static void parseMarkdown(CharSequence[] message, boolean allowStrike) {
        Spannable spannable = message[0] instanceof Spannable ? (Spannable) message[0] : Spannable.Factory.getInstance().newSpannable(message[0]);

        for (int i = 0; i < PATTERNS.length; i++) {
            if (!allowStrike && i == 6 || !FlexConfig.isMarkdownParseLinksEnabled() && i == 8) {
                continue;
            }

            Matcher matcher = PATTERNS[i].matcher(spannable);
            ArrayList<String> sources = new ArrayList<>();
            ArrayList<CharSequence> destinations = new ArrayList<>();

            find:
            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                int length = i < 3 ? 3 : i > 3 && i != 8 ? 2 : 1;
                TextStyleSpan[] textStyleSpans = spannable.getSpans(start, end, TextStyleSpan.class);

                for (TextStyleSpan textStyleSpan : textStyleSpans) {
                    if (!textStyleSpan.isMono()) {
                        continue;
                    }
                    int spanStart = spannable.getSpanStart(textStyleSpan);
                    int spanEnd = spannable.getSpanEnd(textStyleSpan);
                    if (spanStart < start + length || spanEnd > end - length) {
                        continue find;
                    }
                }

                SpannableStringBuilder destination = new SpannableStringBuilder(spannable.subSequence(matcher.start(i == 0 ? 2 : 1), matcher.end(i == 0 ? 2 : 1)));
                if (i < 8) {
                    TextStyleSpan.TextStyleRun run = new TextStyleSpan.TextStyleRun();
                    switch (i) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                            run.flags |= TextStyleSpan.FLAG_STYLE_MONO;
                            if (i != 3) {
                                run.start = start;
                                run.end = end;
                                run.urlEntity = new TLRPC.TL_messageEntityPre();
                                run.urlEntity.language = i == 0 ? matcher.group(1) : "";
                            }
                            break;
                        case 4:
                            run.flags |= TextStyleSpan.FLAG_STYLE_BOLD;
                            break;
                        case 5:
                            run.flags |= TextStyleSpan.FLAG_STYLE_ITALIC;
                            break;
                        case 6:
                            run.flags |= TextStyleSpan.FLAG_STYLE_STRIKE;
                            break;
                        case 7:
                            run.flags |= TextStyleSpan.FLAG_STYLE_SPOILER;
                            break;
                    }
                    MediaDataController.addStyleToText(new TextStyleSpan(run), 0, destination.length(), destination, true);
                } else {
                    destination.setSpan(new URLSpanReplacement(matcher.group(2)), 0, destination.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                sources.add(matcher.group(0));
                destinations.add(destination);
            }

            for (int j = 0; j < sources.size(); j++) {
                spannable = (Spannable) TextUtils.replace(spannable, new String[] {sources.get(j)}, new CharSequence[] {destinations.get(j)});
            }
        }

        message[0] = spannable;
    }
}
