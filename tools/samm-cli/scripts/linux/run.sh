#!/usr/bin/env bash
HERE="\${BASH_SOURCE%/*}"
"\$HERE/jre/bin/java" --enable-native-access=ALL-UNNAMED --sun-misc-unsafe-memory-access=allow -jar "\$HERE/samm-cli-*.jar" "\$@"
