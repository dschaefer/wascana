# Wascana C/C++ Tools for Eclipse

Wascana is a collection of Eclipse CDT extensions and toolchains for common open platforms.
Immediate plans are to support the Minimalist GNU for Windows (MinGW), Arduino, and Raspberry Pi.
Other platforms may be added in the future as the community shows interest.

The common them is to provide a one stop shop and a single point of install that includes both
the CDT extensions, but also the toolchains as well, everything needed to build applications
for those platforms.

Wascana supports the three major platforms as hosts: Windows, Mac, and Linux.
To simplify the build environment, MSYS is used as a shell environment for make on Windows.

## Status

Wascana is still in development. Current focus is to get the Arduino environment ready for the
early December 2014 (just in time for Christmas :))

MinGW is still under investigation. Both MinGW and MSYS from mingw.org is suffering major
quality problems as of late and the compiler doesn't support 64-bit. I've looked at the
MinGW distribution from Equation.com, which could be a good alternative. I've recently been
made aware of the MSYS2 project which provides good support for MinGW-64 (which also does
32-bit). My current plan is to go that route.

Raspberry Pi is very new. I just got one. I'll need to build my own toolchain for it,
which is a pretty big job. I also need to decide how to handle the different Linux distros
for the Pi, or just focus on Raspbian. We need to at least include the header files and libraries
for libc. My current thinking is to promote Qt for this environment.

Speaking of Qt, I'd like it to be the primary programming environment for use with MinGW and
the Raspberry Pi. Need to decide whether I should prepackage Qt binaries or just provide
instructions for users to build it themselves from source.