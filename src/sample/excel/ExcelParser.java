package sample.excel;

import sample.entity.User;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelParser {

    private String filePath;

    public ExcelParser(String filePath) {
        this.filePath = filePath;
    }

    public List<User> getUsers() throws IOException {
        ArrayList<User> users = new ArrayList<>();
        XSSFWorkbook workbook = new XSSFWorkbook(filePath);
        XSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();
        rowIterator.forEachRemaining(cells -> {
            Iterator<Cell> cellIterator = cells.cellIterator();
            ArrayList<String> user = new ArrayList<>();
            cellIterator.forEachRemaining(row -> {
                String rowValue;
                switch (row.getCellType()){
                    case NUMERIC:
                        rowValue = String.valueOf((long) row.getNumericCellValue());
                        break;
                    case BOOLEAN:
                        rowValue = String.valueOf(row.getBooleanCellValue());
                        break;
                    default:
                        rowValue = row.getStringCellValue();
                        break;
                }
                user.add(rowValue);
            });
            User userI = new User(user.get(0), user.get(1), user.get(2), user.get(3), user.get(4), user.get(5));
            if (!userI.getEmail().isEmpty() && userI.getEmail() != null) {
                users.add(userI);
                System.out.println(userI.toString());
            }
        });
        return users;
    }
}
