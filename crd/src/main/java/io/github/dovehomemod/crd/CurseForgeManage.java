package io.github.dovehomemod.crd;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class CurseForgeManage implements Minecraft {
    private String curseApiKey;

    /**
     * curse maven instance
     */
    CurseForgeManage() {
        try(BufferedReader utf8Reader = ResourceUtil.getUtf8Reader("curse-key.json")) {
            curseApiKey = JSONUtil.parseObj(utf8Reader).getStr("apiKey");
        } catch (IOException e) {
            curseApiKey = "";
        }
    }

    public JSONObject getGameModInfo(String game, String modid, String version) {
        try (HttpResponse execute = getCurseforgeInfo("mods/" + modid, 1)) {
            JSONObject data = JSONUtil.parseObj(execute.body()).getJSONObject("data");
            return Objects.equals(data.getInt("gameId"), getGameInfo(game).getInt("id")) ? data : JSONUtil.createObj();
        }
    }

    public JSONObject getGameModInfo(String game, String modid) {
        try (HttpResponse execute = getCurseforgeInfo("mods/" + modid, 1)) {
            JSONObject data = JSONUtil.parseObj(execute.body()).getJSONObject("data");
            return Objects.equals(data.getInt("gameId"), getGameInfo(game).getInt("id")) ? data : JSONUtil.createObj();
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

    @Override
    public String getCurseApiKey() {
        return curseApiKey;
    }
}
