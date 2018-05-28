package com.danyl.learnjava.test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017-2-14.
 */
public class Crawler {
    private static Pattern pattern = Pattern.compile("(\\d{4}-\\d{2}-\\d{2}) (\\d+) (\\d) (\\d) (\\d) (\\d) (\\d)");
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static Connection connection = null;

    public static void main(String[] args) throws Exception {
        String startDate = "2017-03-07";
        String endDate = "2017-03-11";

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(dateFormat.parse(startDate));

        initConnect();

        while (!endDate.equals(dateFormat.format(calendar.getTime()))) {
            String urlDateStr = dateFormat.format(calendar.getTime());
            String url = "http://baidu.lecai.com/lottery/draw/list/200?d=" + urlDateStr;
            Document html = Jsoup.connect(url).timeout(10000).get();
            print(url);
            insertRecord(html);
            calendar.add(Calendar.DATE, 1);
        }
    }

    private static void print(String str) {
        System.out.println(str);
    }

    private static void initConnect() {
        try {
            String url = "jdbc:postgresql://45.62.106.169:5432/lottery";
            String user = "postgres";
            String password = "123";
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void insertRecord(Document html) throws Exception {
        PreparedStatement preState = null;
        try {
            preState = connection.prepareStatement("INSERT INTO record_ssc (issue,time,p1,p2,p3,p4,p5) VALUES (?,?,?,?,?,?,?) ON CONFLICT (issue) DO NOTHING;");
            int count = 0;
            for (Element a : html.getElementsByTag("tr")) {
                Matcher matcher = pattern.matcher(a.text());
                if (matcher.find()) {
                    count++;
                    Date timestamp = new Date(dateFormat.parse(matcher.group(1)).getTime());
                    BigDecimal issue = new BigDecimal(matcher.group(2));
                    int p1 = Integer.parseInt(matcher.group(3));
                    int p2 = Integer.parseInt(matcher.group(4));
                    int p3 = Integer.parseInt(matcher.group(5));
                    int p4 = Integer.parseInt(matcher.group(6));
                    int p5 = Integer.parseInt(matcher.group(7));
                    preState.setBigDecimal(1, issue);
                    preState.setDate(2, timestamp);
                    preState.setInt(3, p1);
                    preState.setInt(4, p2);
                    preState.setInt(5, p3);
                    preState.setInt(6, p4);
                    preState.setInt(7, p5);
                    preState.addBatch();
                }
            }
            if (count > 0) {
                print("count: " + count);
                preState.executeBatch();
            } else {
                print("warning: issue count is zero!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}