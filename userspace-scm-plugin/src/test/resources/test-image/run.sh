#!/bin/bash
echo "args: $@" >&2 # TODO
env | sort >&2 # TODO
cd /ws
echo -n $CONFIG > $HEAD
