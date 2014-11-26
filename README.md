# Wascana C/C++ Tools for Eclipse

Wascana is a collection of Eclipse CDT extensions and toolchains for common open platforms.
Immediate plans are to support Arduino, and Raspberry Pi. Other platforms may be added in
the future as the community shows interest.

The common theme is to provide a one stop shop and a single point of install that includes both
the CDT extensions, but also the toolchains as well, everything needed to build applications
for those platforms.

Wascana supports the three major platforms as hosts: Windows, Mac, and Linux.

## Status

Wascana is still in development. Current focus is to get the Arduino environment ready for the
December 2014 (just in time for Christmas :))

Raspberry Pi is very new to me. I just got one. I'll need to build my own toolchain for it,
which is a pretty big job. I also need to decide how to handle the different Linux distros
for the Pi, or just focus on Raspbian. We need to at least include the header files and libraries
for libc. My current thinking is to promote Qt for this environment.

Speaking of Qt, I'd like it to be the primary programming environment for use with MinGW and
the Raspberry Pi. Need to decide whether I should prepackage Qt binaries or just provide
instructions for users to build it themselves from source.

## MinGW

I had originally planned on distributing an environment for Windows programming using the
MinGW toolchain. However, Microsoft just released the full Visual Studio for free for hobbyists.
That kinda removes my incentive to do that.

What I will do though is support the MinGW toolchain distributed by the Qt project. It provides
a decent enviornment for prototyping Qt programs that will eventually run on embedded hardware
like the Rasberry Pi and QNX. But since I don't have to redistribute the toolchain I can do
that all upstream in the CDT itself.
