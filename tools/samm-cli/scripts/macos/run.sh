#!/usr/bin/env bash
HERE="\${BASH_SOURCE%/*}"
"\$HERE/jre/Contents/Home/bin/java" --enable-native-access=ALL-UNNAMED --sun-misc-unsafe-memory-access=allow -Dpolyglotimpl.DisableMultiReleaseCheck=true -jar "\$HERE/samm-cli-*.jar" "\$@"
