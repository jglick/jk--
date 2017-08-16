#!/bin/bash

cd /ws

# Assumption: workspace contains a dir $CONFIG/trees/ with subdirs named acc. to revisions, representing working trees;
# and a dir $CONFIG/heads/ with files named acc. to heads, each containing a revision;
# and a dir $CONFIG/messages/ with files named acc. to revisions, each containing a commit message.
# The subdir wc/ is the checkout target.
# Files named .stuff are ignored for purposes of polling.

case $COMMAND in (checkout)
    if [ -z "$REVISION" ]
    then
        REVISION=`cat $CONFIG/heads/$HEAD`
    fi
    rm -rfv wc >&2
    cp -rv $CONFIG/trees/$REVISION wc >&2
    echo -n $REVISION > revision
;; (identify)
    cat revision
;; (compare)
    if [ -z "$REVISION" ]
    then
        REVISION=`cat $CONFIG/heads/$HEAD`
    fi
    echo Comparing $REVISION to $BASELINE >&2
    diff -qr $CONFIG/trees/$BASELINE $CONFIG/trees/$REVISION | fgrep -v .stuff >&2
    if [ $REVISION = $BASELINE ]
    then
        echo -n NONE $REVISION
    elif [ `diff -qr $CONFIG/trees/$BASELINE $CONFIG/trees/$REVISION | fgrep -v .stuff | wc -l` -eq 0 ]
    then
        echo -n INSIGNIFICANT $REVISION
    else
        echo -n SIGNIFICANT $REVISION
    fi
;; (*)
    echo Unknown command: $COMMAND >&2
    exit 1
;; esac
