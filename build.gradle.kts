import org.w3c.dom.Element

/*
 * Copyright 2016 Dmitry Korotych.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    maven
    idea
    id("com.gradle.plugin-publish") version "0.9.10"
    id("net.researchgate.release") version "2.7.0"
    id("nebula.info") version "3.6.0"
    id("com.github.hierynomus.license") version "0.14.0"

    if (JavaVersion.current().isJava8Compatible) {
//        id("org.sonarqube") version "2.2.1"
//        if (gradle.gradleVersion < "6.0")
//            id("ru.vyarus.quality").version("4.3.0")
        id("io.github.ddimtirov.codacy") version "0.1.0"
    }
}

//if (gradle.gradleVersion < "6.0")
//    quality {
//        configDir = rootProject.file('config')
//    }

jacoco {
    toolVersion = "0.8.1"
}

//    if (gradle.gradleVersion < '6.0') {
//        afterEvaluate {
//            codenarc {
//                configFile = rootProject.file('config/codenarc/codenarc.groovy')
//            }
//        }
//    }

group = "com.github.dkorotych.gradle.maven.exec"

java {
    sourceCompatibility = JavaVersion.VERSION_1_7
    targetCompatibility = JavaVersion.VERSION_1_7
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    if (gradle.gradleVersion < "6.0") {
        compile(gradleApi())
        compile(localGroovy())
        compile("commons-lang:commons-lang:2.6")
        testCompile("org.spockframework:spock-core:1.0-groovy-2.4") {
            exclude(module = "groovy-all")
        }
        testCompile("cglib:cglib-nodep:3.2.4")
        testCompile("org.objenesis:objenesis:2.4")
    } else {
        implementation(gradleApi())
        implementation(localGroovy())
        implementation("commons-lang:commons-lang:2.6")
        testImplementation("org.spockframework:spock-core:1.0-groovy-2.4") {
            exclude(module = "groovy-all")
        }
        testImplementation("cglib:cglib-nodep:3.2.4")
        testImplementation("org.objenesis:objenesis:2.4")
    }
}

license {
    header = project.file("config/license-header.txt")
//    ext.year = Calendar.getInstance().get(Calendar.YEAR)
//    ext.developers = 'Dmitry Korotych'
    skipExistingHeaders = true
    strictCheck = true
    exclude("**/fixtures/**")
}

pluginBundle {
    website = "https://github.com/dkorotych/gradle-maven-exec-plugin"
    vcsUrl = "https://github.com/dkorotych/gradle-maven-exec-plugin"
    description = "Gradle plugin which provides an Maven exec task"
    tags = listOf("maven", "exec", "cross-platform")

    plugins {
//        mavenExecPlugin { TODO
//            id = 'com.github.dkorotych.gradle-maven-exec'
//            displayName = 'Gradle Maven Exec Plugin'
//        }
    }
}

tasks.test {
    systemProperty("pluginVersion", project.version)
}

//groovydoc {
//    link("http://docs.oracle.com/javase/7/docs/api/", "java.", "org.xml.", "javax.", "org.xml.")
//    link "https://docs.gradle.org/${gradle.gradleVersion}/javadoc/", 'org.gradle'
//    link "http://docs.groovy-lang.org/${GroovySystem.version}/html/gapi/",
//            'groovy', 'org.apache.commons.cli', 'org.codehaus.groovy'
//}

if (gradle.gradleVersion >= "4.10")
    tasks.wrapper {
        gradleVersion = "2.13"
    }
else
    tasks.registering(type = Wrapper::class) {
        gradleVersion = "2.13"
    }

idea {
    project {
        vcs = "Git"
        module {
            isDownloadJavadoc = true
            isDownloadSources = true
        }
        ipr.withXml {
            asElement().childNodes.run { (0 until length).map(::item) }.filterIsInstance<Element>()
                    .first { it.tagName == "component" && it.getAttribute("name") == "VcsDirectoryMappings" }
                    .setAttribute("vcs", "Git")
        }
    }
}

tasks["cleanIdea"].doLast {
    delete(".idea")
}

tasks["updateVersion"].doFirst {
    val file = file("README.md")
    var content = file.readText()
//    val versionPattern = "/\d+(?:\.\d+)+/"
//    content = content.replaceAll("id \"com.github.dkorotych.gradle-maven-exec\" version \"${versionPattern}\"",
//            "id \"com.github.dkorotych.gradle-maven-exec\" version \"${version}\"")
//    content = content.replaceAll("gradle-maven-exec-plugin:${versionPattern}",
//            "gradle-maven-exec-plugin:${version}")
    file.writeText(content)
}

tasks["beforeReleaseBuild"].dependsOn(tasks["install"])

//task generateMetricsPart {
//    def readmeFile = file('README.md')
//    def content = readmeFile.text
//    def header = '## Metrics'
//    def lastSpacesPattern = /\s+$/
//    def model = [
//            'header': header,
//            'template': 'FLAT',
//            'key': "${rootProject.group}:${rootProject.name}",
//            'metric': [
//                    'coverage',
//                    'bugs',
//                    'complexity',
//                    'code_smells',
//                    'vulnerabilities',
//                    'tests',
//                    'test_success_density',
//                    'violations',
//                    'ncloc',
//                    'lines'
//            ]
//    ]
//    def templateText = '''
//$header
//[![SonarQube Quality Gate](https://sonarqube.com/api/badges/gate?key=$key&template=$template)](https://sonarqube.com/dashboard?id=$key)
//<%
//metric.each {
//out.println("[![SonarQube ${it.capitalize().replaceAll('_', ' ')}](https://sonarqube.com/api/badges/measure?metric=${it}&key=$key&template=$template)](https://sonarqube.com/component_measures/metric/${it}/list?id=$key)")
//}
//%>
//'''.replaceAll(lastSpacesPattern, '')
//    def template = new SimpleTemplateEngine().createTemplate(templateText)
//    Writable metrics = template.make(model)
//    def metricsIndex = content.indexOf(header)
//    if (metricsIndex > 0) {
//        content = content.take(metricsIndex).replaceAll(lastSpacesPattern, '')
//    }
//    content += metrics
//    readmeFile.text = content
//}

val additionalUseCaseTest by tasks.register("additionalUseCaseTest", GradleBuild::class) {
    dependsOn("install")
    group = "verification"
    buildFile = file("useCase.gradle")
    tasks = listOf("checkMavenApplication3")
}

tasks.register("realUseCaseTest", GradleBuild::class) {
    dependsOn("install")
    group = "verification"
    buildFile = file("useCase.gradle")
    val useCaseTasks = arrayListOf("checkMavenApplication", "checkMavenApplication2")
    useCaseTasks += additionalUseCaseTest.tasks
    tasks = useCaseTasks
}
