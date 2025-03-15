plugins {
    kotlin("jvm") version "1.9.23"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    `maven-publish`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

version = "1.3.5"

tasks.jar {
    enabled = false
}

tasks.shadowJar {
    archiveBaseName.set("LongTimeNoSee")
    archiveVersion.set(project.version.toString())
    archiveClassifier.set("")
    archiveExtension.set("jar")
    minimize()
    mergeServiceFiles()
    manifest {
        attributes(mapOf("Main-Class" to "com.ixiangpro.longtimenosee.LongTimeNoSee"))
    }
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

repositories {
  maven { url = uri("https://maven.aliyun.com/repository/public") }
  mavenCentral()
  maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
  maven("https://oss.sonatype.org/content/repositories/snapshots")
  mavenLocal()
  maven {
    name = "GitHubPackages"
    url = uri("https://maven.pkg.github.com/XXJNOTFOUND/LongTimeNoSee")
    credentials {
      username = System.getenv("USERNAME")
      password = System.getenv("TOKEN")
    }
  }
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      groupId = "com.ixiangpro"
      artifactId = "longtimenosee"
      version = project.version.toString()

      from(components["java"])
    }
  }
}

dependencies {
  compileOnly("org.spigotmc:spigot-api:1.20.2-R0.1-SNAPSHOT")
  compileOnly("net.md-5:bungeecord-chat:1.20-R0.1")
  implementation(kotlin("stdlib"))
}
