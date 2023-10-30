package main

import (
	"fmt"
	"math"
	"math/rand"
	"os"
	"strings"

	"github.com/hajimehoshi/ebiten"
	"github.com/hajimehoshi/ebiten/inpututil"
)

type LifeGame struct {
	board         *Board
	visibleX      int
	visibleY      int
	time          int
	colourPeriod  int
	trailLength   int
	pixelBuffer   []byte
	wallThickness int
	paused        bool
	speed         float64
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
	// TODO: Add a CLI for all of these values
	boardX := 300
	boardY := 300
	scale := 3
	colourPeriod := 30
	trailLength := 10
	wallThickness := 200 // the very edges of the board are dead, but you can specify a buffer zone between the visible area and the boundary
	speed := 1
	random := false
	filepath := "../Life Files/3enginecordershipgun.cells.txt"

	paused := (filepath == "" && !random) // start in "editor mode" if not randomised and not loading a file
	lg, err := initLifeGame(boardX, boardY, colourPeriod, trailLength, wallThickness, paused, float64(speed))
	if err != nil {
		fmt.Printf("Error in init: %v", err)
		os.Exit(1)
	}

	if filepath != "" {
		err := lg.loadFile(filepath)
		if err != nil {
			fmt.Printf("Error in file loading: %v", err)
			os.Exit(1)
		}
	} else if random {
		lg.randomise()
	}

	x := int(float64(boardX) * float64(scale))
	y := int(float64(boardY) * float64(scale))
	ebiten.SetWindowSize(x, y)
	ebiten.SetWindowTitle("Game of Life")
	ebiten.SetWindowResizable(true)
	ebiten.SetMaxTPS(int(60 * lg.speed))
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

func initLifeGame(x, y, colourPeriod, trailLength, wallThickness int, paused bool, speed float64) (*LifeGame, error) {
	lg := LifeGame{
		board:         newBoard(x+2*wallThickness, y+2*wallThickness, wallThickness),
		visibleX:      x,
		visibleY:      y,
		pixelBuffer:   make([]byte, x*y*4),
		colourPeriod:  colourPeriod,
		trailLength:   trailLength,
		wallThickness: wallThickness,
		paused:        paused,
		speed:         speed,
	}

	return &lg, nil
}

func (lg *LifeGame) Update(screen *ebiten.Image) error {
	if inpututil.IsKeyJustPressed(ebiten.KeySpace) {
		lg.paused = !lg.paused
	}
	if !lg.paused {
		lg.board.gameTick()
		lg.time++
	} else {
		if ebiten.IsMouseButtonPressed(ebiten.MouseButtonLeft) {
			x, y := ebiten.CursorPosition()
			lg.board.get(x+lg.wallThickness, y+lg.wallThickness).on = true
		}
		if ebiten.IsMouseButtonPressed(ebiten.MouseButtonRight) {
			x, y := ebiten.CursorPosition()
			lg.board.get(x+lg.wallThickness, y+lg.wallThickness).on = false
		}
	}

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
	return lg.visibleX, lg.visibleY
}

func (lg *LifeGame) randomise() {
	for x := lg.wallThickness; x < lg.board.sizeX-lg.wallThickness-1; x++ { //ignore walls, they are always dead
		for y := lg.wallThickness; y < lg.board.sizeY-lg.wallThickness-1; y++ {
			lg.board.get(x, y).on = (rand.Int()%2 == 0)
		}
	}
}

func (lg *LifeGame) loadFile(filePath string) error {
	fileBytes, err := os.ReadFile(filePath)
	if err != nil {
		return fmt.Errorf("error opening file: %w", err)
	}
	fileLines := strings.Split(string(fileBytes), "\n")
	var description string
	var fileX, fileY int
	// extract description, and count the number of rows and columns
	for _, line := range fileLines {
		if len(line) == 0 || string(line[0]) == "!" {
			if len(line) != 0 {
				description += line + "\n"
			}
		} else {
			fileY += 1
			if len(line) > fileX {
				fileX = len(line)
			}
		}
	}
	if fileX > lg.visibleX {
		return fmt.Errorf("please specify a board width greater than %d", fileX)
	}
	if fileY > lg.visibleX {
		return fmt.Errorf("please specify a board height greater than %d", fileY)
	}
	fmt.Println(description)
	startX := lg.board.sizeX/2 - fileX/2
	startY := lg.board.sizeY/2 - fileY/2
	x := startX
	y := startY
	for _, line := range fileLines {
		if len(line) == 0 || string(line[0]) != "!" {
			for _, letter := range line {
				if string(letter) == "O" {
					lg.board.get(x, y).on = true
				}
				x += 1
			}
			x = startX
			y += 1
		}
	}

	return nil
}

func newBoard(sizeX, sizeY, wallThickness int) *Board {
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
			isBoundary: (x < wallThickness || x > sizeX-wallThickness-1 || y < wallThickness || y > sizeY-wallThickness-1),
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
	for x := 1; x < b.sizeX-1; x++ { //ignore walls, they are always dead
		for y := 1; y < b.sizeY-1; y++ {
			if b.get(x, y).on {
				for _, offset := range b.neighbourList {
					b.neighbours[(x+offset[0])+b.sizeX*(y+offset[1])]++
				}
			}
		}
	}
	// read neighbours, update board state
	for x := 1; x < b.sizeX-1; x++ { //ignore walls, they are always dead
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
