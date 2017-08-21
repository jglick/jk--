#!/bin/bash

cd "$WORKSPACE"

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
    if [ $CHANGELOG = true -a "$BASELINE" -a $REVISION -gt "$BASELINE" ]
    then
        for ((r = $BASELINE + 1; r <= $REVISION; r++))
        do
            echo $r
            echo dev
            echo http://nowhere.net/commit/$r
            echo 0
            (cat $CONFIG/messages/$r; echo) | sed -e 's/^/> /'
            prev=$((r - 1))
            # TODO parsing output of `diff -qr $CONFIG/trees/$prev $CONFIG/trees/$r` is tricky: "Files $CONFIG/trees/$prev/… and $config/trees/$r/… differ" vs. "Only in $CONFIG/trees/$prev: …" (or "Only in $CONFIG/trees/$prev/…: …") etc.
            case $r in (3)
                echo '* f'
                echo '+ f2'
            ;; (4)
                echo '+ .stuff'
            ;; (*)
                echo TODO for now just hard-coding some file diffs, not for $r
            esac
            echo
        done
    fi
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
        echo NONE
    elif [ `diff -qr $CONFIG/trees/$BASELINE $CONFIG/trees/$REVISION | fgrep -v .stuff | wc -l` -eq 0 ]
    then
        echo INSIGNIFICANT
    else
        echo SIGNIFICANT
    fi
    echo -n $REVISION
;; (*)
    echo Unknown command: $COMMAND >&2
    exit 1
;; esac
