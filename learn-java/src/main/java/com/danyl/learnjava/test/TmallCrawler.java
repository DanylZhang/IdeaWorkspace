package com.danyl.learnjava.test;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017-4-19.
 */
public class TmallCrawler {
    private static WebDriver driver = null;
    private static Connection connection = null;
    private static Pattern pattern = Pattern.compile("https://list\\.tmall\\.com/search_product\\.htm\\?.*?cat=(\\d+).*");
    private static Pattern cidPattern = Pattern.compile("(window\\.g_config\\.cat_rt='(\\d+)';)|(data-atp=\"a!,*?(\\d+),*?\">)");
    private static Map<String, String> initMap = new LinkedHashMap<>();
    private static Queue<Map<String, String>> catQueue = new LinkedList<>();
    private static Map<String, String> cookies = new HashMap<>();

    public static void main(String[] args) throws Exception {
        String str = "cna=XhF+EdxT+zcCAXTiHGydCEYG; l=Am5uuGT7CFp1ro9LgKLSnJYWvs8wIzJ9; isg=At7eZZ5yCvSuLl4amwmLSqSjLnTN7KIZLRUe-IhnBiEIq3-F8C4tKf5J1Q1Q; _med=dw:1680&dh:1050&pw:1680&ph:1050&ist:0; pnm_cku822=094UW5TcyMNYQwiAiwTR3tCf0J%2FQnhEcUpkMmQ%3D%7CUm5Ockt%2FQXxDeUF6RX9AeC4%3D%7CU2xMHDJ%2BH2QJZwBxX39RaVZ4WHYqSy1BJlgiDFoM%7CVGhXd1llXGhWa1RuVm1SaFdvWGVHe0J2SXdKf0RxT3NOdUtwTXNdCw%3D%3D%7CVWldfS0QMAs3CioWLAwiXyVMKGFKalNzT2o8GHMPKhwyZDI%3D%7CVmhIGCwWNgsrFywXKQkxBTwAIBwnEi8PMw42CysXLBkkBDgFOQRSBA%3D%3D%7CV25Tbk5zU2xMcEl1VWtTaUlwJg%3D%3D; cq=ccp%3D0; tk_trace=1; t=99d407868e43c4c9835d32dc216af4f4; cookie2=1cc669bb1c51e20f493a71667794bde4; _tb_token_=Jio38Zz2JWyV; res=scroll%3A1663*5941-client%3A1663*589-offset%3A1663*5941-screen%3A1680*1050; uc3=nk2=F5RHoWhiohbwZxQoL70%3D&id2=VAKO5%2BrCKRp6&vt3=F8dARVKwHtJ91%2BXPhxw%3D&lg2=WqG3DMC9VAQiUQ%3D%3D; lgc=tb2098796_2011; tracknick=tb2098796_2011; skt=755d8523c39f05c8; otherx=e%3D1%26p%3D*%26s%3D0%26c%3D0%26f%3D0%26g%3D0%26t%3D0; swfstore=233792; whl=-1%260%260%260; x=__ll%3D-1%26_ato%3D0; ck1=; hng=; uss=; tt=login.tmall.com; uc1=cookie15=VFC%2FuZ9ayeYq2g%3D%3D&existShop=false; cookie1=BxeEC%2FHMJsI9bQLxl%2Fz%2B7ooRHQStim01YA%2FnS6NYUQs%3D; unb=733757119; _l_g_=Ug%3D%3D; _nk_=tb2098796_2011; cookie17=VAKO5%2BrCKRp6; login=true";
        for (String st : str.split(";")) {
            String[] s = st.split("=");
            if (s.length > 1) {
                cookies.put(st.split("=")[0], st.split("=")[1]);
            } else {
                cookies.put(st.split("=")[0], "");
            }
        }

        initConnection();
        initWebDriver();
        //doItemCategory();

        Thread doPropsThread = new Thread(new Runnable() {
            @Override
            public void run() {
                doProps();
            }
        });
        Thread mapCidBackendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                mapCidBackend();
            }
        });

        //doPropsThread.start();
        mapCidBackendThread.start();

    }

    private static void initWebDriver() {
        System.setProperty("phantomjs.binary.path", "D:/360Downloads/phantomjs-2.1.1-windows/bin/phantomjs.exe");
        driver = new PhantomJSDriver();
    }

    private static void doItemCategory() throws Exception {
        //start from "https://3c.tmall.com"
        Document html = getDoc("https://3c.tmall.com");
        Elements a_arr = html.select("#J_fs_nav ul li a");
        for (Element a : a_arr) {
            initMap.put(URLDecoder.decode(a.absUrl("href"), "gbk"), a.text());
        }

        //iterate initMap to get level one category
        for (Map.Entry<String, String> entry : initMap.entrySet()) {
            String url = entry.getKey();
            String name = entry.getValue();
            html = getDoc(url);
            if (html.select("#J_CrumbSlideCon > li:eq(1) > a").size() > 0) {
                Element a = html.select("#J_CrumbSlideCon > li:eq(1) > a").first();
                print("JJJJJJ", a.absUrl("href"), a.text());
                catQueue.add(createItemCategoryMap(getCid(a.absUrl("href")), "0", a.text().trim(), "0", "1"));
            } else if (isParentCid(html)) {
                for (Element a : html.select("div.j_Cate ul a[title]")) {
                    print("DDDDDD", a.absUrl("href"), a.text());
                    catQueue.add(createItemCategoryMap(getCid(a.absUrl("href")), "0", a.text().trim(), "0", "1"));
                }
            } else {
                throw new Exception("DDD 未知错误" + url);
            }
        }

        while (!catQueue.isEmpty()) {
            Map<String, String> map = catQueue.remove();
            String cat = map.get("cid");
            String url = "https://list.tmall.com/search_product.htm?cat=" + cat;
            html = getDoc(url);

            if (isParentCid(html)) {
                //设置为isParent
                map.put("isParent", "1");
                //将子cid及时添加进队列
                for (Element a : html.select("div.j_Cate ul a[title]")) {
                    print("DDDDDD", a.absUrl("href"), a.text());
                    catQueue.add(createItemCategoryMap(getCid(a.absUrl("href")), cat, a.text().trim(), "0", (Integer.parseInt(map.get("level")) + 1) + ""));
                }
            }
            insertItemCategory(map);
        }
    }

    private static void doProps() {
        try {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT cid FROM prometheus.item_category WHERE is_parent=0;");
            while (resultSet.next()) {
                String cid = "" + resultSet.getInt("cid");
                String url = "https://list.tmall.com/search_product.htm?cat=" + cid;
                print(url);
                Document html = getDoc(url);
                for (Element propsName : html.select("div.j_Prop.attr")) {
                    //先插入props表
                    print(propsName.select("div.attrKey").first().text().trim());
                    Map<String, String> map = new HashMap<>();
                    map.put("cid", cid);
                    map.put("propName", propsName.select("div.attrKey").first().text().trim());
                    insertProps(map);
                    //再插入props_values表
                    for (Element propsValues : propsName.select("div.attrValues > ul > li > a")) {
                        print(propsValues.text().trim());
                        map.put("valueName", propsValues.text().trim());
                        insertPropsValues(map);
                    }
                }
            }
        } catch (Exception e) {
            print(e.getMessage());
        }
    }

    private static void mapCidBackend() {
        try {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT cid FROM prometheus.item_category WHERE is_parent=0 AND root_cid=0 AND cid NOT IN (50069203,50069217,50071180);");
            while (resultSet.next()) {
                Thread.sleep(1000 * (int) (Math.random() * 10));
                String cid = "" + resultSet.getInt("cid");
                String url = "https://list.tmall.com/search_product.htm?spm=a220m.1000858.0.0.tHE91B&sort=s&style=g&search_condition=23&from=sn_1_rightnav&active=1&cat=" + cid;
                print(url);

                //String html=getDoc(url).html();
                driver.get(url);
                if (driver.getCurrentUrl().contains("err") || driver.getCurrentUrl().contains("login")) {
                    print(new Date().toString(), "重新登录！！！", driver.getCurrentUrl());
                    driver.manage().window().maximize();
                    Thread.sleep(1000 * 3);
                    File screenShot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                    FileUtils.copyFile(screenShot, new File("E:/image.png"));
                    Desktop.getDesktop().open(new File("E:/image.png"));
                    Thread.sleep(1000 * 15);
                    driver.navigate().forward();
                } else if (driver.getCurrentUrl().contains("sec.taobao.com/query.")) {
                    print(new Date().toString(), "要输入验证码！！！", driver.getCurrentUrl());
                    driver.manage().window().maximize();
                    Thread.sleep(1000 * 3);
                    File screenShot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                    FileUtils.copyFile(screenShot, new File("E:/image.png"));
                    org.openqa.selenium.Rectangle rectangle = driver.findElement(By.id("checkcodeImg")).getRect();
                    BufferedImage bufferedImage = ImageIO.read(screenShot).getSubimage(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
                    ImageIO.write(bufferedImage, "png", new File("E:/image.png"));
                    Desktop.getDesktop().open(new File("E:/image.png"));
                    Scanner scanner = new Scanner(System.in);
                    driver.findElement(By.id("checkcodeInput")).sendKeys(scanner.nextLine());
                    driver.findElement(By.cssSelector("div.submit > input[type='submit']")).submit();
                    driver.navigate().forward();
                }

                String html = driver.getPageSource();
                Matcher matcher = cidPattern.matcher(html);
                if (matcher.find()) {
                    String rootCid = matcher.group(2) == null ? matcher.group(4) : matcher.group(2);
                    print("UPDATE prometheus.item_category SET root_cid=" + rootCid + " WHERE cid=" + cid);
                    connection.createStatement().executeUpdate("UPDATE prometheus.item_category SET root_cid=" + rootCid + " WHERE cid=" + cid);
                }
            }
        } catch (Exception e) {
            print(e.getMessage());
        }
    }

    private static void print(Object... str) {
        for (Object s : str) {
            System.out.printf("%s\t|\t", s);
        }
        System.out.println();
    }

    private static Document getDoc(String url) throws Exception {
        org.jsoup.Connection.Response response = Jsoup.connect(url).timeout(600000)
                .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("accept-encoding", "gzip, deflate, br")
                .header("accept-language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
                .header("Connection", "keep-alive")
                .header("cache-control", "no-cache")
                .header("upgrade-insecure-requests", "1")
                .header("Referer", url)
                .header("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:52.0) Gecko/20100101 Firefox/52.0")
                .method(org.jsoup.Connection.Method.GET)
                .cookies(cookies)
                .execute();
        cookies.putAll(response.cookies());
        Document html = response.parse();
        if (html.location().contains("login") || html.location().contains("err")) {
            print(new Date().toString(), "重新登录！！！", html.location());
            System.exit(0);
        }
        return html;
    }

    private static boolean isParentCid(Document html) {
        return html.select("div.j_Cate ul a[title]").size() > 0;
    }

    private static Map<String, String> createItemCategoryMap(String cid, String parentCid, String name, String isParent, String level) {
        Map<String, String> itemCategoryMap = new LinkedHashMap<String, String>();
        itemCategoryMap.put("cid", cid);
        itemCategoryMap.put("parentCid", parentCid);
        itemCategoryMap.put("name", name);
        itemCategoryMap.put("isParent", isParent);
        itemCategoryMap.put("level", level);
        return itemCategoryMap;
    }

    private static String getCid(String url) throws Exception {
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            throw new Exception("没有找到cat");
        }
    }

    private static void initConnection() {
        try {
            String url = "jdbc:mysql://139.196.96.149:13306/prometheus?useUnicode=true&characterEncoding=UTF-8";
            String user = "dataway-rw";
            String password = "QqHVMhmN*8";
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void insertItemCategory(Map<String, String> map) throws Exception {
        PreparedStatement preState = null;
        try {
            preState = connection.prepareStatement("INSERT INTO prometheus.item_category (cid,parent_cid,name,is_parent,level) VALUES (?,?,?,?,?) ON DUPLICATE KEY UPDATE parent_cid=values(parent_cid),name=values(name),is_parent=values(is_parent),level=values(level);");
            preState.setInt(1, Integer.parseInt(map.get("cid")));
            preState.setInt(2, Integer.parseInt(map.get("parentCid")));
            preState.setString(3, map.get("name"));
            preState.setInt(4, Integer.parseInt(map.get("isParent")));
            preState.setInt(5, Integer.parseInt(map.get("level")));

            preState.addBatch();
            preState.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
            print(map.toString());
        }
    }

    private static void insertProps(Map<String, String> map) throws Exception {
        PreparedStatement preState = null;
        try {
            preState = connection.prepareStatement("INSERT IGNORE INTO prometheus.item_category_props (cid,prop_name) VALUES (?,?);");
            preState.setInt(1, Integer.parseInt(map.get("cid")));
            preState.setString(2, map.get("propName"));

            preState.addBatch();
            preState.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void insertPropsValues(Map<String, String> map) throws Exception {
        PreparedStatement preState = null;
        try {
            preState = connection.prepareStatement("INSERT IGNORE INTO prometheus.item_category_props_values (cid,prop_name,value_name) VALUES (?,?,?);");
            preState.setInt(1, Integer.parseInt(map.get("cid")));
            preState.setString(2, map.get("propName"));
            preState.setString(3, map.get("valueName"));

            preState.addBatch();
            preState.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
