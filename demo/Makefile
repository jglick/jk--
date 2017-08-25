IMAGE=userspace-scm-demo

copy-plugins:
	if [ \! -f ../userspace-scm-plugin/target/userspace-scm.hpi ]; then mvn -f .. -DskipTests install; fi
	if [ \! -f target/test-classes/test-dependencies/index -o pom.xml -nt target/test-classes/test-dependencies/index -o ../userspace-scm-plugin/target/userspace-scm.hpi -nt target/test-classes/test-dependencies/userspace-scm.hpi ]; then mvn test-compile; fi
	rm -rf docker/plugins
	mkdir docker/plugins
	cp -v target/test-classes/test-dependencies/*.hpi docker/plugins

build-tool:
	docker build -t scm-impl scm-impl

# http://stackoverflow.com/q/23544282/12916 unclear how best to ensure that the jenkins user can write to this volume
volume:
	docker volume create --name=m2repo
	sudo chmod a+rw $$(docker volume inspect -f '{{.Mountpoint}}' m2repo)

build: copy-plugins build-tool volume
	docker build -t $(IMAGE) docker

ifeq ($(shell uname -s),Darwin)
    STAT_OPT = -f
else
    STAT_OPT = -c
endif
run: build
	docker run \
	    --name jenkins \
	    --rm \
	    -p 127.0.0.1:8080:8080 \
	    -p 127.0.0.1:8000:8000 \
	    -p 127.0.0.1:2222:2222 \
	    -v /var/run/docker.sock:/var/run/docker.sock \
	    --group-add=$(shell stat $(STAT_OPT) %g /var/run/docker.sock) \
	    -v $(shell which docker):/usr/bin/docker \
	    -v m2repo:/m2repo \
	    $(IMAGE)
