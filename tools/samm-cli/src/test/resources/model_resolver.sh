#!/bin/bash

root="$1"
samm_ver="$2"
model_urn="$3"

no_samm_urn="${model_urn/urn:samm:/}"
path="${no_samm_urn/://}"
path="${path/\#//}"
path="${root}/${samm_ver}/${path}.ttl"

cat "$path"