plugins {
    java
    application
    id("org.graalvm.buildtools.native") version "0.10.3"
    id("com.diffplug.spotless") version "6.25.0"
}

val mainClassName = "org.jtop.Main"

group = "org.jtop"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://central.sonatype.com/repository/maven-snapshots/")
        mavenContent { snapshotsOnly() }
    }
}

dependencies {
    implementation(platform("dev.tamboui:tamboui-bom:0.3.0"))
    implementation("dev.tamboui:tamboui-toolkit")
    implementation("dev.tamboui:tamboui-jline3-backend")
    implementation("com.github.oshi:oshi-core:7.1.0")
}

tasks.named<JavaExec>("run") {
    mainClass.set(mainClassName)
    standardInput = System.`in`
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = mainClassName
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

spotless {
    java {
        eclipse().configFile("eclipse-formatter.xml")
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
}

graalvmNative {
    binaries {
        named("main") {
            imageName.set("jtop")
            mainClass.set(mainClassName)
            buildArgs.add("--no-fallback")
            buildArgs.add("-H:+ReportExceptionStackTraces")
        }
    }
}