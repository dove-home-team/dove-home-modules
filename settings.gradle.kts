import java.util.Properties

var settingsProperties = Properties()
file("ext/settings.ext.properties").bufferedReader(Charsets.UTF_8).use {
    settingsProperties.load(it)
}

rootProject.name = settingsProperties.getProperty("projName")

include("curse-rinth-download")//批量下载curseforge modrinth模组

project(":curse-rinth-download").run {
    this.projectDir = file("crd")
    this.name = "crd"
}
