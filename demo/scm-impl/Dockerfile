FROM ubuntu:xenial-20170802
RUN apt-get update -y && apt-get install --no-install-recommends -y python2.7 python libpython2.7-dev make gcc gettext wget
ENV MERCURIAL_VERSION 4.0.2
RUN wget --no-check-certificate -nv -O /tmp/mercurial.tar.gz https://www.mercurial-scm.org/release/mercurial-$MERCURIAL_VERSION.tar.gz && \
    cd /opt && \
    tar xfz /tmp/mercurial.tar.gz && \
    rm /tmp/mercurial.tar.gz && \
    cd mercurial-$MERCURIAL_VERSION && \
    make local && \
    ln -sv /opt/mercurial-$MERCURIAL_VERSION/hg /usr/bin/hg && \
    cd / && \
    hg --version 

ADD run.sh /
RUN chmod a+x /run.sh
ENTRYPOINT ["/run.sh"]

VOLUME /ws
