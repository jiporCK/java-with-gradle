#!/bin/bash

message="$1"

if [ -z "$message" ]; then
    echo "Please write your commit message!"\
    exit 1
fi

git add .
git commit -m "$message"
git push origin "$(git branch --show-current)"
