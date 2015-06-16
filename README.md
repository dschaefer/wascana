# Wascana C/C++ Toolchains for Eclipse CDT

Wascana is a collection of Eclipse features that install toolchains for use
with the Eclipse CDT. It exists mainly because the rules at Eclipse prevent
us from including these toolchains with the CDT as they tend to be GPL.
To help users set up their complete IDE environment, we provide them here
and contribute them to the Eclipse Marketplace. Users can the one click
install the plugins from CDT along with the toolchains so that those plugins
can do their thing.

The main focus is providing environments for hobbyists, especially those using
low cost CPU boards.
The first candidate is Arduino.
Also on the roadmap is Raspberry Pi (ARMv6) and ESP8266.
ARMv7 boards, Raspberry PI 2 (once the community standardizes on a Linux
distro with this CPU architecture) and Beagleboard are also interesting.

A key contribution for Wascana are scripts that build the toolchains and the
Maven POMs that bundle them into Eclipse features. We support the three
major platforms: Windows, Linux, and Mac. Linux comes in both 32-bit and
64-bit varieties since 64-bit distros tend not to support 32-bit executables
very well. Windows is 32-bit since it's good at that. And Mac is 64-bit since
there are no longer any 32-bit Mac OS X releases.

BTW, in the past, Wascana used to focus on the MinGW toolchain. While that's
still a goal, it has been put on the backburner. The original MinGW.org has
somewhat stagnated. The newer MinGW-64 project which is much more up to date
and has a 64-bit compiler, doesn't have an official distribution. That makes
it much more work. And with the free Visual Studio being a much more complete
environment, I'd rather focus on helping get proper Visual C++ support into
the CDT.
