FROM jenkinsci/jenkins:2.73

USER root
RUN cd /opt && \
    wget -q -O - http://www-us.apache.org/dist/maven/maven-3/3.5.0/binaries/apache-maven-3.5.0-bin.tar.gz | tar xfz - && \
    /opt/apache-maven-3.5.0/bin/mvn -version
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
