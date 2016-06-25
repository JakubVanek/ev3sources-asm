LEGO Logo interpreter & lms2012 assembler
=========================================
This repo contains a decompiled and unscrambled LEGO Logo language interpreter
source code and a LEGO Mindstorms EV3 assembler written in Logo
and decorated by comments. I created a Logo syntax highlighter for
gtksourceview too.

Origin: https://github.com/mindboards/ev3sources/tree/master/lms2012/lmssrc/adk/lmsasm

License: same as origin (unknown)

Files
-----

* assembler.logo - an assembler core
* fileread.logo  - an assembler c preprocessor
* startup.logo   - an assembler loader
* logo.lang - a gtksourceview Logo language syntax highlighting
* README.md - this readme
* original/assembler.jar     - the original LEGO Logo interpreter
* original/assembler.src.jar - the original LEGO Logo interpreter decompiled by Fernflower
* deasm/ - directory with the modified Logo interpreter
* deasm/META-INF/ - directory for manifest
* deasm/src/ - directory with sources
* deasm/build.xml - ant build file
* deasm/build.properties - ant build properties (empty)
