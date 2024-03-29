package model;

import music.MusicThread;
import view.GridType;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class store the real chess information.
 * The Chessboard has 9*7 cells, and each cell has a position for chess
 */
public class Chessboard {
    private Cell[][] grid;
    private final Set<ChessboardPoint> riverCell = new HashSet<>();
    private final Set<ChessboardPoint> trapCell = new HashSet<>();
    private final Set<ChessboardPoint> densCell = new HashSet<>();

    public Chessboard() {
        this.grid =
                new Cell[Constant.CHESSBOARD_ROW_SIZE.getNum()][Constant.CHESSBOARD_COL_SIZE.getNum()];//19X19

        initSets();
        initGrid();
        initPieces();
    }

    private void initSets() {
        riverCell.add(new ChessboardPoint(3,1));
        riverCell.add(new ChessboardPoint(3,2));
        riverCell.add(new ChessboardPoint(4,1));
        riverCell.add(new ChessboardPoint(4,2));
        riverCell.add(new ChessboardPoint(5,1));
        riverCell.add(new ChessboardPoint(5,2));

        riverCell.add(new ChessboardPoint(3,4));
        riverCell.add(new ChessboardPoint(3,5));
        riverCell.add(new ChessboardPoint(4,4));
        riverCell.add(new ChessboardPoint(4,5));
        riverCell.add(new ChessboardPoint(5,4));
        riverCell.add(new ChessboardPoint(5,5));

        trapCell.add(new ChessboardPoint(0,2));
        trapCell.add(new ChessboardPoint(0,4));
        trapCell.add(new ChessboardPoint(1,3));
        trapCell.add(new ChessboardPoint(7,3));
        trapCell.add(new ChessboardPoint(8,2));
        trapCell.add(new ChessboardPoint(8,4));

        densCell.add(new ChessboardPoint(0,3));
        densCell.add(new ChessboardPoint(8,3));
    }

    public void initGrid() {//初始化网格  河流/陷阱/巢穴
        for (int i = 0; i < Constant.CHESSBOARD_ROW_SIZE.getNum(); i++) {
            for (int j = 0; j < Constant.CHESSBOARD_COL_SIZE.getNum(); j++) {
                if (riverCell.contains(new ChessboardPoint(i, j))) {
                    grid[i][j] = new Cell(GridType.RIVER);
                } else if (trapCell.contains(new ChessboardPoint(i, j))) {
                    grid[i][j] = new Cell(GridType.TRAP);
                    if (i < 2) {
                        grid[i][j].setOwner(PlayerColor.RED);
                    } else {
                        grid[i][j].setOwner(PlayerColor.BLUE);
                    }
                } else if (densCell.contains(new ChessboardPoint(i, j))) {
                    grid[i][j] = new Cell(GridType.DENS);
                    if (i < 2) {
                        grid[i][j].setOwner(PlayerColor.RED);
                    } else {
                        grid[i][j].setOwner(PlayerColor.BLUE);
                    }
                } else {
                    grid[i][j] = new Cell(GridType.LAND);
                }
            }
        }
    }

    public void initPieces() {
        // 清空棋盘
        for (int i = 0; i < Constant.CHESSBOARD_ROW_SIZE.getNum(); i++) {
            for (int j = 0; j < Constant.CHESSBOARD_COL_SIZE.getNum(); j++) {
                grid[i][j].removePiece();
            }
        }
        grid[0][0].setPiece(new ChessPiece(PlayerColor.RED, "Lion",7));
        grid[0][6].setPiece(new ChessPiece(PlayerColor.RED, "Tiger",6));
        grid[1][1].setPiece(new ChessPiece(PlayerColor.RED, "Dog",3));
        grid[1][5].setPiece(new ChessPiece(PlayerColor.RED, "Cat",2));
        grid[2][0].setPiece(new ChessPiece(PlayerColor.RED, "Rat",1));
        grid[2][2].setPiece(new ChessPiece(PlayerColor.RED, "Leopard",5));
        grid[2][4].setPiece(new ChessPiece(PlayerColor.RED, "Wolf",4));
        grid[2][6].setPiece(new ChessPiece(PlayerColor.RED, "Elephant",8));

        grid[6][0].setPiece(new ChessPiece(PlayerColor.BLUE, "Elephant",8));
        grid[6][2].setPiece(new ChessPiece(PlayerColor.BLUE, "Wolf",4));
        grid[6][4].setPiece(new ChessPiece(PlayerColor.BLUE, "Leopard",5));
        grid[6][6].setPiece(new ChessPiece(PlayerColor.BLUE, "Rat",1));
        grid[7][1].setPiece(new ChessPiece(PlayerColor.BLUE, "Cat",2));
        grid[7][5].setPiece(new ChessPiece(PlayerColor.BLUE, "Dog",3));
        grid[8][0].setPiece(new ChessPiece(PlayerColor.BLUE, "Tiger",6));
        grid[8][6].setPiece(new ChessPiece(PlayerColor.BLUE, "Lion",7));
    }
    //获得当前点的棋子对象
    private ChessPiece getChessPieceAt(ChessboardPoint point) {
        return getGridAt(point).getPiece();
    }
    //获得当前点的cell容器
    private Cell getGridAt(ChessboardPoint point) {
        return grid[point.getRow()][point.getCol()];
    }

    private int calculateDistance(ChessboardPoint src, ChessboardPoint dest) {
        return Math.abs(src.getRow() - dest.getRow()) + Math.abs(src.getCol() - dest.getCol());
    }
    //清除棋子
    private ChessPiece removeChessPiece(ChessboardPoint point) {
        ChessPiece chessPiece = getChessPieceAt(point);
        getGridAt(point).removePiece();
        return chessPiece;
    }

    private void setChessPiece(ChessboardPoint point, ChessPiece chessPiece) {
        getGridAt(point).setPiece(chessPiece);
    }
    //棋子移动
    public void moveChessPiece(ChessboardPoint src, ChessboardPoint dest) {
        if (!isValidMove(src, dest)) {
            throw new IllegalArgumentException("Illegal chess move!");
        }
        setChessPiece(dest, removeChessPiece(src));
    }
    //旗子捕获
    public void captureChessPiece(ChessboardPoint src, ChessboardPoint dest) {
        removeChessPiece(dest);
        setChessPiece(dest, removeChessPiece(src));
    }

    public Cell[][] getGrid() {
        return grid;
    }
    public PlayerColor getChessPieceOwner(ChessboardPoint point) {
        return getGridAt(point).getPiece().getOwner();
    }

    public boolean isValidMove(ChessboardPoint src, ChessboardPoint dest) {
        if (getChessPieceAt(src) == null || getChessPieceAt(dest) != null) {
            return false;
        }
        // 老鼠能游泳
        if (getGridAt(dest).getType() == GridType.RIVER) {
            if (getChessPieceAt(src).getName() != "Rat") {
                return false;
            } else {
                return calculateDistance(src, dest) == 1;
            }
        }
        // 狮子和虎可以跳过河
        if (calculateDistance(src, dest) > 1
            && (getChessPieceAt(src).getName() == "Lion" || getChessPieceAt(src).getName() == "Tiger")) {
            // 检查两个格子是否在同一行或者同一列
            if (src.getRow() != dest.getRow() && src.getCol() != dest.getCol()) {
                return false;
            }
            // 检查两个格子之间是否全为RIVER，如果是，则可以移动，否则不可以移动
            if (src.getRow() == dest.getRow()) {
                int step = src.getCol() < dest.getCol() ? 1 : -1;
                int col = src.getCol() + step;
                while (col != dest.getCol()) {
                    if (getGridAt(new ChessboardPoint(src.getRow(), col)).getType() != GridType.RIVER) {
                        return false;
                    }
                    // 检查河流格子上是否有棋子
                    if (getChessPieceAt(new ChessboardPoint(src.getRow(), col)) != null) {
                        return false;
                    }
                    col += step;
                }
                return true;
            }
            if (src.getCol() == dest.getCol()) {
                int step = src.getRow() < dest.getRow() ? 1 : -1;
                int row = src.getRow() + step;
                while (row != dest.getRow()) {
                    if (getGridAt(new ChessboardPoint(row, src.getCol())).getType() != GridType.RIVER) {
                        return false;
                    }
                    // 检查河流格子上是否有棋子
                    if (getChessPieceAt(new ChessboardPoint(row, src.getCol())) != null) {
                        return false;
                    }
                    row += step;
                }
                return true;
            }
        }
        // 不能走到自己的巢穴里
        if (getGridAt(dest).getType() == GridType.DENS && getGridAt(dest).getOwner() == getChessPieceAt(src).getOwner()) {
            return false;
        }

        return calculateDistance(src, dest) == 1;
    }

    public boolean isValidCapture(ChessboardPoint src, ChessboardPoint dest) {
        ChessPiece srcPiece = getChessPieceAt(src);
        ChessPiece destPiece = getChessPieceAt(dest);
        if (srcPiece == null || destPiece == null) {
            return false;
        }
        if (srcPiece.getOwner() == destPiece.getOwner()) {
            return false;
        }
        // 在河里的老鼠不能被捕获
        if (getGridAt(dest).getType() == GridType.RIVER) {
            return false;
        }
        // 在河里的老鼠不能捕获大象
        if (getGridAt(src).getType() == GridType.RIVER) {
            return false;
        }
        // 狮子和虎可以跳过河来捕获
        if (calculateDistance(src, dest) > 1
            && (srcPiece.getName() == "Lion" || srcPiece.getName() == "Tiger")) {
            // 检查两个格子是否在同一行或者同一列
            if (src.getRow() != dest.getRow() && src.getCol() != dest.getCol()) {
                return false;
            }
            // 检查两个格子之间是否全为RIVER，如果是，则可以移动，否则不可以移动
            if (src.getRow() == dest.getRow()) {
                int step = src.getCol() < dest.getCol() ? 1 : -1;
                int col = src.getCol() + step;
                while (col != dest.getCol()) {
                    if (getGridAt(new ChessboardPoint(src.getRow(), col)).getType() != GridType.RIVER) {
                        return false;
                    }
                    // 检查河流格子上是否有棋子
                    if (getChessPieceAt(new ChessboardPoint(src.getRow(), col)) != null) {
                        return false;
                    }
                    col += step;
                }
                return srcPiece.canCapture(destPiece);
            }
            if (src.getCol() == dest.getCol()) {
                int step = src.getRow() < dest.getRow() ? 1 : -1;
                int row = src.getRow() + step;
                while (row != dest.getRow()) {
                    if (getGridAt(new ChessboardPoint(row, src.getCol())).getType() != GridType.RIVER) {
                        return false;
                    }
                    // 检查河流格子上是否有棋子
                    if (getChessPieceAt(new ChessboardPoint(row, src.getCol())) != null) {
                        return false;
                    }
                    row += step;
                }
                return srcPiece.canCapture(destPiece);
            }
        }
        return calculateDistance(src, dest) == 1 && srcPiece.canCapture(destPiece);
    }

    public void solveTrap(ChessboardPoint selectedPoint, ChessboardPoint destPoint) {
        //对于进入陷阱与出陷阱的处理
        if (getGridAt(destPoint).getType() == GridType.TRAP && getGridAt(destPoint).getOwner() != getChessPieceAt(selectedPoint).getOwner()) {
            getTrapped(selectedPoint);
        } else if (getGridAt(selectedPoint).getType() == GridType.TRAP && getGridAt(selectedPoint).getOwner() != getChessPieceAt(selectedPoint).getOwner()) {
            exitTrap(selectedPoint);
        }
    }

    public boolean solveDens(ChessboardPoint destPoint) {
        if (getGridAt(destPoint).getType() == GridType.DENS) {
            return true;
        }
        return false;
    }

    public boolean checkAnnihilate(PlayerColor currentPlayer){
        // 检查是否还有敌方棋子
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 7; j++) {
                ChessboardPoint point = new ChessboardPoint(i, j);
                if (getChessPieceAt(point) != null && getChessPieceAt(point).getOwner() != currentPlayer) {
                    // 检查敌方棋子周围是否全是己方等级更高的棋子
                    for (int k = 0; k < 4; k++) {
                        ChessboardPoint neighbor = point.getNeighbor(k);
                        if (neighbor.getRow()<0 || neighbor.getRow()>8 || neighbor.getCol()<0 || neighbor.getCol()>6) {
                            continue;
                        }
                        if (getChessPieceAt(neighbor) == null || (getChessPieceAt(neighbor).getOwner() == currentPlayer
                            && getChessPieceAt(neighbor).getRank() < getChessPieceAt(point).getRank())) {
                            return false;
                        }
                    }
                }
            }
        }
        System.out.println("Annihilate!");
        return true;
    }

    public void getTrapped(ChessboardPoint point) {
        getGridAt(point).getPiece().setRank(0);
    }

    public void exitTrap(ChessboardPoint point) {
        switch (getGridAt(point).getPiece().getName()) {
            case "Rat":
                getGridAt(point).getPiece().setRank(1);
                break;
            case "Cat":
                getGridAt(point).getPiece().setRank(2);
                break;
            case "Dog":
                getGridAt(point).getPiece().setRank(3);
                break;
            case "Wolf":
                getGridAt(point).getPiece().setRank(4);
                break;
            case "Leopard":
                getGridAt(point).getPiece().setRank(5);
                break;
            case "Tiger":
                getGridAt(point).getPiece().setRank(6);
                break;
            case "Lion":
                getGridAt(point).getPiece().setRank(7);
                break;
            case "Elephant":
                getGridAt(point).getPiece().setRank(8);
                break;
        }
    }
    //step用于记录棋子回合内行为
    public Step recordStep(ChessboardPoint fromPoint, ChessboardPoint toPoint, PlayerColor currentPlayer, int turn){
        ChessPiece fromPiece = getChessPieceAt(fromPoint);
        ChessPiece toPiece = getChessPieceAt(toPoint);
        Step step = new Step(fromPoint, toPoint, fromPiece, toPiece, currentPlayer, turn);
//        System.out.println(step);
        return step;
    }
    //悔棋返回上一步
    public void undoStep(Step step){
        ChessboardPoint fromPoint = step.getFrom();
        ChessboardPoint toPoint = step.getTo();
        ChessPiece fromPiece = step.getFromChessPiece();
        ChessPiece toPiece = step.getToChessPiece();
        if (toPiece != null) {
            setChessPiece(toPoint, toPiece);
        } else {
            setChessPiece(toPoint, null);
        }
        setChessPiece(fromPoint, fromPiece);
    }
    //用于执行step中存储的行为
    public void runStep(Step step){
        ChessboardPoint fromPoint = step.getFrom();
        ChessboardPoint toPoint = step.getTo();
        ChessPiece fromPiece = step.getFromChessPiece();
        setChessPiece(fromPoint, null);
        setChessPiece(toPoint, fromPiece);
    }

    public List<ChessboardPoint> getValidMoves(ChessboardPoint point) {
        List<ChessboardPoint> availablePoints = new ArrayList<>();
        // 检查整张棋盘，用isValidMove()方法检查每个格子是否可以移动到，同时也用isValidCapture()方法检查每个格子是否可以吃掉
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 7; j++) {
                ChessboardPoint destPoint = new ChessboardPoint(i, j);
                if (isValidMove(point, destPoint) || isValidCapture(point, destPoint)) {
                    availablePoints.add(destPoint);
                }
            }
        }
        return availablePoints;
    }

    public List<ChessboardPoint> getValidPoints(PlayerColor color){
        List<ChessboardPoint> availablePoints = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 7; j++) {
                ChessboardPoint point = new ChessboardPoint(i, j);
                if (getChessPieceAt(point) != null && getChessPieceAt(point).getOwner() == color) {
                    availablePoints.add(point);
                }
            }
        }
        return availablePoints;
    }

    public List<Step> getValidSteps(PlayerColor color){
        List<Step> availableSteps = new ArrayList<>();
        List<ChessboardPoint> availablePoints = getValidPoints(color);
        for (ChessboardPoint point : availablePoints) {
            List<ChessboardPoint> validMoves = getValidMoves(point);
            for (ChessboardPoint destPoint : validMoves) {
                availableSteps.add(recordStep(point, destPoint, color, 0));
            }
        }
        return availableSteps;
    }

    public List<Step> getValidStepsWithValue(PlayerColor color){
        List<Step> availableSteps = new ArrayList<>();
        List<ChessboardPoint> availablePoints = getValidPoints(color);
        for (ChessboardPoint point : availablePoints) {
            List<ChessboardPoint> validMoves = getValidMoves(point);
            for (ChessboardPoint destPoint : validMoves) {
                Step step = recordStep(point, destPoint, color, 0);
                //越接近敌方基地，价值越高
                if (color == PlayerColor.RED) {
                    step.setValue(destPoint.getRow() - point.getRow() + Math.abs(3 - point.getCol()) - Math.abs(3 - destPoint.getCol()));
                } else {
                    step.setValue(point.getRow() - destPoint.getRow() + Math.abs(3 - point.getCol()) - Math.abs(3 - destPoint.getCol()));
                }
                if (getChessPieceAt(destPoint) != null) {
                    step.setValue(step.getValue() + (int) Math.pow(getChessPieceAt(destPoint).getRank(),2));
                }
                if (getGridAt(destPoint).getType() == GridType.DENS && getGridAt(destPoint).getOwner() != color) {
                    step.setValue(10000);
                }
                if (getGridAt(destPoint).getType() == GridType.TRAP && getGridAt(destPoint).getOwner() != color) {
                    //检查周围是否有敌人
                    if (getChessPieceAt(destPoint) != null) {
                        step.setValue((int) Math.pow(getChessPieceAt(destPoint).getRank(),2));
                    } else {
                        boolean hasEnemy = false;
                        for (int k = 0; k < 4; k++) {
                            ChessboardPoint neighborPoint = destPoint.getNeighbor(k);
                            if (neighborPoint.getRow()<0 || neighborPoint.getRow()>8 || neighborPoint.getCol()<0 || neighborPoint.getCol()>6) {
                                continue;
                            }
                            if (getChessPieceAt(neighborPoint) != null && getChessPieceAt(neighborPoint).getOwner() != color) {
                                step.setValue(-1);
                                hasEnemy = true;
                                break;
                            }
                        }
                        if (!hasEnemy) {
                            step.setValue(10000);
                        }
                    }
                }
                availableSteps.add(step);
            }
        }
        return availableSteps;
    }

    public void moveMusic(ChessboardPoint point){
        ChessPiece chessPiece = getChessPieceAt(point);
        MusicThread musicThread = null;

        if (chessPiece.getName().equals("Rat")){
            musicThread = new MusicThread(getClass().getResource("/music/Moiuse squeal 2.wav"), false);
        } else if (chessPiece.getName().equals("Cat")){
            musicThread = new MusicThread(getClass().getResource("/music/Cat meow 5.wav"), false);
        } else if (chessPiece.getName().equals("Dog")){
            musicThread = new MusicThread(getClass().getResource("/music/Dog bark 3.wav"), false);
        } else if (chessPiece.getName().equals("Wolf")){
            musicThread = new MusicThread(getClass().getResource("/music/Wolf barks 1.wav"), false);
        } else if (chessPiece.getName().equals("Leopard")){
            musicThread = new MusicThread(getClass().getResource("/music/Lion roar 3.wav"), false);
        } else if (chessPiece.getName().equals("Tiger")){
            musicThread = new MusicThread(getClass().getResource("/music/Tiger attack 1.wav"), false);
        } else if (chessPiece.getName().equals("Lion")){
            musicThread = new MusicThread(getClass().getResource("/music/Lion atacks, bites 5.wav"), false);
        } else if (chessPiece.getName().equals("Elephant")){
            musicThread = new MusicThread(getClass().getResource("/music/Elephant Trumpeting 1.wav"), false);
        }
        Thread music = new Thread(musicThread);
        music.start();
    }

}
