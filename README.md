[How to Use Jenkins Less](https://jenkinsworld20162017.sched.com/event/ALQa/how-to-use-jenkins-less) at Jenkins World 2017

Demo commands:

```bash
make -C demo run &
(cd /tmp && rm -rf wc && hg clone http://localhost:8000/ wc && cd wc && echo 'echo(/more/)' >> Jenkinsfile && hg ci -m more && hg push)
ssh -p 2222 -o NoHostAuthenticationForLocalhost=yes localhost build -s -v scm/userspace/default
ssh -p 2222 -o NoHostAuthenticationForLocalhost=yes localhost list-changes scm/userspace/default 2
make -C demo build-tool && docker run --rm --env CONFIG=http://jenkins:8000/ --env COMMAND=list --link jenkins scm-impl
mvn -f userspace-scm-plugin -DskipTests clean install && ssh -p 2222 -o NoHostAuthenticationForLocalhost=yes localhost install-plugin -restart -name userspace-scm = < userspace-scm-plugin/target/userspace-scm.hpi
```
