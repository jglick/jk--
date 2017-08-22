```bash
make -C demo run &
ssh -p 2222 -o StrictHostKeyChecking=no localhost build -s -v userspace-scm
hg clone http://localhost:8000/ wc
pushd wc
echo 'echo(/more/)' >> Jenkinsfile
hg ci -m more
hg push
ssh -p 2222 -o StrictHostKeyChecking=no localhost build -s -v userspace-scm
ssh -p 2222 -o StrictHostKeyChecking=no localhost list-changes userspace-scm 2
docker cp jenkins:/var/jenkins_home/jobs/userspace-scm/builds/lastSuccessfulBuild/build.xml - | tr -d '\000'
docker cp jenkins:/var/jenkins_home/jobs/userspace-scm/builds/lastSuccessfulBuild/changelog0.xml - | tr -d '\000'
docker cp jenkins:/var/jenkins_home/jobs/userspace-scm/builds/lastSuccessfulBuild/polling.log - | tr -d '\000'
mvn -f userspace-scm-plugin -DskipTests clean install && ssh -p 2222 -o StrictHostKeyChecking=no localhost install-plugin -restart -name userspace-scm = < userspace-scm-plugin/target/userspace-scm.hpi
```
