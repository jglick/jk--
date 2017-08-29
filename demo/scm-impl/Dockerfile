FROM ubuntu:xenial-20170802
RUN apt-get update -y && apt-get install --no-install-recommends -y python2.7 python libpython2.7-dev make gcc gettext wget
# TODO or simply install mercurial from apt
ENV MERCURIAL_VERSION 4.0.2
ENV MERCURIAL_HOME /opt/mercurial-$MERCURIAL_VERSION
RUN wget --no-check-certificate -nv -O /tmp/mercurial.tar.gz https://www.mercurial-scm.org/release/mercurial-$MERCURIAL_VERSION.tar.gz && \
    cd /opt && \
    tar xfz /tmp/mercurial.tar.gz && \
    rm /tmp/mercurial.tar.gz && \
    cd mercurial-$MERCURIAL_VERSION && \
    make local && \
    ln -sv $MERCURIAL_HOME/hg /usr/bin/hg && \
    cd / && \
    hg --version 

ADD run.sh rheads.py /
RUN chmod a+x /run.sh
ENTRYPOINT ["/run.sh"]

VOLUME /ws
