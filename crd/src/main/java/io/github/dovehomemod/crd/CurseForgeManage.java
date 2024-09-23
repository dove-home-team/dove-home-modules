package io.github.dovehomemod.crd;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class CurseForgeManage {
    private final String curseApiKey;
    public CurseForgeManage() throws IOException {
        try(BufferedReader utf8Reader = ResourceUtil.getUtf8Reader("curse-key.json")) {
            curseApiKey = JSONUtil.parseObj(utf8Reader).getStr("apiKey");
        }


    }

    public JSONObject getGameInfo(String game) {
        AtomicReference<JSONObject> obj = new AtomicReference<>();
        try (HttpResponse execute = HttpRequest.get("https://api.curseforge.com/v1/games")
                .header("x-api-key", curseApiKey).timeout(20000).execute()) {
            var body = execute.body();
            JSONUtil.parseObj(body).getJSONArray("data").stream().filter(info -> {
                if (info instanceof JSONObject jif) {
                    return jif.getStr("slug").equals(game);
                }
                return false;
            }).map(info -> (JSONObject) info).findFirst().ifPresent(obj::set);
        }
        if (obj.get() == null) {//避免非空判断
            obj.set(JSONUtil.createObj());
        }
        return obj.get();
    }

    public String getCurseApiKey() {
        return curseApiKey;
    }
}
