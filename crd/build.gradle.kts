import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil
// api key use
// cmcl launcher
// https://github.com/MrShieh-X/console-minecraft-launcher
val curseApiKeyFile = file("src/main/resources/curse-key.json")
var curseApiKeyConfig: JSONObject

if (!curseApiKeyFile.exists()) {
    curseApiKeyConfig = JSONUtil.createObj().putOnce("apiKey", "")
    curseApiKeyFile.bufferedWriter(Charsets.UTF_8)
        .use {
            it.write(curseApiKeyConfig.toStringPretty())
        }
} else {
    curseApiKeyConfig = JSONUtil.readJSONObject(curseApiKeyFile, Charsets.UTF_8)
}

dependencies {
    implementation("cn.hutool:hutool-all:5.+")
}


tasks.jar {
    from(file("curse-key.json"))
}