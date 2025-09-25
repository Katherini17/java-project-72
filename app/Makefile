install:
	./gradlew installDist

run-dist:
	./build/install/app/bin/app

clean:
	./gradlew clean
run:
	./gradlew run

build:
	./gradlew clean build

test:
	./gradlew clean test

lint-main:
	./gradlew checkstyleMain

lint-test:
	./gradlew checkstyleTest

coverage:
	./gradlew jacocoTestReport

update-check:
	./gradlew dependencyUpdates

.PHONY: build