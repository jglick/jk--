#!/bin/bash -xe

cd "$WORKSPACE"

case $COMMAND in (checkout)
    if [ "$REVISION" ]
    then
        r="$REVISION"
    else
        r="$HEAD"
    fi
    if [ -d .hg ]
    then
        hg pull --update --rev "$r" >&2
    else
        hg clone --updaterev "$r" "$CONFIG" . >&2
    fi
    if [ $CHANGELOG = true -a "$BASELINE" ]
    then
        hg log -r$(hg id -i)-"${BASELINE}" -T "{node|short}\n{author}\n${CONFIG}rev/{node|short}\n{sub(' .+','',date|hgdate)}\n{indent(desc, '> ')}\n{file_adds % '+ {file}\n'}{file_dels % '- {file}\n'}{file_mods % '* {file}\n'}\n"
    fi
;; (identify)
    hg id -i | tr -d '\n'
;; (compare)
    if [ -z "$REVISION" ]
    then
        REVISION=$(hg id -r "$HEAD" "$CONFIG")
    fi
    if [ $REVISION = $BASELINE ]
    then
        echo NONE
    else
        # TODO not possible to differentiate without requiring workspace for polling, or doing a temporary clone (preferably using a permanent caching volume)
        echo SIGNIFICANT
    fi
    echo -n $REVISION
;; (list)
    PYTHONPATH=$MERCURIAL_HOME python /rheads.py "$CONFIG"
;; (*)
    echo Unknown command: $COMMAND >&2
    exit 1
;; esac
