package com.sww.analyze;

import java.util.LinkedHashMap;

/**
 * @Author shaowenwen
 * @Date 2020/12/25 7:48 AM
 */
public class Model {

    public String positionId;
    public String ad_config_id;
    public String animator;
    public String is_main_ad;
    public String page_id;


//    public LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
    public LinkedHashMap<String, DspNode> dspLinkedHashMap = new LinkedHashMap<>();

    public static class DspNode {
        String path;
        String appId;
        String positionId;
        String uiType;
        String loadType;

        String moPositionId;
        String twPositionId;
        String hkPositionId;
        String hwPositionId;

        public DspNode(String path, String appId, String positionId, String uiType, String loadType) {
            this.path = path;
            this.appId = appId;
            this.positionId = positionId;
            this.uiType = uiType;
            this.loadType = loadType;
        }

        @Override
        public String toString() {
            return "DspNode{" +
                    "path='" + path + '\'' +
                    ", appId='" + appId + '\'' +
                    ", positionId='" + positionId + '\'' +
                    ", uiType='" + uiType + '\'' +
                    ", loadType='" + loadType + '\'' +
                    ", moPositionId='" + moPositionId + '\'' +
                    ", twPositionId='" + twPositionId + '\'' +
                    ", hkPositionId='" + hkPositionId + '\'' +
                    ", hwPositionId='" + hwPositionId + '\'' +
                    '}';
        }
    }

    public Model(String positionId) {
        this.positionId = positionId;
    }

    public Model(String positionId, String ad_config_id, String animator, String is_main_ad, String page_id) {
        this.positionId = positionId;
        this.ad_config_id = ad_config_id;
        this.animator = animator;
        this.is_main_ad = is_main_ad;
        this.page_id = page_id;
    }
}
