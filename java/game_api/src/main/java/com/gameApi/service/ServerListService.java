package com.gameApi.service;

import com.gameApi.request.ServerListRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ServerListService {
    public List<Map<String,  Object>> serverList(ServerListRequest request) {
        List <Map<String,  Object>> list = new ArrayList<>();
        list.add(new HashMap<String, Object>() {{
             put("ip", "113.44.69.217");
             put("port", 29034);
             put("name", "ios");
              put("httpport",3172);
              put("active", true);
        }});
        return list;
    }
}
