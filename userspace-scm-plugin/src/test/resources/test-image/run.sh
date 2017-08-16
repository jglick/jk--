#!/bin/bash

cd /ws

# Assumption: workspace contains a dir $CONFIG/trees/ with subdirs named acc. to revisions, representing working trees;
# and a dir $CONFIG/heads/ with files named acc. to heads, each containing a revision;
# and a dir $CONFIG/messages/ with files named acc. to revisions, each containing a commit message.
# The subdir wc/ is the checkout target.
# Files named .stuff are ignored for purposes of polling.

case $1 in (checkout)
    if [ -z "$REV" ]
    then
        REV=`cat $CONFIG/heads/$HEAD`
    fi
    rm -rfv wc >&2
    cp -rv $CONFIG/trees/$REV wc >&2
    echo -n $REV > rev
;; (identify)
    cat rev
;; (compare)
    if [ -z "$REV" ]
    then
        REV=`cat $CONFIG/heads/$HEAD`
    fi
    echo Comparing $REV to $BASELINE >&2
    diff -qr $CONFIG/trees/$BASELINE $CONFIG/trees/$REV | fgrep -v .stuff >&2
    if [ $REV = $BASELINE ]
    then
        echo -n NONE $REV
    elif [ `diff -qr $CONFIG/trees/$BASELINE $CONFIG/trees/$REV | fgrep -v .stuff | wc -l` -eq 0 ]
    then
        echo -n INSIGNIFICANT $REV
    else
        echo -n SIGNIFICANT $REV
    fi
;; (*)
    echo Unknown command: $1 >&2
    exit 1
;; esac
