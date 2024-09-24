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
        getGameModInfo("minecraft", "819845");
    }

    public JSONObject getGameModInfo(String game, String modid) {// 默认检索mc的
        int gameId = getGameInfo(game).getInt("id", 423);
        try (HttpResponse execute = getCurseforgeInfo("mods/" + modid, 1)) {
            return JSONUtil.parseObj(execute.body()).getJSONObject("data");
        }
    }

    public JSONObject getGameInfo(String game) {
        AtomicReference<JSONObject> obj = new AtomicReference<>();
        try (HttpResponse execute = getCurseforgeInfo("games", 1)) {

            JSONUtil.parseObj(execute.body()).getJSONArray("data").stream().filter(info -> {
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

    public HttpResponse getCurseforgeInfo(String appendUrl, int apiVersion) {
        return HttpRequest.get("https://api.curseforge.com/v" + apiVersion + "/" + appendUrl)
                .header("x-api-key", curseApiKey).timeout(20000).execute();
    }

    public String getCurseApiKey() {
        return curseApiKey;
    }
}
