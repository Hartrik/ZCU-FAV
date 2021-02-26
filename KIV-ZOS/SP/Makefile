CC = gcc
TARGET = ntfs-dist
FLAGS = -lpthread -Wall -std=c99 -D_GNU_SOURCE

SRCDIR = src/main/c
BINDIR = build
SOURCES := $(wildcard $(SRCDIR)/*.c)
HEADERS := $(wildcard $(SRCDIR)/*.h)

server: clean $(SOURCES) $(HEADERS)
	-mkdir $(BINDIR)
	$(CC) $(SOURCES) -o $(BINDIR)/$(TARGET) $(FLAGS)

clean:
	-rm -rf $(BINDIR)
