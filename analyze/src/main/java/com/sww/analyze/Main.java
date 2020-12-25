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

        LinkedHashMap<String, Model> map = analyzeTool.analyzeStringXmlFiless(new File("/Users/shaowenwen/workFile/res/strings.xml"),
                new LinkedHashMap<String, Model>());
        System.out.println(" -- " + map.size());
        for (Map.Entry<String, Model> entry : map.entrySet()) {
            String postionId = entry.getKey();
            Model model = entry.getValue();
            for (Map.Entry<String, Model.DspNode> entry1 : model.dspLinkedHashMap.entrySet()) {
                Model.DspNode dspNode = entry1.getValue();
                if (dspNode != null) {
                    System.out.println(postionId + " -- " + dspNode.path + " -- " + dspNode.appId + " -- " + dspNode.positionId + " -- " + dspNode.uiType + " -- " + dspNode.loadType
                            + " -- " + model.ad_config_id
                            + " -- " + model.animator
                            + " -- " + model.page_id
                            + " -- " + model.is_main_ad
                            + " -- " + model.is_reward_ad
                            + " -- " + model.is_full_screen_ad
                            + " -- " + model.is_full_interstitial_ad
                    );
                }
            }
        }


    }

}
