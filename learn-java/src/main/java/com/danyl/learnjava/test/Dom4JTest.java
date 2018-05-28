package com.danyl.learnjava.test;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

/**
 * Created by Administrator on 2017-5-8.
 */
public class Dom4JTest {
    @Test
    public void Dom4jtest() throws Exception {
        File file = new File("D:/BaiduYunDownload/xmg/02-Java基础加强/contacts.xml");
        Document doc = new SAXReader().read(file);
        Element rootElement = doc.getRootElement();
        List<Element> linkManList = rootElement.elements("linkman");
        for (Element linkman : linkManList) {
            System.out.println(linkman.attributeValue("id"));
            System.out.println(linkman.element("name").getText());
            System.out.println(linkman.element("email").getText());
        }
        Element linkman = rootElement.addElement("linkman");
        linkman.addAttribute("id", "5");
        linkman.addElement("name").setText("张丹玉");
        linkman.addElement("email").setText("1475811550@qq.com");
        linkman.addElement("address").setText("天上");
        linkman.addElement("group").setText("逍遥派");
        OutputFormat prettyPrint = OutputFormat.createPrettyPrint();
        XMLWriter xmlWriter = new XMLWriter(new FileWriter(file), prettyPrint);
        xmlWriter.write(doc);
        xmlWriter.close();
    }
}
