#!/usr/bin/env python3

import pefile
import shutil
import sys

from tempfile import NamedTemporaryFile


def set_subsystem_type(path, subsystem_type):
    temp = NamedTemporaryFile(delete=False)
    with pefile.PE(path, fast_load=True) as pe:
        subsystem_dict = dict(pefile.subsystem_types)
        pe.OPTIONAL_HEADER.Subsystem = subsystem_dict[subsystem_type]
        pe.write(temp.name)
    temp.close()
    shutil.move(temp.name, path)


if __name__ == "__main__":
    set_subsystem_type(sys.argv[1], "IMAGE_SUBSYSTEM_WINDOWS_CUI")
