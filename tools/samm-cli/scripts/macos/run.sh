#!/usr/bin/env bash
DIR="${BASH_SOURCE%/*}"
exec "$DIR/jre/Contents/Home/bin/java" --enable-native-access=ALL-UNNAMED --sun-misc-unsafe-memory-access=allow -Dpolyglotimpl.DisableMultiReleaseCheck=true -jar "$DIR"/samm-cli-*.jar "$@"
