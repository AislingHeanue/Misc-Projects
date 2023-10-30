package main

import (
	"fmt"
	"math"
	"math/rand"
	"os"

	"github.com/hajimehoshi/ebiten"
)

type LifeGame struct {
	board        *Board
	time         int
	colourPeriod int
	trailLength  int
	pixelBuffer  []byte
}

type Board struct {
	boxes         []Box
	neighbours    []int
	neighbourList [8][2]int
	sizeX         int
	sizeY         int
}

type Box struct {
	on         bool
	isBoundary bool
	timeDead   int
}

func main() {
	boardX := 500
	boardY := 500
	boxSize := 2
	colourPeriod := 30
	trailLength := 60
	x := boardX * boxSize
	y := boardY * boxSize

	lg, err := initLifeGame(boardX, boardY, colourPeriod, trailLength)
	if err != nil {
		fmt.Printf("Error in init: %v", err)
		os.Exit(1)
	}
	random := true // TODO: allow user to draw image, allow loading of life files
	if random {
		lg.randomise()
	}

	ebiten.SetWindowSize(x, y)
	ebiten.SetWindowTitle("Game of Life")
	err = ebiten.RunGame(lg)
	if err != nil {
		fmt.Printf("Error in game: %v", err)
		os.Exit(1)
	}
}

// returns a number normalised by a sin function to a range of 0 and 1. Used to add a rainbow colour effect
func colour(time int, period int, age int, trailLength int) (byte, byte, byte) {
	return byte(math.Min(255,
			((128 - 128/float64(trailLength)*float64(age)) * (1 + math.Sin(float64(time)/float64(period)))),
		)),
		byte(math.Min(255,
			((128 - 128/float64(trailLength)*float64(age)) * (1 + math.Sin((2*math.Pi/3)+float64(time)/float64(period)))),
		)),
		byte(math.Min(255,
			((128 - 128/float64(trailLength)*float64(age)) * (1 + math.Sin((4*math.Pi/3)+float64(time)/float64(period)))),
		))
}

func initLifeGame(x int, y int, colourPeriod int, trailLength int) (*LifeGame, error) {
	lg := LifeGame{
		board:        newBoard(x+2, y+2),
		pixelBuffer:  make([]byte, x*y*4),
		colourPeriod: colourPeriod,
		trailLength:  trailLength,
	}

	return &lg, nil
}

func (lg *LifeGame) Update(screen *ebiten.Image) error {
	lg.board.gameTick()
	lg.time++

	return nil
}

func (lg *LifeGame) Draw(screen *ebiten.Image) {
	lg.board.Draw(lg.pixelBuffer, lg.time, lg.colourPeriod, lg.trailLength)

	err := screen.ReplacePixels(lg.pixelBuffer)
	if err != nil {
		panic(err)
	}
}

func (lg *LifeGame) Layout(outsideWidth, outsideHeight int) (screenWidth, screenHeight int) {
	return lg.board.sizeX - 2, lg.board.sizeY - 2
}

func (lg *LifeGame) randomise() {
	for x := 1; x < lg.board.sizeX-1; x++ { //ignore walls, they are always dead (TODO: maybe make a larger wall size eg. 20 to simulate an endless board?)
		for y := 1; y < lg.board.sizeY-1; y++ {
			lg.board.get(x, y).on = (rand.Int()%2 == 0)
		}
	}
}

func newBoard(sizeX, sizeY int) *Board {
	b := Board{
		sizeX: sizeX,
		sizeY: sizeY,
		neighbourList: [8][2]int{
			{-1, -1}, {0, -1}, {+1, -1},
			{-1, 0}, {+1, 0},
			{-1, +1}, {0, +1}, {+1, +1},
		},
		boxes:      make([]Box, sizeX*sizeY),
		neighbours: make([]int, sizeX*sizeY), //do not track neighbours for boundary cells
	}
	for i := 0; i < sizeX*sizeY; i++ {
		x := i % sizeX
		y := i / sizeX // floor division
		b.boxes[i] = Box{
			on:         false,
			timeDead:   100,
			isBoundary: (x == 0 || x == sizeX-1 || y == 0 || y == sizeY-1),
		}
	}
	return &b
}

func (b *Board) get(x int, y int) *Box {

	return &b.boxes[x+b.sizeX*y] // x wide, y tall, index starts in top right at 0,0 (though the first row and column are dead space)
}

func (b *Board) Draw(buffer []byte, time int, period int, trailLength int) {
	index := 0
	for _, box := range b.boxes {
		if !box.isBoundary {
			if box.on {
				buffer[index+0] = 0xff
				buffer[index+1] = 0xff
				buffer[index+2] = 0xff
				buffer[index+3] = 0xff
				index += 4
			} else if box.timeDead != 0 && box.timeDead < trailLength {
				red, green, blue := colour(time-box.timeDead, period, box.timeDead, trailLength)
				buffer[index+0] = red
				buffer[index+1] = green
				buffer[index+2] = blue
				buffer[index+3] = 0
				index += 4
			} else {
				buffer[index+0] = 0
				buffer[index+1] = 0
				buffer[index+2] = 0
				buffer[index+3] = 0
				index += 4
			}
		}
	}
}

func (b *Board) gameTick() {
	// re-init neighbours
	for i := range b.neighbours {
		b.neighbours[i] = 0
	}
	for x := 1; x < b.sizeX-1; x++ { //ignore walls, they are always dead (TODO: maybe make a larger wall size eg. 20 to simulate an endless board?)
		for y := 1; y < b.sizeY-1; y++ {
			if b.get(x, y).on {
				for _, offset := range b.neighbourList {
					b.neighbours[(x+offset[0])+b.sizeX*(y+offset[1])]++
				}
			}
		}
	}
	// read neighbours, update board state
	for x := 1; x < b.sizeX-1; x++ { //ignore walls, they are always dead (TODO: maybe make a larger wall size eg. 20 to simulate an endless board?)
		for y := 1; y < b.sizeY-1; y++ {
			if b.neighbours[x+b.sizeX*y] == 3 {
				b.get(x, y).on = true
				b.get(x, y).timeDead = 0
			} else if b.neighbours[x+b.sizeX*y] == 2 && b.get(x, y).on {

			} else {
				b.get(x, y).on = false
				b.get(x, y).timeDead++
			}
		}
	}
}
