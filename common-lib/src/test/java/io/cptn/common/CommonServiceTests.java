package io.cptn.common;

import io.cptn.common.services.CommonService;
import io.cptn.common.web.ListEntitiesParam;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/* @author: kc, created on 4/3/23 */
class CommonServiceTests {
    @Test
    void getPageableTest1() {
        FakeService service = new FakeService();

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        ListEntitiesParam param = new ListEntitiesParam(request);

        Pageable pageable = service.getPageable(param);
        assertEquals(0, pageable.getPageNumber());
        assertEquals(15, pageable.getPageSize());

        List<Sort.Order> list = pageable.getSort().toList();
        assertEquals(1, list.size());
        assertEquals("createdAt", list.get(0).getProperty());
        assertEquals("DESC", list.get(0).getDirection().toString());
    }

    @Test
    void getPageableTest2() {
        FakeService service = new FakeService();

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getParameter("page")).thenReturn("1");
        Mockito.when(request.getParameter("size")).thenReturn("10");
        Mockito.when(request.getParameter("sortBy")).thenReturn("name");
        Mockito.when(request.getParameter("asc")).thenReturn("true");


        ListEntitiesParam param = new ListEntitiesParam(request);


        Pageable pageable = service.getPageable(param);
        assertEquals(1, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());

        List<Sort.Order> list = pageable.getSort().toList();
        assertEquals(1, list.size());
        assertEquals("name", list.get(0).getProperty());
        assertEquals("ASC", list.get(0).getDirection().toString());
    }

    class FakeService extends CommonService {
        @Override
        public Pageable getPageable(ListEntitiesParam param) {
            return super.getPageable(param);
        }
    }
}
