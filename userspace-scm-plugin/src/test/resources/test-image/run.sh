#!/bin/bash

cd /ws

# Assumption: workspace contains a dir $CONFIG/trees/ with subdirs named acc. to revisions, representing working trees;
# and a dir $CONFIG/heads/ with files named acc. to heads, each containing a revision;
# and a dir $CONFIG/messages/ with files named acc. to revisions, each containing a commit message.
# The subdir wc/ is the checkout target.

case $1 in ( checkout )
    if [ -z "$REV" ]
    then
        REV=`cat $CONFIG/heads/$HEAD`
    fi
    rm -rfv wc >&2
    cp -rv $CONFIG/trees/$REV wc >&2
;; (*)
    echo Unknown command: $1 >&2
    exit 1
;; esac
