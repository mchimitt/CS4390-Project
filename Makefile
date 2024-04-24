# Define the compiler
JAVAC = javac

# Define the source files and their corresponding class files
CLIENT_SRC = Client.java
SERVER_SRC = Server.java

CLIENT_CLASSES = $(CLIENT_SRC:.java=.class)
SERVER_CLASSES = $(SERVER_SRC:.java=.class)

# Define the main target (default target)
all: $(CLIENT_CLASSES) $(SERVER_CLASSES)

# Compile client classes
$(CLIENT_CLASSES): $(CLIENT_SRC)
	$(JAVAC) $<

# Compile server classes
$(SERVER_CLASSES): $(SERVER_SRC)
	$(JAVAC) $<

# Clean compiled files
clean:
	rm -f $(CLIENT_CLASSES) $(SERVER_CLASSES)
