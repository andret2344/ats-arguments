plugins {
	idea
	java
	jacoco
	`maven-publish`
	checkstyle
	id("org.barfuin.gradle.jacocolog") version "3.1.0"
}

sourceSets {
	create("integration") {
		java.srcDir("src/integration/java")
		resources.srcDir("src/integration/resources")
		compileClasspath += sourceSets["main"].output + sourceSets["test"].output
		runtimeClasspath += sourceSets["main"].output + sourceSets["test"].output
	}
}

configurations {
	named("integrationImplementation") {
		extendsFrom(configurations.getByName("testImplementation"))
	}
	named("integrationRuntimeOnly") {
		extendsFrom(configurations.getByName("testRuntimeOnly"))
	}
}

repositories {
	mavenCentral()
	maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
}

dependencies {
	compileOnly(
			group = "org.spigotmc",
			name = "spigot-api",
			version = "${project.properties["spigotVersion"]}-R0.1-SNAPSHOT"
	)
	compileOnly(group = "org.projectlombok", name = "lombok", version = "1.18.30")
	implementation(group = "org.jetbrains", name = "annotations", version = "24.1.0")
	annotationProcessor(group = "org.projectlombok", name = "lombok", version = "1.18.30")

	testCompileOnly(group = "org.projectlombok", name = "lombok", version = "1.18.30")
	testImplementation(group = "org.assertj", name = "assertj-core", version = "3.24.2")
	testImplementation(group = "org.mockito", name = "mockito-core", version = "5.8.0")
	testImplementation(group = "org.mockito", name = "mockito-inline", version = "5.2.0")
	testImplementation(group = "org.mockito", name = "mockito-testng", version = "0.5.2")
	testImplementation(
			group = "org.spigotmc",
			name = "spigot-api",
			version = "${project.properties["spigotVersion"]}-R0.1-SNAPSHOT"
	)
	testImplementation(group = "org.testng", name = "testng", version = "7.8.0")
	testAnnotationProcessor(group = "org.projectlombok", name = "lombok", version = "1.18.30")
}

tasks {
	compileJava {
		options.compilerArgs.addAll(
				listOf(
						"-parameters",
						"-g",
						"-Xlint:deprecation",
						"-Xlint:unchecked"
				)
		)
	}

	compileTestJava {
		options.compilerArgs.addAll(
				listOf(
						"-parameters",
						"-g",
						"-Xlint:deprecation",
						"-Xlint:unchecked"
				)
		)
	}

	checkstyleMain {
		exclude("example/**.java")
	}

	checkstyleTest {
		exclude("**")
	}

	named<Checkstyle>("checkstyleIntegration") {
		exclude("**")
	}

	create<Test>("integrationTest") {
		description = "Runs the integration tests."
		group = "verification"
		testClassesDirs = sourceSets["integration"].output.classesDirs
		classpath = sourceSets["integration"].runtimeClasspath
		outputs.upToDateWhen { false }
		useTestNG()
	}

	check {
		dependsOn("integrationTest")
	}

	test {
		jacoco {
			exclude("**/Material")
		}
		useTestNG()
		finalizedBy(jacocoTestCoverageVerification, jacocoAggregatedReport)
	}

	jacocoTestReport {
		classDirectories.setFrom(files(classDirectories.files.map {
			fileTree(it).matching {
				exclude("example/**")
			}
		}))
	}

	jacocoTestCoverageVerification {
		violationRules {
			rule {
				classDirectories.setFrom(jacocoTestReport.get().classDirectories)
				limit {
					minimum = BigDecimal("1")
				}
			}
		}
	}

	javadoc {
		source = sourceSets["main"].allJava
		classpath = configurations["compileClasspath"]

		options {
			this as StandardJavadocDocletOptions

			memberLevel = JavadocMemberLevel.PROTECTED
			author(true)

			links("https://docs.oracle.com/en/java/javase/11/docs/api/")
		}
	}

	register<Jar>("sourceJar") {
		archiveClassifier.set("sources")
		from(sourceSets["main"].allJava)
	}

	register<Jar>("packageJavadoc") {
		archiveClassifier.set("javadoc")
		from(named("javadoc"))
	}

	publishing {
		publications {
			create<MavenPublication>("maven") {
				artifact(jar)
//				artifact(named("sourceJar"))
//				artifact(named("packageJavadoc"))
				groupId = project.properties["group"] as String
				version = project.properties["version"] as String
				artifactId = project.properties["artifact"] as String
			}
		}
		repositories {
			maven {
				name = "GitLab"

				url = uri("https://gitlab.com/api/v4/projects/12063927/packages/maven")
				credentials(HttpHeaderCredentials::class) {
					name = "Job-Token"
					value = System.getenv("CI_JOB_TOKEN")
				}
				authentication {
					create<HttpHeaderAuthentication>("header")
				}
			}
		}
	}

	jar {
		archiveBaseName.set("${project.properties["artifact"]}")
	}
}
