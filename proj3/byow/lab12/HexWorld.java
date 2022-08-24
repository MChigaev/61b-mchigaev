package byow.lab12;
import edu.neu.ccs.util.Hex;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;
    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);
    public static void main(String[] args) {
        Hexagon test = new Hexagon(0, 0, 4);
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for(int w = 0; w < WIDTH; w++) {
            for(int h = 0; h < HEIGHT; h++) {
                world[w][h] = Tileset.NOTHING;
            }
        }
        int size = 2;
        int nextx = 25;
        int nexty = 25;
        int origx = 25;
        int origy = 25;
        for(int i = 0; i < 19; i++) {
            Hexagon hex = new Hexagon(nextx, nexty, size);
            TETile[][] relworld = hex.getRelativeworld();
            for(TETile[] z: relworld) {
                for(TETile j: z) {
                    System.out.println(j);
                }
            }
            for(int y = 0; y < relworld[0].length; y++) {
                for(int x = 0; x < relworld.length; x++) {
                    if(relworld[x][y] == Tileset.NOTHING) {
                        continue;
                    }
                    world[nextx+x][nexty+y] = relworld[x][y];
                }
            }
            if(i == 2) {
                nextx = origx;
                nexty = origy;
            }
            nextx = nextx + 2 * size - 1;
            nexty = nexty + size;
            ter.renderFrame(world);
        }
        ter.renderFrame(world);
    }
}

class Hexagon {
    int x;
    int y;
    int length;
    TETile[][] relativeworld;
    int totallen = 0;
    public Hexagon(int x, int y, int length) {
        this.x = x;
        this.y = y;
        this.length = length;
        this.relativeworld = new TETile[length + 2*(length-1)][2*length];
        int rowoffset = length - 1;
        totallen = length + 2 * rowoffset;
        int xcoord = 0;
        int ycoord = 0;
        while (rowoffset > -1) {
            xcoord = 0;
            for (int i = 0; i < rowoffset; i++) {
                relativeworld[xcoord][ycoord] = Tileset.NOTHING;
                xcoord++;
            }
            for(int i = 0; i < totallen-2*rowoffset; i++) {
                relativeworld[xcoord][ycoord] = Tileset.FLOWER;
                xcoord++;
            }

            for (int i = 0; i < rowoffset; i++) {
                relativeworld[xcoord][ycoord] = Tileset.NOTHING;
                xcoord++;
            }
            rowoffset--;
            ycoord++;
        }
        rowoffset++;
        while (rowoffset < length) {
            xcoord = 0;
            for (int i = 0; i < rowoffset; i++) {
                relativeworld[xcoord][ycoord] = Tileset.NOTHING;
                xcoord++;
            }
            for(int i = 0; i < totallen-2*rowoffset; i++) {
                relativeworld[xcoord][ycoord] = Tileset.FLOWER;
                xcoord++;
            }
            for (int i = 0; i < rowoffset; i++) {
                relativeworld[xcoord][ycoord] = Tileset.NOTHING;
                xcoord++;
            }
            rowoffset++;
            ycoord++;

        }
    }
    public TETile[][] getRelativeworld() {
        return this.relativeworld;
    }
    public int getX() {
        return this.x;
    }
    public int getY() {
        return this.y;
    }
    public int getTotallen() {
        return this.totallen;
    }
}

