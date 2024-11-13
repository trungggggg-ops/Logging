package vn.vifo.logging.dto.response.log;

import org.springframework.data.domain.Page;
import vn.vifo.logging.dto.response.PaginationResponse;

import java.util.List;

public class LogsPaginationResponse extends PaginationResponse<LogResponse> {
    public LogsPaginationResponse(final Page<?> pageModel, final List<LogResponse> items) {
        super(pageModel, items);
    }
}
