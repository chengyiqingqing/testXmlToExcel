package com.sww.analyze;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        analyzeTool analyzeTool = new analyzeTool();
//        analyzeTool.analyze();
         ;
        LinkedHashMap<String, ArrayList<String>> map = analyzeTool.analyzeStringXmlFiless(new File("/Users/shaowenwen/workFile/res/strings.xml"),
                new LinkedHashMap<String, ArrayList<String>>());
        System.err.println(" -- "+map.size());
       /* for (Map.Entry<String, ArrayList<String>> entry : map.entrySet()) {
            System.err.println(""+entry.getKey()+" -- "+entry.getValue());
        }*/

    }

}
