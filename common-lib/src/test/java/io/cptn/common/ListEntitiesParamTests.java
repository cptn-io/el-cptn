package io.cptn.common;

import io.cptn.common.web.ListEntitiesParam;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

/* @author: kc, created on 4/3/23 */
@ExtendWith(MockitoExtension.class)
class ListEntitiesParamTests {

    @Mock
    HttpServletRequest request;

    @Test
    void listEntitiesParamTest() {
        Mockito.when(request.getParameter("page")).thenReturn(null);
        Mockito.when(request.getParameter("size")).thenReturn(null);
        Mockito.when(request.getParameter("sortBy")).thenReturn(null);
        Mockito.when(request.getParameter("asc")).thenReturn(null);

        ListEntitiesParam param = new ListEntitiesParam(request);
        assertEquals(0, param.getPage());
        assertEquals(15, param.getSize());
        assertEquals("createdAt", param.getSortBy()[0]);
        assertEquals(false, param.isSortAsc());
    }

    @Test
    void listEntitiesParamAllValuesSetTest() {
        Mockito.when(request.getParameter("page")).thenReturn("1");
        Mockito.when(request.getParameter("size")).thenReturn("10");
        Mockito.when(request.getParameter("sortBy")).thenReturn("name");
        Mockito.when(request.getParameter("asc")).thenReturn("true");

        ListEntitiesParam param = new ListEntitiesParam(request);
        assertEquals(1, param.getPage());
        assertEquals(10, param.getSize());
        assertEquals("name", param.getSortBy()[0]);
        assertEquals(true, param.isSortAsc());
    }

    @Test
    void boundaryConditionsTest1() {
        Mockito.when(request.getParameter("page")).thenReturn("-1");
        Mockito.when(request.getParameter("size")).thenReturn("0");
        Mockito.when(request.getParameter("sortBy")).thenReturn("name,createdAt");
        Mockito.when(request.getParameter("asc")).thenReturn("false");

        ListEntitiesParam param = new ListEntitiesParam(request);
        assertEquals(0, param.getPage());
        assertEquals(15, param.getSize());
        assertEquals("name", param.getSortBy()[0]);
        assertEquals("createdAt", param.getSortBy()[1]);
        assertEquals(false, param.isSortAsc());
    }

    @Test
    void boundaryConditionsTest2() {
        Mockito.when(request.getParameter("page")).thenReturn("1");
        Mockito.when(request.getParameter("size")).thenReturn("101");
        Mockito.when(request.getParameter("sortBy")).thenReturn("name,createdAt");
        Mockito.when(request.getParameter("asc")).thenReturn("someRandomValue");

        ListEntitiesParam param = new ListEntitiesParam(request);
        assertEquals(1, param.getPage());
        assertEquals(15, param.getSize());
        assertEquals(false, param.isSortAsc());
    }
}
