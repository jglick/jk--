IMAGE=userspace-scm-demo

copy-plugins:
	if [ \! -f target/test-classes/test-dependencies/index -o pom.xml -nt target/test-classes/test-dependencies/index ]; then mvn test-compile; fi
	rm -rf docker/plugins
	mkdir docker/plugins
	cp -v target/test-classes/test-dependencies/*.hpi docker/plugins

build: copy-plugins
	docker build -t $(IMAGE) docker

run: build
	docker run --rm -p 127.0.0.1:8080:8080 $(IMAGE)
