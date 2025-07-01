package kr.me.seesaw.core.excel;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import java.util.Map;

public class ExcelXlsxView extends AbstractXlsxView {

    private final ExcelWriter excelWriter;

    public ExcelXlsxView(ExcelWriter excelWriter) {
        this.excelWriter = excelWriter;
    }


    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
        excelWriter.setWorkbook(workbook);
        excelWriter.setData(model);
        excelWriter.setResponse(response);
        excelWriter.create();
    }

}
