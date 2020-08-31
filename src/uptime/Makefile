CFLAGS += -I/usr/lib/jvm/java-8-openjdk/include/ -I/usr/lib/jvm/java-8-openjdk/include/linux -I. -fpic -c
LDFLAGS += -shared

SRCS = uptime.c
OBJS = $(SRCS:.c=.o)

libuptime.so: $(OBJS)
	$(CC) ${LDFLAGS} -o $@ $^

.PHONY: clean
clean:
	- rm -f libuptime.so ${OBJS} $(SRCS:.c=.d)
