package com.sww.analyze.readExcel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * @author ShaoWenWen
 * @date 2019-10-30
 */
public class WriteXml {

    public static void main(String[] args) {
        WriteXml writeXml = new WriteXml();
        File file = new File("/Users/shaowenwen/workFile/res/strings.xml");
        writeXml.writeToXml(file);

    }

    public void writeToXml(File file) {
        try {
            // 得到创建 DOM 解析器的工厂
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            // 得到 DOM 解析器对象
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            // 把要解析的 XML 文档转化为输入流，以便 DOM 解析器解析它
//            InputStream is = new FileInputStream(file);
            // DOM 解析器对象的 parse() 方法解析 XML 文档，得到代表整个文档的 Document 对象
            Document doc = documentBuilder.parse(file);
            //得到 XML 文档的根节点
            Element rootElement = doc.getDocumentElement();

            //创建node对象，并添加子节点
            Node node=doc.createElement("string");
//            Node child = node.appendChild(doc.createElement("name"));
//            child.setTextContent("wenwen");
//            Node child2 = node.appendChild(doc.createElement("age"));
//            child2.setTextContent("8");
            ((Element) node).setAttribute("name","name1");
            node.setTextContent("哈哈1");

            //追加子节点
            rootElement.appendChild(node);

            //往文件中添加
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult streamResult = new StreamResult(file);
            transformer.transform(source, streamResult);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
