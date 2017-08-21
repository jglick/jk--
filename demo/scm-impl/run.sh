#!/bin/bash

set -x

PATH=/opt/mercurial-4.0.2:$PATH

cd /ws
ls -la >&2 # TODO

case $COMMAND in (checkout)
    if [ "$REVISION" ]
    then
        r="$REVISION"
    else
        r="$HEAD"
    fi
    hg clone --updaterev "$r" "$CONFIG" .
    # TODO handle changelog
;; (identify)
    # TODO
;; (compare)
    # TODO
    echo SIGNIFICANT
    echo -n $REVISION
;; (*)
    echo Unknown command: $COMMAND >&2
    exit 1
;; esac
