install:
	make -C app install

run-dist:
	make -C app install

clean:
	make -C app clean

run:
	make -C app run

build:
	make -C app build

test:
	make -C app test

lint-main:
	make -C app lint-main

lint-test:
	make -C app lint-test

coverage:
	make -C app coverage

update-check:
	make -C app uodate-check

.PHONY: build