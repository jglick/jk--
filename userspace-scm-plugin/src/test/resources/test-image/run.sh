#!/bin/bash
env | sort >&2 # TODO
cd /ws
echo -n $CONFIG > $HEAD
