.PHONY: lint run

game:
	go build -o game "Game of Life.go"

lint:
	golangci-lint run

run: game
	./game
