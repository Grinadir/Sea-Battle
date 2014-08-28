package app.model;

import java.util.Arrays;

/**
 * Representation of ship health
 */
enum ShipState {
    LIVE, WOUNDED, DESTROYED
}

/**
 * Representation of ship status
 */
enum ShipStatus {
    AVAILABLE, BUSY
}

/**
 * Representation of ship size
 */
enum ShipSize {
    ONE(1), TWO(2), THREE(3), FOUR(4);

    private final int value;

    private ShipSize(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }
}

/**
 * Represents direction of ship
 */
enum ShipDirection {
    HORISONTAL, VERTICAL
}

/**
 * Class represents model and service functions of the Ship
 */
public class Ship {

    private ShipDirection shipDirection;

    private ShipSize shipSize;

    private ShipState shipState;

    private ShipStatus shipStatus;

    private Cell[] cells;

    /**
     * Constructor_1 with specified ship size
     *
     * @param shipSize - the length of the ship
     */
    public Ship(ShipSize shipSize) {
        this.shipSize = shipSize;
        this.cells = new Cell[shipSize.getValue()];
        this.shipStatus = ShipStatus.AVAILABLE;
    }

    /**
     * Constructor_2 with specified array of coordinates
     *
     * @param cells - array with coordinates
     */
    public Ship(Cell[] cells) {
        this.shipSize = getShipSizeFromCellsLength(cells);
        this.cells = cells;
        this.shipStatus = ShipStatus.AVAILABLE;
    }

    /**
     * Constructor_3 with specified array of coordinates and ship status
     *
     * @param cells  - array with coordinates
     * @param status - status of the ship
     */
    public Ship(Cell[] cells, ShipStatus status) {
        this.shipSize = getShipSizeFromCellsLength(cells);
        this.cells = cells;
        this.shipStatus = status;
        this.shipState = initiateShipState(status);
    }

    /**
     * Initiating Live status of ship if ship state is BUSY
     *
     * @param shipStatus - status of the ship
     * @return Ship State
     */
    public ShipState initiateShipState(ShipStatus shipStatus) {
        return shipStatus.equals(ShipStatus.BUSY) ? ShipState.LIVE : null;
    }

    /**
     * Initiating Size of the ship by counting Cell array size
     *
     * @param cells - cells of the ship
     * @return ShipSize
     */
    public ShipSize getShipSizeFromCellsLength(Cell[] cells) {
        ShipSize shipSize = null;
        if (cells.length > 0 && cells.length < 5) {
            switch (cells.length) {
                case 4:
                    shipSize = ShipSize.FOUR;
                    break;
                case 3:
                    shipSize = ShipSize.THREE;
                    break;
                case 2:
                    shipSize = ShipSize.TWO;
                    break;
                case 1:
                    shipSize = ShipSize.ONE;
                    break;
                default:
                    shipSize = null;
            }
        }
        return shipSize;
    }

    /**
     * Return ship state if ship is BUSY (have been placed to the field)
     *
     * @return - ShipState
     */
    public ShipState getShipState() {
        if (shipStatus.equals(ShipStatus.BUSY)) {
            int count = 0;
            for (Cell cell : cells) if (cell.getCellState() == CellState.HIT) count++;

            if (count > 0 && count == cells.length) {
                shipState = ShipState.DESTROYED;
                return shipState;
            } else if (count > 0 && count < cells.length) {
                shipState = ShipState.WOUNDED;
                return shipState;
            } else {
                shipState = ShipState.LIVE;
                return shipState;
            }
        }
        return null;
    }

    /**
     * TODO * + test
     * verifying that cells, where ship to be placed, are free
     *
     * @return boolean
     */
    public boolean isShipSpotIsFree(Field field) {
        if (cells.length > 0) {
            for (Cell[] fieldCellsLine : field.getFieldGrid()) {
                for (Cell fieldCellColumn : fieldCellsLine) {
                    for (Cell shipCell : cells) {
                        if (shipCell.equals(fieldCellColumn)) return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * TODO *  + test
     *
     * @param ship
     * @return
     */
    public Cell[] getCellsAroundShip(Ship ship) {

        Cell[] aroundShip = new Cell[(shipSize.getValue() + 2) * 3];
        return null;
    }

    public ShipDirection getShipDirection() {
        return shipDirection;
    }

    public void setShipDirection(ShipDirection shipDirection) {
        this.shipDirection = shipDirection;
    }

    public ShipSize getShipSize() {
        return shipSize;
    }

    public ShipStatus getShipStatus() {
        return shipStatus;
    }

    public void setShipStatus(ShipStatus shipStatus) {
        this.shipStatus = shipStatus;
    }

    public Cell[] getCells() {
        return cells;
    }

    public void setCells(Cell[] cells) {
        this.cells = cells;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ship)) return false;

        Ship ship = (Ship) o;

        if (!Arrays.equals(cells, ship.cells)) return false;
        if (shipSize != ship.shipSize) return false;
        if (shipState != ship.shipState) return false;
        if (shipStatus != ship.shipStatus) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = shipSize != null ? shipSize.hashCode() : 0;
        result = 31 * result + (shipState != null ? shipState.hashCode() : 0);
        result = 31 * result + (shipStatus != null ? shipStatus.hashCode() : 0);
        result = 31 * result + (cells != null ? Arrays.hashCode(cells) : 0);
        return result;
    }
}