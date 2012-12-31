# jcas project Makefile

all:
	(cd core; make all)

build: 
	(cd core; make build)

clean:
	(cd core; make clean)

test:
	(cd core; make test)

.PHONY: all
