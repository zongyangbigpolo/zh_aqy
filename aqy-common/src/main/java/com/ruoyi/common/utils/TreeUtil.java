package com.ruoyi.common.utils;



import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.util.Iterator;
import java.util.stream.Collectors;

/**
 * @author DQC
 * @email 1183158200@qq.com
 * @date 2023/3/16 13:55
 */
public class TreeUtil {
    public TreeUtil() {
    }

    public JSONArray toTree(JSONArray treeNodes, String id, String pid) {
        JSONArray returnList = new JSONArray();
        Iterator iterator = treeNodes.iterator();

        while(iterator.hasNext()) {
            JSONObject next = (JSONObject)iterator.next();
            if (next.containsKey(pid) && next.getString(pid).equals("0")) {
                this.recursionFn(treeNodes, next, id, pid);
                returnList.add(next);
            }
        }

        if (returnList.isEmpty()) {
            returnList = treeNodes;
        }

        return returnList;
    }

    private void recursionFn(JSONArray treeNodes, JSONObject next, String id, String pid) {
        JSONArray childList = this.getChildList(treeNodes, next, id, pid);
        next.put("children", childList);

        for(int i = 0; i < childList.size(); ++i) {
            JSONObject job = childList.getJSONObject(i);
            if (this.getChildList(treeNodes, job, id, pid).size() != 0) {
                Iterator it = childList.iterator();

                while(it.hasNext()) {
                    this.recursionFn(treeNodes, (JSONObject)it.next(), id, pid);
                }
            }
        }

    }

    private JSONArray getChildList(JSONArray treeNodes, JSONObject next, String id, String pid) {
        JSONArray tList = new JSONArray();
        Iterator it = treeNodes.iterator();

        while(it.hasNext()) {
            JSONObject n = (JSONObject)it.next();
            if (n.containsKey(pid) && next.containsKey(id) && n.getString(pid).equals(next.getString(id))) {
                tList.add(n);
            }
        }

        return tList;
    }

    public JSONArray toTreeSelect(JSONArray jsonArray, String id, String pid, String label) {
        return (JSONArray)jsonArray.stream().map((obj) -> {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("id", ((JSONObject)obj).get(id));
            jsonObj.put("pid", ((JSONObject)obj).get(pid));
            jsonObj.put("label", ((JSONObject)obj).get(label));
            JSONArray children = (JSONArray)((JSONObject)obj).get("children");
            if (children.size() != 0) {
                jsonObj.put("children", this.toTreeSelect(children, id, pid, label));
            }

            return jsonObj;
        }).collect(Collectors.toCollection(JSONArray::new));
    }
}

