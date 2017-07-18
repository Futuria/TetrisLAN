
import java.util.ArrayList;
/**
 * @author Nathan Lo, Joey Ma
 * @version June 15, 2017
 */
public class TRandom {
    private static ArrayList<Integer> tetrisList, pentaList;

    static{
        tetrisList = new ArrayList<Integer>();
        pentaList = new ArrayList<Integer>();
    }

    private static void resetTetris() {
        for (int i = Piece.TETRIS_START; i < Piece.TETRIS_END; i++) tetrisList.add(i);
        java.util.Collections.shuffle(tetrisList);
    }

    private static void resetPenta() {
        for (int i = Piece.PENTA_START; i < Piece.PENTA_END; i++) pentaList.add(i);
        java.util.Collections.shuffle(pentaList);
    }

    public static int getTetris() {
        if(tetrisList.isEmpty()) resetTetris();
        int res = tetrisList.get(0);
        tetrisList.remove(0);
        return res;
    }

    public static int getPenta() {
        if(pentaList.isEmpty()) resetPenta();
        int res = pentaList.get(0);
        pentaList.remove(0);
        return res;
    }
}
