import cn.hutool.core.date.LocalDateTimeUtil
import cn.hutool.core.io.FileUtil
import cn.hutool.http.HttpUtil
import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil
import cn.hutool.setting.Setting
import com.fasterxml.jackson.dataformat.toml.TomlMapper
import com.vanniktech.maven.publish.SonatypeHost
import java.util.Properties


plugins {
    `java-library`
    id("com.github.hierynomus.license") version "0.15.0"
    id("com.vanniktech.maven.publish") version "0.29.0"
    base
    signing
}

var buildProperties = Properties()
rootProject.file("ext/build.ext.properties").bufferedReader().use {
    buildProperties.load(it)
}

group = buildProperties.getProperty("mavenGroup")
version = buildProperties.getProperty("projectVersion")
description = buildProperties.getProperty("projDescription")

base {
    archivesName = name
}

buildscript {
    repositories {
        mavenCentral()
        mavenLocal()
    }
    dependencies {
        classpath("cn.hutool:hutool-json:5.8.31")
        classpath("com.fasterxml.jackson.dataformat:jackson-dataformat-toml:2.17.2")
        classpath("cn.hutool:hutool-setting:5.8.31")
        classpath("cn.hutool:hutool-http:5.8.31")
    }
}

var s = Setting(file(".git/config").absolutePath)
var branch = FileUtil.readUtf8String(file(".git/HEAD")).replace("ref: refs/heads/", "").trim()

val repos =
    JSONUtil.parseObj(HttpUtil.get(s.get("remote \"origin\"", "url")
            .replace(".git", "")
            .replace("https://github.com/", "https://api.github.com/repos/"), Charsets.UTF_8))
var parse =
    LocalDateTimeUtil.parse(repos.getStr("created_at").replace("Z", "+0000"), "yyyy-MM-dd'T'HH:mm:ssZ")



var file = file("maven.toml").apply {
    if (!exists()) {
        FileUtil.copyFile(file("gradle/template.toml"), this)
    }
}

var mavenToml: JSONObject = JSONUtil.createObj()
file.bufferedReader(Charsets.UTF_8).use {
    val readTree = TomlMapper().readTree(it)
    mavenToml = JSONUtil.parseObj(readTree.toPrettyString())
}

var center = mavenToml.getJSONObject("center")
var signToml = mavenToml.getJSONObject("center")




subprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "base")
    apply(plugin = "com.vanniktech.maven.publish")
    apply(plugin = "java-library")

    base {
        archivesName = "${rootProject.base.archivesName.get()}-$name"
    }

}

allprojects {
    repositories {
        mavenCentral()
        mavenLocal()
        maven {
            url = uri("https://central.sonatype.com/api/v1/publisher/deployments/download/")

        }
    }

    mavenPublishing {

        publishToMavenCentral(SonatypeHost.DEFAULT, automaticRelease = true)
        coordinates(project.group.toString(), base.archivesName.get(), project.version.toString())
        pom {
            name = base.archivesName.get()
            description = project.description
            inceptionYear = parse.year.toString()
            licenses {
                license {
                    name = mavenToml.getStr("license")
                    url = "${repos.getStr("svn_url")}/blob/$branch/LICENSE"
                    description = "${repos.getStr("svn_url")}/blob/$branch/LICENSE"
                }
            }
            developers {
                mavenToml.getJSONArray("authors").forEach {
                    it as JSONObject
                    developer {
                        id = it.getStr("id")
                        name = it.getStr("name")
                        url = it.getStr("url")
                    }
                }
            }
            scm {
                url = repos.getStr("html_url")
                connection = "scm:git:${repos.getStr("git_url")}"
                developerConnection = "scm:git:ssh://${repos.getStr("ssh_url")}"
            }
        }
    }


}





