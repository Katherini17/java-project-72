plugins {
    application
    checkstyle
    jacoco
    id("com.github.ben-manes.versions") version "0.53.0"
    id("org.sonarqube") version "6.3.1.5724"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.jsoup:jsoup:1.21.2")

    implementation ("io.javalin:javalin:6.7.0")
    implementation("io.javalin:javalin-rendering:6.7.0")
    implementation("io.javalin:javalin-testtools:6.7.0")
    implementation("org.slf4j:slf4j-simple:2.0.7")
    implementation("ch.qos.logback:logback-classic:1.5.18")
    implementation("org.codehaus.janino:janino:3.1.12")

    implementation("com.zaxxer:HikariCP:7.0.2")
    implementation("com.h2database:h2:2.4.240")

    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")

    testCompileOnly("org.projectlombok:lombok:1.18.42")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.42")

    implementation("gg.jte:jte:3.2.1")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("hexlet.code.App")
}

sonar {
    properties {
        property("sonar.projectKey", "Katherini17_java-project-72")
        property("sonar.organization", "katherini-17-projects")
    }
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}
tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}

jacoco {
    toolVersion = "0.8.13"
}

tasks.jacocoTestReport {
    reports {
        xml.required = true
    }
}