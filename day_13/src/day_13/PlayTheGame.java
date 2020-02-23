package day_13;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import aoc2019.IntCodeComputer;
import aoc2019.IntCodeComputer.Status;

public class PlayTheGame {

    private static final char[] TILES = new char[] { ' ', '#', '$', '-', 'o' };
    private static final int PADDLE = 3;
    private static final int BALL = 4;
    
    private IntCodeComputer computer;
    private char[][] gameBoard;
    private int[] ballPosition = new int[2];
    private int[] prevBallPosition = new int[2];
    private int paddlePosition;
    
    public PlayTheGame(Long[] program) {
        program[0] = 2L;
        computer = new IntCodeComputer(program, new LinkedList<>(), new LinkedList<>());
    }
    
    public void play() {
        if (fetchTheGrid() == false) {
            System.exit(1);
        }

        prevBallPosition[0] = ballPosition[0];
        prevBallPosition[1] = ballPosition[1];
        
        displayGameBoard();
        
        gameOn();
    }
    
    private boolean fetchTheGrid() {
        // Fetch the grid setup
        System.out.println("Starting up and fetching grid setup");
        Status status = computer.execute();
        System.out.println("Computer status: " + status);
        
        if (status == Status.FAILURE) {
            System.out.println("Something went wrong - exiting");
            return false;
        } else if (status == Status.SUCCESS) {
            System.out.println("Odd - computer should be waiting for joystick input");
            return false;
        }
        
        Queue<Long> outputQueue = computer.getOutputQueue();
        
        if (outputQueue.size() %3 != 0) {
            System.out.println(outputQueue.size() + " elements on the queue is not a multiple of 3 - something went wrong");
            return false;
        }

        int maxCol = 0;
        int maxRow = 0;
        Map<String, Character> tileMap = new HashMap<>();
        
        while (outputQueue.isEmpty() == false) {
            // Only process grid stuff for the moment - stop when we hit score content
            if (outputQueue.peek().intValue() == -1) {
                break;
            }
            
            // Data values are in groups of 3
            int x = outputQueue.remove().intValue();
            int y = outputQueue.remove().intValue();
            int tileId = outputQueue.remove().intValue();

            if (tileId == BALL) {
                // Set ball position
                ballPosition[0] = x;
                ballPosition[1] = y;
            } else if (tileId == PADDLE) {
                // Set paddle position
                paddlePosition = x;
            }
            
            if (x > maxCol) {
                maxCol = x;
            }
            
            if (y > maxRow) {
                maxRow = y;
            }
            
//            System.out.println(formatInstruction(x, y, tileId));
            tileMap.put(String.format("%d,%d", x, y), TILES[tileId]);
        }

        gameBoard = new char[maxRow + 1][maxCol + 1];
        tileMap.forEach((k, v) -> {
            String[] split = k.split(",");
            int x = Integer.valueOf(split[0]);
            int y = Integer.valueOf(split[1]);
            gameBoard[y][x] = v;
        });
        
        return true;
    }

    private String formatInstruction(int x, int y, int tileId) {
        return String.format("[%d, %d, %d]", x, y, tileId);
    }
    
    private void displayGameBoard() {
        for (int row = 0; row < gameBoard.length; row++) {
            for (int col = 0; col < gameBoard[row].length; col++) {
                System.out.print(gameBoard[row][col]);
            }
            
            System.out.println();
        }
    }

    private void gameOn() {
        Status status = Status.FAILURE;
        Queue<Long> outputQueue = computer.getOutputQueue();
        
        do {
            processOutputQueue(outputQueue);
            int paddleMovement = movePaddle();
            computer.inputValue(paddleMovement);
            status = computer.execute();
        } while (status != Status.SUCCESS);

        System.out.println("Game finished running");
        processOutputQueue(outputQueue);
    }

    private void processOutputQueue(Queue<Long> queue) {
        while (queue.isEmpty() == false) {
            // Data values are in groups of 3
            int x = queue.remove().intValue();
            int y = queue.remove().intValue();
            int tileId = queue.remove().intValue();
            
            if (x == -1) {
                if (y == 0) {
                    // This is a 'score' instruction
                    System.out.println("Current score: " + tileId);
                } else {
                    System.out.println("Unknown instruction: " + formatInstruction(x, y, tileId));
                }
            } else {
                gameBoard[y][x] = TILES[tileId];
                
                if (tileId == PADDLE) {
                    // Update paddle position
                    paddlePosition = x;
                } else if (tileId == BALL) {
                    // Update ball position
                    prevBallPosition[0] = ballPosition[0];
                    prevBallPosition[1] = ballPosition[1];
                    ballPosition[0] = x;
                    ballPosition[1] = y;
                }
            }
        }
    }

    private int movePaddle() {
//        System.out.println(String.format("ball: %d,%d - paddle %d", ballPosition[0], ballPosition[1], paddlePosition));
        if (paddlePosition < ballPosition[0]) {
            return 1;
        } else if (paddlePosition > ballPosition[0]) {
            return -1;
        } else {
            return 0;
        }
    }
}
