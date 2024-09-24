package io.github.dovehomemod.crd;

import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONNull;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.util.Map;

public interface Minecraft extends Api {
    default JSONArray getMinecraftVersions() {
        try (HttpResponse curseforgeInfo = getCurseforgeInfo("minecraft/version", 1)) {
            return JSONUtil.parseObj(curseforgeInfo.body()).getJSONArray("data");
        }
    }

    default JSONObject getMinecraftVersion(String version) {
        try (HttpResponse curseforgeInfo = getCurseforgeInfo("minecraft/version/" + version, 1)) {
            return JSONUtil.parseObj(curseforgeInfo.body()).getJSONObject("data");
        }
    }

    default JSONArray getMinecraftLoaders() {
        try (HttpResponse curseforgeInfo = getCurseforgeInfo("minecraft/modloader", 1, Map.of("includeAll", true))) {
            return JSONUtil.parseObj(curseforgeInfo.body()).getJSONArray("data");
        }
    }

    default JSONArray getMinecraftVersionLoaders(String version) {
        try (HttpResponse curseforgeInfo = getCurseforgeInfo("minecraft/modloader", 1, Map.of("includeAll", true, "version", version))) {
            return JSONUtil.parseObj(curseforgeInfo.body()).getJSONArray("data");
        }
    }

    default void getMinecraftLoader(String loaderName) {
        try (HttpResponse curseforgeInfo = getCurseforgeInfo("minecraft/modloader/" + loaderName, 1)) {
            System.out.println(JSONUtil.parseObj(curseforgeInfo.body()).toStringPretty());
        }
    }
}
