FROM jenkinsci/jenkins:2.60.3

USER root
COPY plugins /usr/share/jenkins/ref/plugins
RUN chown -R jenkins.jenkins /usr/share/jenkins/ref/plugins
USER jenkins

ADD JENKINS_HOME /usr/share/jenkins/ref
