package com.sww.analyze;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jxl.Workbook;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * (1)javax.xml.parsers 包中的DocumentBuilderFactory用于创建DOM模式的解析器对象 ， DocumentBuilderFactory是一个抽象工厂类，它不能直接实例化，
 * 但该类提供了一个newInstance方法 ，这个方法会根据本地平台默认安装的解析器，自动创建一个工厂的对象并返回。
 * (2)jxl表格输出
 * @author ShaoWenWen
 * @date 2019-10-29
 */
public class analyzeTool {

    private ArrayList<String> arrTitle;
    private String out_path = "/Users/shaowenwen/workFile/res/analyze.xls";
    private WritableWorkbook book = null;
    private boolean isValues = false;

    void analyze() {
        File[] resDirectoryFiles = null;
        String path = "/Users/shaowenwen/workFile/res/";
        File dire = new File(path);
        if (dire.isDirectory()) {
            resDirectoryFiles = dire.listFiles();
        }
        resetCategoriesOrder(resDirectoryFiles);
        arrTitle = new ArrayList<>();
        LinkedHashMap<String, ArrayList<String>> hashMap = new LinkedHashMap<>();
        for (int i = 0; i < resDirectoryFiles.length; i++) {
            //files为文件列表；
            if (resDirectoryFiles[i].getName().contains("values")) {
                if (resDirectoryFiles[i].getName().equals("values")) {
                    isValues = true;
                } else {
                    isValues = false;
                }
                arrTitle.add(resDirectoryFiles[i].getName());
                if (resDirectoryFiles[i].isDirectory()) {
                    File[] valueDirectoryFiles = (File[]) resDirectoryFiles[i].listFiles();
                    for (File file : valueDirectoryFiles) {// 基本都是一个strings.xml,可能会有demens,colors;
                        // 对单个string.xml文件进行操作
                        analyzeStringXmlFile(file, hashMap);
                    }
                    // 进行补全，因为string_xx.xml文件的个数会少于value目录下的string的个数，所以进行补全。
                    fillUpValue(hashMap);
                }
            }
        }
        outputExcel(hashMap, out_path);
    }

    private void resetCategoriesOrder(File[] files){
        for (int index = 0;index <files.length;index++) {
            if (files[index].getName().equals("values") && index != 0) {
                File file = files[index];
                files[index] = files[0];
                files[0] = file;
                break;
            }
        }
    }

    private void analyzeStringXmlFile(File file, LinkedHashMap<String, ArrayList<String>> hashMap) {
        if (file.getName().contains("string") && file.getName().contains(".xml")) {
            try {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = (Document) dBuilder.parse(file);
                doc.getDocumentElement().normalize();
                System.out.println("Root element:" + doc.getDocumentElement().getNodeName());
                NodeList nodeList = doc.getElementsByTagName("string");
                ArrayList<String> arrayList;
                for (int n = 0; n < nodeList.getLength(); n++) {
                    Node nNode = nodeList.item(n);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        String key = eElement.getAttribute("name");
                        String value = eElement.getTextContent();
                        //如果含有该Key；
                        if (hashMap.containsKey(key)) {
                            ArrayList<String> arrayListContainKey = hashMap.get(key);
                            arrayListContainKey.add(value);
                            hashMap.put(key, arrayListContainKey);
                        } else {//没有此key;
                            if (isValues) {
                                arrayList = new ArrayList<>();
                                for (int wen_i = 0; wen_i < arrTitle.size() - 1; wen_i++) {
                                    arrayList.add(null);
                                }
                                arrayList.add(value);
                                hashMap.put(key, arrayList);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void fillUpValue(HashMap<String, ArrayList<String>> hashMap) {
        for (String hashKey : hashMap.keySet()) {
            ArrayList<String> arrlist = hashMap.get(hashKey);
            if (arrlist.size() < arrTitle.size()) {
                arrlist.add(null);
            }
            hashMap.put(hashKey, arrlist);
        }
    }

    private void outputExcel(HashMap<String, ArrayList<String>> hashMap, String path) {
        try {
            book = Workbook.createWorkbook(new File(path));
            // 设置工作表名(某行的列，某行，数据)；
            WritableSheet sheet = book.createSheet("testForExcel", 0);
            sheet.addCell(new Label(0, 0, "KEY"));
            for (int title = 0; title < arrTitle.size(); title++) {
                sheet.addCell(new Label(title + 1, 0, arrTitle.get(title)));
            }
            // 定义开始输出的行数
            int row = 2;
            ArrayList<String> arrayList;
            Set<Map.Entry<String, ArrayList<String>>> entrySet = hashMap.entrySet();
            for (Map.Entry<String, ArrayList<String>> entry : hashMap.entrySet()) {
                int column = 0;
                String key = entry.getKey();
                arrayList = entry.getValue();
                sheet.addCell(new Label(column, row, key));
                for (int arr_i = 0; arr_i < arrayList.size(); arr_i++) {
                    WritableCellFormat wcf1 = new WritableCellFormat();// 单元格样式
                    wcf1.setBackground(Colour.ORANGE);
                    Label label;
                    if (arrayList.get(arr_i)!=null){
                         label = new Label(++column, row, arrayList.get(arr_i));
                    } else {
                         label = new Label(++column, row, arrayList.get(arr_i), wcf1);
                    }
                    sheet.addCell(label);
                }
                row++;
            }
            book.write();
            book.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("有异常,报错了");
        } finally {
            System.out.println("去打开你想要的文件吧");
        }
    }

}