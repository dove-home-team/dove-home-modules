
val projName: String by settings
rootProject.name = projName

include("curse-rinth-download")//批量下载curseforge modrinth模组

project(":curse-rinth-download").run {
    this.projectDir = file("crd")
    this.name = "crd"
}