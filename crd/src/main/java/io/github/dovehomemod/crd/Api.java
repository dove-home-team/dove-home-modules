package io.github.dovehomemod.crd;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

public interface Api {

    String getCurseApiKey();

    default HttpResponse getCurseforgeInfo(String appendUrl, int apiVersion, Map<String, ? extends Serializable> parameters) {
        StringBuilder url = new StringBuilder().append("https://api.curseforge.com/v").append(apiVersion).append("/").append(appendUrl);
        if (!parameters.isEmpty()) {
            url.append("?");
            Iterator<? extends Map.Entry<String, ? extends Serializable>> iterator = parameters.entrySet().iterator();
            if (iterator.hasNext()) {
                Map.Entry<String, ? extends Serializable> next = iterator.next();
                url.append(next.getKey()).append("=").append(next.getValue().toString());
            }
            while (iterator.hasNext()) {
                Map.Entry<String, ? extends Serializable> next = iterator.next();
                url
                        .append("&")
                        .append(next.getKey())
                        .append("=")
                        .append(next.getValue().toString());
            }
        }
        return HttpRequest.get(url.toString())
                .header("x-api-key", getCurseApiKey()).timeout(20000).execute();
    }

    default HttpResponse getCurseforgeInfo(String appendUrl, int apiVersion) {
        return getCurseforgeInfo(appendUrl, apiVersion, Map.of());
    }
}
