package com.sww.analyze;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jxl.Workbook;
import jxl.demo.Write;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * (1)javax.xml.parsers 包中的DocumentBuilderFactory用于创建DOM模式的解析器对象 ， DocumentBuilderFactory是一个抽象工厂类，它不能直接实例化，
 * 但该类提供了一个newInstance方法 ，这个方法会根据本地平台默认安装的解析器，自动创建一个工厂的对象并返回。
 * (2)jxl表格输出
 *
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
                        System.out.println(" -- " + file.getAbsolutePath());
                        System.out.println("");
                        analyzeStringXmlFile(file, hashMap);
                    }
                    // 进行补全，因为string_xx.xml文件的个数会少于value目录下的string的个数，所以进行补全。
                    fillUpValue(hashMap);
                }
            }
        }
        outputExcel(hashMap, out_path);
    }

    private void resetCategoriesOrder(File[] files) {
        for (int index = 0; index < files.length; index++) {
            if (files[index].getName().equals("values") && index != 0) {
                File file = files[index];
                files[index] = files[0];
                files[0] = file;
                break;
            }
        }
    }

    public void analyzeStringXmlFile(File file, LinkedHashMap<String, ArrayList<String>> hashMap) {
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

    public LinkedHashMap<String, Model> analyzeStringXmlFiless(File file, LinkedHashMap<String, Model> hashMap) {
        if (file.getName().contains("string") && file.getName().contains(".xml")) {
            try {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = (Document) dBuilder.parse(file);
                doc.getDocumentElement().normalize();
                System.out.println("Root element: -- " + doc.getDocumentElement().getNodeName());
                NodeList nodeList = doc.getElementsByTagName("mtb_base_layout");
                ArrayList<String> arrayList;
                for (int n = 0; n < nodeList.getLength(); n++) {
                    Node nNode = nodeList.item(n);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        String positionId = getValue(eElement, "position");
                        Model model = new Model(positionId,
                                getValue(eElement, "ad_config_id"),
                                getValue(eElement, "animator"),
                                getValue(eElement, "page_id"),
                                getValue(eElement, "is_main_ad"),
                                getValue(eElement, "is_reward_ad"),
                                getValue(eElement, "is_full_screen_ad"),
                                getValue(eElement, "is_full_interstitial_ad")
                        );
                        hashMap.put(positionId, model);

                        NodeList dspNodeList = eElement.getElementsByTagName("dsp");
                        for (int i = 0; i < dspNodeList.getLength(); i++) {
                            Node dspNode = dspNodeList.item(i);
                            if (dspNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element dspElement = (Element) dspNode;
//                                System.out.println(getValue(dspElement, "name"));
                                if (isDfp(dspElement)) {
                                    putDFPNodeList(model.dspLinkedHashMap, dspElement);
                                } else {
                                    Model.DspNode dspNode1 = getDspNode(dspElement);
                                    model.dspLinkedHashMap.put(getValue(dspElement, "name"), dspNode1);
                                }
                            }
                        }

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return hashMap;
    }

    public boolean isDfp(Element dspElement) {
        String path = dspElement.getAttribute("name");
        if (path != null && path.contains("dfp")) {
            return true;
        }
        return false;
    }

    public void putDFPNodeList(LinkedHashMap<String, Model.DspNode> dspLinkedHashMap, Element dspElement) {
        Model.DspNode dspNode = null;
        String path = dspElement.getAttribute("name");

        String ui_type = getContentValue(dspElement, "ui_type");
        String dfp_app_id = getContentValue(dspElement, "dfp_app_id");
        String dfp_mo_unit_id = getContentValue(dspElement, "dfp_mo_unit_id");
        String dfp_tw_unit_id = getContentValue(dspElement, "dfp_tw_unit_id");
        String dfp_hk_unit_id = getContentValue(dspElement, "dfp_hk_unit_id");
        String dfp_hw_unit_id = getContentValue(dspElement, "dfp_hw_unit_id");
        String adSourceTag = null;
        if (!isEmpty(dfp_mo_unit_id)) {
            adSourceTag = "dfp_mo";
            dspNode = new Model.DspNode(adSourceTag,
                    adSourceTag,
                    dfp_app_id,
                    dfp_mo_unit_id,
                    ui_type,
                    "null");
            dspLinkedHashMap.put(adSourceTag, dspNode);
        }
        if (!isEmpty(dfp_tw_unit_id)) {
            adSourceTag = "dfp_tw";
            dspNode = new Model.DspNode(adSourceTag,
                    adSourceTag,
                    dfp_app_id,
                    dfp_tw_unit_id,
                    ui_type,
                    "null");
            dspLinkedHashMap.put(adSourceTag, dspNode);
        }
        if (!isEmpty(dfp_hk_unit_id)) {
            adSourceTag = "dfp_hk";
            dspNode = new Model.DspNode(adSourceTag,
                    adSourceTag,
                    dfp_app_id,
                    dfp_hk_unit_id,
                    ui_type,
                    "null");
            dspLinkedHashMap.put(adSourceTag, dspNode);
        }
        if (!isEmpty(dfp_hw_unit_id)) {
            adSourceTag = "dfp_hw";
            dspNode = new Model.DspNode(adSourceTag,
                    adSourceTag,
                    dfp_app_id,
                    dfp_hw_unit_id,
                    ui_type,
                    "null");
            dspLinkedHashMap.put(adSourceTag, dspNode);
        }
    }

    public boolean isEmpty(String string) {
        return string == null || string.trim().equals("");
    }

    public Model.DspNode getDspNode(Element dspElement) {
        String path = dspElement.getAttribute("name");
        Model.DspNode dspNode = null;
        if (path.equals("com.meitu.business.ads.tencent.Tencent")) {
            dspNode = new Model.DspNode("gdt",
                    path,
                    getContentValue(dspElement, "tencent_app_id"),
                    getContentValue(dspElement, "tencent_pos_id"),
                    getContentValue(dspElement, "ui_type"),
                    getContentValue(dspElement, "load_type"));
        } else if (path.equals("com.meitu.business.ads.toutiao.Toutiao")) {
            dspNode = new Model.DspNode("toutiao",
                    path,
                    getContentValue(dspElement, "toutiao_app_id"),
                    getContentValue(dspElement, "toutiao_pos_id"),
                    getContentValue(dspElement, "ui_type"),
                    getContentValue(dspElement, "load_type"));
        } else if (path.equals("com.meitu.business.ads.adiva.Adiva")) {
            dspNode = new Model.DspNode("adiva",
                    path,
                    getContentValue(dspElement, "adiva_app_id"),
                    getContentValue(dspElement, "adiva_pos_id"),
                    getContentValue(dspElement, "ui_type"),
                    getContentValue(dspElement, "load_type"));
        }
        return dspNode;
    }

    public String getValue(Element eElement, String key) {
        return eElement.getAttribute(key);
    }

    public String getContentValue(Element dspElement, String key) {
        NodeList dspNodeList = dspElement.getElementsByTagName(key);
        Element eElement = (Element) dspNodeList.item(0);
        String appId = null;
        if (eElement != null) {
            appId = eElement.getTextContent();
        }
        return appId;
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

    public void outputDataToExcel(String[] titleArray, LinkedHashMap<String, Model> map, String path) {
        try {
            book = Workbook.createWorkbook(new File(path));
            WritableSheet sheet = book.createSheet("testForExcel", 0);

            for (int i = 0; i < titleArray.length; i++) {
                sheet.addCell(new Label(i, 0, titleArray[i]));
            }

            // 定义开始输出的行数
            int row = 2;
            for (Map.Entry<String, Model> entry : map.entrySet()) {
                String postionId = entry.getKey();
                Model model = entry.getValue();
                for (Map.Entry<String, Model.DspNode> entry1 : model.dspLinkedHashMap.entrySet()) {
                    Model.DspNode dspNode = entry1.getValue();
                    int columnIndex = 0;
                    if (dspNode != null) {
                        sheet.addCell(new Label(columnIndex++, row, postionId));
                        sheet.addCell(new Label(columnIndex++, row, dspNode.path));
                        sheet.addCell(new Label(columnIndex++, row, dspNode.appId));
                        sheet.addCell(new Label(columnIndex++, row, dspNode.positionId));
                        sheet.addCell(new Label(columnIndex++, row, dspNode.uiType));
                        sheet.addCell(new Label(columnIndex++, row, dspNode.loadType));
                        sheet.addCell(new Label(columnIndex++, row, model.ad_config_id));
                        sheet.addCell(new Label(columnIndex++, row,  model.animator));
                        sheet.addCell(new Label(columnIndex++, row,  model.page_id));
                        sheet.addCell(new Label(columnIndex++, row,  model.is_main_ad));
                        sheet.addCell(new Label(columnIndex++, row,  model.is_reward_ad));
                        sheet.addCell(new Label(columnIndex++, row,  model.is_full_screen_ad));
                        sheet.addCell(new Label(columnIndex++, row,  model.is_full_interstitial_ad));
                    }
                    row++;
                }
            }

        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            try {
                book.write();
                book.close();
            } catch (Throwable e) {
                e.printStackTrace();
            }
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
                    if (arrayList.get(arr_i) != null) {
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
