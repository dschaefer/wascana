# Wascana C/C++ Tools for Eclipse

Wascana is a collection of Eclipse CDT extensions and toolchains for common open platforms.
Immediate plans are to support the Arduino and Raspberry Pi.
Other platforms may be added in the future as the community shows interest or if they
become so popular it would help grow the use of CDT by supporting them.

The common theme is to provide a one stop shop and a single point of install that includes both
the CDT extensions, but also the toolchains as well, everything needed to build applications
for those platforms.

Wascana supports the three major platforms as hosts: Windows, Mac, and Linux.

## Status

Wascana is still in development. Current focus is to get the Arduino environment ready for the
CDT 8.6 release in February. I'm still trying to get a beta out for the holiday season, but
it looks like I'll be using the holiday season to make some progress.

Raspberry Pi is very new. I just got one. I'll need to build my own toolchain for it,
which is a pretty big job. I also need to decide how to handle the different Linux distros
for the Pi, or just focus on Raspbian. We need to at least include the header files and libraries
for libc. My current thinking is to promote Qt for this environment.

Speaking of Qt, I'd like it to be the primary programming environment for use with the Raspberry Pi.
Need to decide whether I should prepackage Qt binaries or just provide
instructions for users to build it themselves from source.

Wascana was started to support the MinGW environment on Windows in a similar way. However,
I think it's better to focus my time on the hobbyist embedded market, a natural home for CDT.
The Qt project redistributes a mingw environment so as part of our CDT work to support Qt,
users can use that so Wascana distribution of the toolchain won't be necessary.
