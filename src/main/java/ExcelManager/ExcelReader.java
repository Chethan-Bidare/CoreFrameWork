package ExcelManager;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Excel Reader class
 * Created By : Chethan K Bidare
 * Created On : 29-06-19
 */
public class ExcelReader {
    private final String path;
    private XSSFWorkbook workbook;

    public ExcelReader(final String path){
        this.path = path;
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            workbook = new XSSFWorkbook(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the File Exists in Specified Path
     * @return - Boolean
     */
    private boolean checkIfFileExists(final String excelName) {
        boolean isFileNameExists =true;
        File file = new File(path);
        if (!file.exists()){
            isFileNameExists = false;
        }
        if (!path.contains(excelName)){
            isFileNameExists = false;
        }
        return isFileNameExists;
    }

    /**
     *  Gets the data from the Specified Sheet
     *  @param excelName - Excel Name
     *  @param sheetName - Sheet Name
     *
     *  @return String[][]
     */
    public String[][] getDataFromSheet(final String excelName, final String sheetName) throws Exception {
        String[][] dataSets ;
        if (checkIfFileExists(excelName)){
            XSSFSheet sheet = workbook.getSheet(sheetName);
            int totalRows = sheet.getLastRowNum()+1;
            int totalCols = sheet.getRow(0).getLastCellNum();
            dataSets = new String[totalRows-1][totalCols];

            for (int i=1; i<totalRows; i++){
                XSSFRow row = sheet.getRow(i);
                for (int j=0; j<totalCols; j++){
                    XSSFCell cell = row.getCell(j);
                    if (cell.getCellType()== CellType.BOOLEAN){
                        dataSets[i-1][j]= String.valueOf(cell.getBooleanCellValue());
                    }
                    else if (cell.getCellType()==CellType.STRING){
                        dataSets[i-1][j] = cell.getStringCellValue();
                    }
                    else if (cell.getCellType()==CellType.NUMERIC){
                        dataSets[i-1][j]=String.valueOf(cell.getNumericCellValue());
                    }
                }
            }
        }
        else {
            throw new Exception("File Not Found in the specified Path : "+excelName);
        }
        return dataSets;
    }
}