package net.jlxxw.client.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * html 转 txt
 * @author chunyang.leng
 * @date 2020/12/16 9:40 上午
 */
public class HtmlToText {
    private static final Logger logger = LoggerFactory.getLogger(HtmlToText.class);

    /**
     * 过滤器
     */
    private static final List<Function<String, String>> PATTERN_FUNCTION = new ArrayList<>();

    static {
        /**
         * script脚本过滤
         */
        Function<String, String> scriptFunction = (input) -> {
            // 定义script的正则表达式{或<script[^>]*?>[//s//S]*?<///script>
            String regEx = "<[//s]*?script[^>]*?>[//s//S]*?<[//s]*?///[//s]*?script[//s]*?>";
            Pattern patternScript = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
            Matcher matcherScript = patternScript.matcher(input);
            input = matcherScript.replaceAll(""); // 过滤script标签
            return input;
        };
        PATTERN_FUNCTION.add(scriptFunction);

        /**
         * style标签过滤
         */
        Function<String, String> styleFunction = (input) -> {
            String regEx = "<[//s]*?style[^>]*?>[//s//S]*?<[//s]*?///[//s]*?style[//s]*?>"; // 定义style的正则表达式{或<style[^>]*?>[//s//S]*?<///style>
            Pattern patternStyle = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
            Matcher matcherStyle = patternStyle.matcher(input);
            input = matcherStyle.replaceAll(""); // 过滤style标签
            return input;
        };
        PATTERN_FUNCTION.add(styleFunction);

        /**
         * html过滤
         */
        Function<String, String> htmlFunction = (input) -> {
            String regEx = "<[^>]+>"; // 定义HTML标签的正则表达式
            Pattern pHtml = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
            Matcher mHtml = pHtml.matcher(input);
            input = mHtml.replaceAll(""); // 过滤html标签
            return input;
        };
        PATTERN_FUNCTION.add(htmlFunction);

        /**
         * html过滤
         */
        Function<String, String> htmlFunction2 = (input) -> {
            String regEx = "<[^>]+";
            Pattern pHtml = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
            Matcher mHtml = pHtml.matcher(input);
            input = mHtml.replaceAll(""); // 过滤html标签
            return input;
        };
        PATTERN_FUNCTION.add(htmlFunction2);
    }

    /**
     * html文本转字符串
     *
     * @param html  带有html标签的文本
     * @return 去掉html的内容
     */
    public static String toText(String html) {
        try {
            for (Function<String, String> function : PATTERN_FUNCTION) {
                html = function.apply(html);
            }
            return html.replaceAll("\\s*","");
        } catch (Exception e) {
            logger.error("html转text出现异常，输入参数:{},异常：{}", html, e.getMessage());
            return html;
        }
    }
}
