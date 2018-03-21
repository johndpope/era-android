#!/usr/bin/env bash
TOKEN="f0ca30e5-254e-4d82-85d1-a7c41cfe14fd"
GIT_HASH=$(git rev-parse HEAD)

python /Library/Python/2.7/site-packages/codecov/__init__.py --token="$TOKEN" --commit=$GIT_HASH --branch="develop"