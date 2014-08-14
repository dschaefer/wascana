VERSION = 156

BOARD ?= uno
OUTPUT_DIR ?= build/Default

rwildcard = $(foreach d,$(wildcard $1*),$(call rwildcard,$d/,$2) $(filter $(subst *,%,$2),$d))

ifeq ($(BOARD),uno)
ARCH = avr
CORE = arduino
VARIANT = standard
MCU = atmega328p
F_CPU = 16000000L
BOARD = AVR_UNO
endif

ifeq ($(ARCH),avr)
CXXFLAGS = -g -Os -w -fno-exceptions -ffunction-sections -fdata-sections -MMD \
	-mmcu=$(MCU) -DF_CPU=$(F_CPU) -DARDUINO=$(VERSION) -DARDUINO_$(BOARD) -DARDUINO_ARCH_AVR $(INCLUDES)
CFLAGS = -g -Os -w -ffunction-sections -fdata-sections -MMD \
	-mmcu=$(MCU) -DF_CPU=$(F_CPU) -DARDUINO=156 -DARDUINO_$(BOARD) -DARDUINO_ARCH_AVR $(INCLUDES)

CXX = avr-g++
CC = avr-gcc
AR = avr-ar
OBJCOPY = avr-objcopy

define do_link
$(CC) -Os -Wl,--gc-sections -mmcu=$(MCU) -o $(OUTPUT_DIR)/$(EXE).elf $^
avr-objcopy -O ihex -R .eeprom $(OUTPUT_DIR)/$(EXE).elf $(OUTPUT_DIR)/$(EXE).hex
$(do_link_extra)
avr-size $(OUTPUT_DIR)/$(EXE).elf
endef

define do_eeprom
avr-objcopy -O ihex -j .eeprom --set-section-flags=.eeprom=alloc,load \
	--no-change-warnings --change-section-lma .eeprom=0 \
	$(OUTPUT_DIR)/$(EXE).elf  $(OUTPUT_DIR)/$(EXE).eep
endef

define do_load_avrdude
@echo avrdude blah blah blah 
endef

endif # ARCH = avr

INCLUDES = -I$(ARDUINO_HOME)/hardware/arduino/$(ARCH)/cores/$(CORE) \
           -I$(ARDUINO_HOME)/hardware/arduino/$(ARCH)/variants/$(VARIANT)

LIB_ROOT = $(ARDUINO_HOME)/hardware/arduino/$(ARCH)/cores/$(CORE)

LIB_SRCS = $(call rwildcard, $(LIB_ROOT)/, *.c *.cpp)

LIB_OBJS = $(patsubst $(LIB_ROOT)/%.c, $(OUTPUT_DIR)/arduino/%.o, $(filter %.c, $(LIB_SRCS))) \
           $(patsubst $(LIB_ROOT)/%.cpp, $(OUTPUT_DIR)/arduino/%.o, $(filter %.cpp, $(LIB_SRCS)))

SRCS = $(call rwildcard, ./, *.c *.cpp)

OBJS = $(patsubst %.cpp, $(OUTPUT_DIR)/%.o, $(filter %.cpp, $(SRCS))) \
       $(patsubst %.c, $(OUTPUT_DIR)/%.o, $(filter %.c, $(SRCS)))

all:	$(OUTPUT_DIR)/$(EXE).hex

clean:
	rm -fr $(OUTPUT_DIR)

load:	#$(OUTPUT_DIR)/$(EXE).hex
	$(do_load_$(LOADER))

$(OUTPUT_DIR)/$(EXE).hex:	$(OBJS) $(OUTPUT_DIR)/core.a
	$(do_link)

$(OUTPUT_DIR)/core.a: $(LIB_OBJS)
	$(AR) r $@ $?

$(OUTPUT_DIR)/arduino/%.o: $(LIB_ROOT)/%.c
	@mkdir -p $(dir $@)
	$(CC) -c $(CFLAGS) $(CPPFLAGS) -o $@ $< 

$(OUTPUT_DIR)/arduino/%.o: $(LIB_ROOT)/%.cpp
	@mkdir -p $(dir $@)
	$(CXX) -c $(CXXFLAGS) $(CPPFLAGS) -o $@ $< 

$(OUTPUT_DIR)/%.o: %.c
	@mkdir -p $(dir $@)
	$(CC) -c $(CFLAGS) $(CPPFLAGS) -o $@ $< 

$(OUTPUT_DIR)/%.o: %.cpp
	@mkdir -p $(dir $@)
	$(CXX) -c $(CXXFLAGS) $(CPPFLAGS) -o $@ $< 
