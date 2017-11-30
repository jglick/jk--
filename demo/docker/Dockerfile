FROM jenkinsci/jenkins:2.73.3

USER root

ENV DOCKER_BUCKET get.docker.com
ENV DOCKER_VERSION 1.12.6
RUN set -x \
    && curl -fSL "https://${DOCKER_BUCKET}/builds/Linux/x86_64/docker-$DOCKER_VERSION.tgz" -o docker.tgz \
    && tar -xzvf docker.tgz \
    && mv docker/* /usr/local/bin/ \
    && rmdir docker \
    && rm docker.tgz \
    && docker -v

ENV MAVEN_VERSION 3.5.2
RUN cd /opt && \
    wget -q -O - http://www-us.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar xfz - && \
    /opt/apache-maven-$MAVEN_VERSION/bin/mvn -version
COPY plugins /usr/share/jenkins/ref/plugins
ADD JENKINS_HOME /usr/share/jenkins/ref
ADD simple-maven-project-with-tests /hg
ADD lib /pipeline-lib
RUN chown -R jenkins.jenkins /usr/share/jenkins/ref/plugins /hg /pipeline-lib

USER jenkins

RUN cd /hg && \
    hg init && \
    (echo '[ui]'; echo 'username = Demo <demo@jenkins.io>') > ~/.hgrc && \
    hg commit -A -m 'init' && \
    cd /pipeline-lib && \
    hg init && \
    hg commit -A -m 'init'

EXPOSE 8000 8001

ENV JAVA_OPTS -Dstapler.trace=true -Dhudson.DNSMultiCast.disabled=true -Dhudson.Main.development=true
