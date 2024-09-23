package io.github.dovehomemod.crd;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

public class CurseForgeManage {
    private final String curseApiKey;
    public CurseForgeManage() throws IOException {
        try(BufferedReader utf8Reader = ResourceUtil.getUtf8Reader("curse-key.json")) {
            curseApiKey = JSONUtil.parseObj(utf8Reader).getStr("apiKey");
        }

//        JSONUtil.parseObj()
    }

    public String getCurseApiKey() {
        return curseApiKey;
    }
}
