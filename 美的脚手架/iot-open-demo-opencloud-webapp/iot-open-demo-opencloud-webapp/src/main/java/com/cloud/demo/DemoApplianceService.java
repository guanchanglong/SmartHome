package com.cloud.demo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cloud.common.vo.ApplianceStatusVo;
import com.cloud.common.vo.ApplianceVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户产品服务
 */
@Component
public class DemoApplianceService {

    @Autowired
    private DemoSubscriptionSchedule demoSubscriptionSchedule;

    /**
     * 用hashmap模拟用户
     */
    private HashMap<String, List<ApplianceVo>> applianceList = new HashMap<String, List<ApplianceVo>>() {{
        //第一个用户没有设备
        put("13511111111", new ArrayList<ApplianceVo>() {
        });
        //第二个用户一个在线设备
        put("13522222222", new ArrayList<ApplianceVo>() {{
            add(new ApplianceVo() {{
                setApplianceCode("1099511842222");
                setOnlineStatus("1");
                setName("智能灯2");
                setType("0x13");
                setSn8("L0000002");
            }});
        }});
        //第三个用户一个离线设备
        put("13533333333", new ArrayList<ApplianceVo>() {{
            add(new ApplianceVo() {{
                setApplianceCode("1099511843333");
                setOnlineStatus("0");
                setName("智能灯3");
                setType("0x13");
                setSn8("L0000002");
            }});
        }});
        //第四用户两个设备 一个在线一个离线
        put("13588888888", new ArrayList<ApplianceVo>() {{
            add(new ApplianceVo() {{
                setApplianceCode("1099511848888");
                setOnlineStatus("1");
                setName("智能灯8");
                setType("0x13");
                setSn8("L0000002");
            }});
            add(new ApplianceVo() {{
                setApplianceCode("1099511849999");
                setOnlineStatus("0");
                setName("智能灯9");
                setType("0x13");
                setSn8("L0000002");
            }});
        }});
    }};


    /**
     * 根据用户名获取设备列表
     *
     * @param username
     * @return 没有就报错
     */
    public List<ApplianceVo> getUserDeviceList(String username) throws Exception {
        if (applianceList.containsKey(username)) {
            return applianceList.get(username);
        }else{
            return new ArrayList<>();
        }
    }

    /**
     * 模拟设备订阅
     *
     */
    public void subscribeApplianceList(String username, JSONArray applianceCodes) {
        demoSubscriptionSchedule.addSubscription(username,applianceCodes);
    }


    public void unSubscribeApplianceList(String username, JSONArray applianceCodes) {
        demoSubscriptionSchedule.delSubscription(username,applianceCodes);
    }


    HashMap<String, String> applianceCode2Status = new HashMap<String, String>() {{
        put("1099511842222", "on");
        put("1099511843333", "off");
        put("1099511848888", "on");
        put("1099511849999", "off");
    }};

    /**
     * 控制设备
     *
     * @param username
     * @param command
     */
    public void controlAppliance(String username, JSONObject command) throws Exception {
        List<ApplianceVo> applianceVoList = getUserDeviceList(username);
        String applianceCode = command.getString("applianceCode");
        //假设这个command是可以直接执行而不需要转换的
        //ApplianceVo applianceVo = applianceVoList.stream().filter(v -> v.getApplianceCode().equals(applianceCode)).findAny().get();
        Iterator<ApplianceVo> ite = applianceVoList.iterator();
        boolean applianceNotExists = true;
        while (ite.hasNext()) {
            ApplianceVo vo = ite.next();
            if(vo.getApplianceCode().equals(applianceCode)){
                applianceCode2Status.put(vo.getApplianceCode(),command.getJSONObject("control").getString("power"));
                applianceNotExists = false;
                break;
            }
        }
        if (applianceNotExists){
            throw new Exception("No Appliance Found");
        }
        //假设设备的整个操作流程耗时
        try {
            Random random = new Random();
            Thread.sleep(500L + random.nextInt(500));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询状态
     *
     * @param username
     * @param applianceCodes
     */
    public List<ApplianceStatusVo> getApplianceStatusByList(String username, JSONArray applianceCodes) throws Exception {
        boolean applianceNotExists = true;
        List<ApplianceVo> applianceVoList = getUserDeviceList(username);
        List<ApplianceStatusVo> applianceStatusVos = new ArrayList<>();
        Iterator<Object> codeIte = applianceCodes.iterator();
        while (codeIte.hasNext()){
            String applianceCode = (String)codeIte.next();
            Iterator<ApplianceVo> ite = applianceVoList.iterator();
            while (ite.hasNext()){
                ApplianceVo vo = ite.next();
                if(vo.getApplianceCode().equals(applianceCode)){
                    applianceStatusVos.add(
                            new ApplianceStatusVo() {
                                {
                                    setApplianceCode(vo.getApplianceCode());
                                    setOnlineStatus(vo.getOnlineStatus());
                                    getStatus().put("power", applianceCode2Status.get(vo.getApplianceCode()));
                                    getStatus().put("color_blue",255);
                                    getStatus().put("color_red", 0);
                                    getStatus().put("brightness",50);
                                    getStatus().put("color_temperature", 4000);
                                    getStatus().put("color_gre", 0);
                                    getStatus().put("color_mode", 0);
                                }
                            }
                    );
                    applianceNotExists = false;
                    break;
                }
            }
        }

        if (applianceNotExists){
            throw new Exception("No Appliance Found");
        }
        /**
        applianceVoList.stream().filter(v -> applianceCodes.stream().anyMatch(a -> v.getApplianceCode().equals((String) a))).map(v -> {
            return applianceStatusVos.add(
                    new ApplianceStatusVo() {
                        {
                            setApplianceCode(v.getApplianceCode());
                            setOnlineStatus(v.getOnlineStatus());
                            getStatus().put("power", applianceCode2Status.get(v.getApplianceCode()));
                            getStatus().put("color_blue",255);
                            getStatus().put("color_red", 0);
                            getStatus().put("brightness",50);
                            getStatus().put("color_temperature", 4000);
                            getStatus().put("color_gre", 0);
                            getStatus().put("color_mode", 0);
                        }
                    }
            );
        });**/
        return applianceStatusVos;
    }

    public static void main(String[] args) {
        try {
            DemoApplianceService demoApplianceService = new DemoApplianceService();
            List<ApplianceVo> deviceList = demoApplianceService.getUserDeviceList("13588888888");
            System.out.println("deviceList"+ JSONObject.toJSON(deviceList));

            List<ApplianceStatusVo> statusList = demoApplianceService.getApplianceStatusByList("13588888888", new JSONArray() {{
                add("1099511848888");
                add("1099511849999");
            }});
            System.out.println("statusList"+ JSONObject.toJSON(statusList));

            demoApplianceService.controlAppliance("13588888888",new JSONObject(){{put("applianceCode","1099511848888");put("control",new JSONObject(){ {put("power","off");}});}});

            List<ApplianceStatusVo> statusList2 = demoApplianceService.getApplianceStatusByList("13588888888", new JSONArray() {{
                add("1099511848888");
                add("1099511849999");
            }});
            System.out.println("statusList2"+ JSONObject.toJSON(statusList2));
            System.out.println("----------");
            System.out.println(JSONObject.toJSONString(new ApplianceVo() {
                {
                    setApplianceCode("1099511848888");
                    setOnlineStatus("1");
                    setName("智能灯8");
                    setType("0x13");
                    setSn8("L0000002");
                };}));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void addAppliance(String mobile, ApplianceVo vo) {
        boolean needAdd = true;
        List<ApplianceVo> applianceVos = new ArrayList<>();
        if(applianceList.containsKey(mobile)) {
            applianceVos = applianceList.get(mobile);
            for (ApplianceVo innerVo : applianceVos) {
                if (innerVo.getApplianceCode().equals(vo.getApplianceCode())) {
                    needAdd = false;
                    break;
                }
            }
        }
        if (needAdd) {
            applianceVos.add(vo);
            applianceList.put(mobile, applianceVos);//。。。
        }
    }

    public void delAppliance(String mobile, String applianceCode) {
       if(applianceList.containsKey(mobile)){
           List<ApplianceVo> applianceVos = applianceList.get(mobile);
           for (ApplianceVo innerVo : applianceVos) {
               if (innerVo.getApplianceCode().equals(applianceCode)) {
                   applianceVos.remove(innerVo);
               }
           }
           applianceList.put(mobile, applianceVos);
       }
    }
}
