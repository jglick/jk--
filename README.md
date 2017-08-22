```bash
make -C demo run &
ssh -p 2222 -o StrictHostKeyChecking=no localhost build -s -v userspace-scm
hg clone http://localhost:8000/ wc
pushd wc
echo 'echo(/more/)' >> Jenkinsfile
hg ci -m more
hg push
ssh -p 2222 -o StrictHostKeyChecking=no localhost build -s -v userspace-scm
docker cp jenkins:/var/jenkins_home/jobs/userspace-scm/builds/lastSuccessfulBuild/build.xml -
# TODO list-changes does not work until 2.70
docker cp jenkins:/var/jenkins_home/jobs/userspace-scm/builds/lastSuccessfulBuild/changelog0.xml -
mvn -f userspace-scm-plugin -DskipTests clean install && ssh -p 2222 -o StrictHostKeyChecking=no localhost install-plugin -restart -name userspace-scm = < userspace-scm-plugin/target/userspace-scm.hpi
```
