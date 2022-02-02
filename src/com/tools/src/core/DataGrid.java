package core;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class DataGrid implements Serializable {

    private final int row;
    private final int col;
    private final int[] cLen;
    private final String[] cName;
    private final List<Object[]> data;

    public DataGrid(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int col = rsmd.getColumnCount();
        cName = new String[col];
        cLen = new int[col];
        for (int i = 0; i < col; i += 1) {
            cName[i] = rsmd.getColumnName(i + 1);
            cLen[i] = cName[i].length();
        }
        data = new ArrayList<>();
        while (rs.next()) {
            Object[] row = new Object[col];
            for (int i = 0; i < col; i += 1) {
                Object obj = rs.getObject(i + 1);
                row[i] = obj;
                if (obj.toString().length() > cLen[i]) {
                    cLen[i] = obj.toString().length();
                }
            }
            data.add(row);
        }
        this.row = data.size();
        this.col = col;
    }

    public int getRowCount() {
        return row;
    }

    public int getColCount() {
        return col;
    }

    public String getColName(int index) {
        return cName[index];
    }

    public Object getData(int i, int j) {
        return (data.get(i))[j];
    }

    public void printData() {
        printData(" | ");
    }

    public void printData(String sep) {
        for (int i = 0; i < col; i += 1) {
            System.out.print(getColName(i));
            if (i != col - 1) {
                System.out.print(sep);
            }
        }
        System.out.println();
        for (int i = 0; i < row; i += 1) {
            for (int j = 0; j < col; j += 1) {
                System.out.print(getData(i, j));
                if (j != col - 1) {
                    System.out.print(sep);
                }
            }
            System.out.println();
        }
    }

    public void printDataFormatted() {
        System.out.print("-+-");
        for (int j = 0; j < col; j += 1) {
            for (int k = 0; k < cLen[j]; k += 1) {
                System.out.print("-");
            }
            System.out.print("-+-");
        }
        System.out.println();
        System.out.print(" | ");
        for (int j = 0; j < col; j += 1) {
            System.out.printf("%" + cLen[j] + "s", getColName(j));
            System.out.print(" | ");
        }
        System.out.println();
        System.out.print("-+-");
        for (int j = 0; j < col; j += 1) {
            for (int k = 0; k < cLen[j]; k += 1) {
                System.out.print("-");
            }
            System.out.print("-+-");
        }
        System.out.println();
        for (int i = 0; i < row; i += 1) {
            System.out.print(" | ");
            for (int j = 0; j < col; j += 1) {
                System.out.printf("%" + cLen[j] + "s", getData(i, j).toString());
                System.out.print(" | ");
            }
            System.out.println();
        }
        System.out.print("-+-");
        for (int j = 0; j < col; j += 1) {
            for (int k = 0; k < cLen[j]; k += 1) {
                System.out.print("-");
            }
            System.out.print("-+-");
        }
    }

}
