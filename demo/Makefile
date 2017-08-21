IMAGE=userspace-scm-demo

copy-plugins:
	if [ \! -f ../userspace-scm-plugin/target/userspace-scm.hpi ]; then mvn -f .. -DskipTests install; fi
	if [ \! -f target/test-classes/test-dependencies/index -o pom.xml -nt target/test-classes/test-dependencies/index -o ../userspace-scm-plugin/target/userspace-scm.hpi -nt target/test-classes/test-dependencies/userspace-scm.hpi ]; then mvn test-compile; fi
	rm -rf docker/plugins
	mkdir docker/plugins
	cp -v target/test-classes/test-dependencies/*.hpi docker/plugins

build-tool:
	docker build -t scm-impl scm-impl

build: copy-plugins build-tool
	docker build -t $(IMAGE) docker

ifeq ($(shell uname -s),Darwin)
    STAT_OPT = -f
else
    STAT_OPT = -c
endif
run: build
	docker run --name jenkins --rm -p 127.0.0.1:8080:8080 -p 127.0.0.1:8000:8000 -v /var/run/docker.sock:/var/run/docker.sock --group-add=$(shell stat $(STAT_OPT) %g /var/run/docker.sock) -v $(shell which docker):/usr/bin/docker $(IMAGE)
