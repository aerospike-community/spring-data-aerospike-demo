#!/bin/bash

git add -f ../docs_processed
git config --local user.email "action@github.com"
git config --local user.name "GitHub Action"
git commit -m "Update documentation" -a

if [ $? -ne 0 ]; then
    echo "nothing to commit"
    exit 0
fi

git push