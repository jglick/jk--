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
        # or by hgweb: REVISION=$(wget -q -O - "${CONFIG}"branches | xmlstarlet sel -t -m "//_:tr[@class='tagEntry'][normalize-space(_:td[1]/_:a/text())='$HEAD']" -v 'normalize-space(_:td[2]/_:a)')
        REVISION=$(hg id -r "$HEAD" "$CONFIG")
    fi
    if [ $REVISION = $BASELINE ]
    then
        echo NONE
    else
        # TODO not possible to differentiate without requiring workspace for polling, or doing a temporary clone
        echo SIGNIFICANT
    fi
    echo -n $REVISION
;; (*)
    echo Unknown command: $COMMAND >&2
    exit 1
;; esac
