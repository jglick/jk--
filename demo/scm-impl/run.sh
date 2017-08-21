#!/bin/bash -xe

cd "$WORKSPACE"

case $COMMAND in (checkout)
    if [ "$REVISION" ]
    then
        r="$REVISION"
    else
        r="$HEAD"
    fi
    hg clone --updaterev "$r" "$CONFIG" . >&2
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
