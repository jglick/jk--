FROM jenkinsci/jenkins:2.60.3

USER root
COPY plugins /usr/share/jenkins/ref/plugins
ADD JENKINS_HOME /usr/share/jenkins/ref
RUN mkdir /hg
RUN chown -R jenkins.jenkins /usr/share/jenkins/ref/plugins /hg
USER jenkins

RUN hg init /hg && \
    cd /hg && \
    echo 'echo "hello world"' > Jenkinsfile && \
    hg add Jenkinsfile && \
    (echo '[ui]'; echo 'username = Demo <demo@jenkins.io>') > ~/.hgrc && \
    hg commit -m 'init'

EXPOSE 8000

ENV JAVA_OPTS -Dstapler.trace=true
